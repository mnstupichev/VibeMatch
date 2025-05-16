from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List, Optional
import crud, models, schemas
from database import get_db
import logging

from test_utils import router as test_utils_router
from schemas import EventParticipantWithUser

app = FastAPI(title="VibeMatch API")

# Настройка логирования
logger = logging.getLogger(__name__)

# Root endpoint
@app.get("/")
def read_root():
    return {"message": "Welcome to VibeMatch API"}

# Users endpoints
@app.get("/users/", response_model=List[schemas.User])
def get_users(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """Get list of all users"""
    try:
        users = crud.get_users(db, skip=skip, limit=limit)
        return users
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/users/{user_id}/", response_model=schemas.User)
def get_user(user_id: int, db: Session = Depends(get_db)):
    """Get user by ID"""
    user = crud.get_user_by_id(db, user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

@app.get("/users/{user_id}/events/", response_model=List[schemas.EventParticipant])
def get_user_events(user_id: int, db: Session = Depends(get_db)):
    """Get events for specific user"""
    try:
        return crud.get_user_events(db, user_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Events endpoints
@app.get("/events/", response_model=List[schemas.Event])
def get_events(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """Get list of all active events"""
    try:
        events = crud.get_all_events(db, skip=skip, limit=limit)
        return events
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/events/{event_id}/", response_model=schemas.Event)
def get_event(event_id: int, db: Session = Depends(get_db)):
    """Get event by ID"""
    event = crud.get_event_by_id(db, event_id)
    if not event:
        raise HTTPException(status_code=404, detail="Event not found")
    return event

@app.get("/events/{event_id}/participants/", response_model=List[schemas.EventParticipant])
def get_event_participants(event_id: int, db: Session = Depends(get_db)):
    """Get participants for specific event"""
    try:
        return crud.get_event_participants(db, event_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/events/{event_id}/join/", response_model=schemas.EventParticipant)
def join_event(event_id: int, user_id: int, status: str = "interested", db: Session = Depends(get_db)):
    """Join an event"""
    try:
        return crud.join_event(db, event_id, user_id, status)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/users/", response_model=schemas.User)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    """
    Создание нового пользователя
    Args:
        user: Данные из тела запроса (автоматически валидируются схемой UserCreate)
        db: Сессия БД (автоматически внедряется)
    Returns:
        Созданный пользователь (автоматически конвертируется в схему User)
    Raises:
        HTTPException: Если email уже занят
    """
    try:
        return crud.create_user(db=db, user=user)
    except ValueError as e:
        # Преобразование ошибки в HTTP-ответ
        raise HTTPException(
            status_code=400,
            detail=str(e)
        )

@app.put("/users/{user_id}/", response_model=schemas.User)
def update_user_profile(
    user_id: int,
    user_update: schemas.UserUpdate,
    db: Session = Depends(get_db)
):
    """
    Обновляет профиль пользователя
    Args:
        user_id: ID пользователя
        user_update: Данные для обновления
        db: Сессия базы данных
    Returns:
        Обновленный пользователь
    Raises:
        HTTPException: Если пользователь не найден или произошла ошибка при обновлении
    """
    try:
        # Преобразуем Pydantic модель в словарь, исключая None значения
        update_data = user_update.dict(exclude_unset=True)
        
        # Обновляем пользователя
        updated_user = crud.update_user(db, user_id, update_data)
        if not updated_user:
            raise HTTPException(status_code=404, detail="User not found")
            
        return updated_user
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/events/", response_model=schemas.Event)
def create_event(event: schemas.EventCreate, db: Session = Depends(get_db)):
    """Create a new event"""
    try:
        return crud.create_event(db, event)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/events/{event_id}/like/", response_model=Optional[schemas.EventLike])
def toggle_event_like(event_id: int, user_id: int, db: Session = Depends(get_db)):
    """Поставить или убрать лайк событию"""
    try:
        logger.debug(f"Received like toggle request for event_id: {event_id}, user_id: {user_id}")
        result = crud.toggle_event_like(db, event_id, user_id)
        
        if result is None:
            logger.info(f"Like removed for event_id: {event_id}, user_id: {user_id}")
            return {"message": "Like removed successfully"}
        else:
            logger.info(f"Like added for event_id: {event_id}, user_id: {user_id}")
            return result
            
    except ValueError as e:
        error_msg = str(e)
        logger.error(f"Error toggling like: {error_msg}")
        
        if "Event not found" in error_msg:
            raise HTTPException(status_code=404, detail="Event not found")
        elif "User not found" in error_msg:
            raise HTTPException(status_code=404, detail="User not found")
        elif "Database error" in error_msg:
            logger.error(f"Database error details: {error_msg}", exc_info=True)
            raise HTTPException(
                status_code=500,
                detail="Database error occurred while toggling like"
            )
        else:
            raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error in toggle_event_like endpoint: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail="An unexpected error occurred while toggling like"
        )

@app.get("/events/{event_id}/likes/", response_model=List[schemas.EventLike])
def get_event_likes(event_id: int, db: Session = Depends(get_db)):
    """Получить все лайки события"""
    try:
        return crud.get_event_likes(db, event_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/users/{user_id}/liked-events/", response_model=List[schemas.Event])
def get_user_liked_events(user_id: int, db: Session = Depends(get_db)):
    """Получить список событий, которые лайкнул пользователь"""
    try:
        logger.debug(f"Received request for liked events of user {user_id}")
        events = crud.get_user_liked_events(db, user_id)
        logger.debug(f"Successfully retrieved {len(events)} liked events for user {user_id}")
        return events
    except ValueError as e:
        error_msg = str(e)
        logger.error(f"Error getting liked events for user {user_id}: {error_msg}")
        
        if "User not found" in error_msg:
            raise HTTPException(status_code=404, detail="User not found")
        elif "Database error" in error_msg:
            logger.error(f"Database error details: {error_msg}", exc_info=True)
            raise HTTPException(
                status_code=500,
                detail="Database error occurred while fetching liked events"
            )
        else:
            raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error in get_user_liked_events endpoint: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=500,
            detail="An unexpected error occurred while fetching liked events"
        )

@app.get("/events/{event_id}/participants_with_users/", response_model=List[EventParticipantWithUser])
def get_event_participants_with_users(event_id: int, db: Session = Depends(get_db)):
    return crud.get_event_participants_with_users(db, event_id)

@app.get("/events/{event_id}/likes_with_users/", response_model=List[schemas.User])
def get_event_likes_with_users(event_id: int, db: Session = Depends(get_db)):
    likes = db.query(models.EventLike).filter(models.EventLike.event_id == event_id).all()
    user_ids = [like.user_id for like in likes]
    if not user_ids:
        return []
    users = db.query(models.User).filter(models.User.user_id.in_(user_ids)).all()
    return users

app.include_router(test_utils_router)