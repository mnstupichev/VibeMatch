from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List, Optional
import crud, models, schemas
from database import get_db

app = FastAPI(title="VibeMatch API")

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
        return crud.toggle_event_like(db, event_id, user_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/events/{event_id}/likes/", response_model=List[schemas.EventLike])
def get_event_likes(event_id: int, db: Session = Depends(get_db)):
    """Получить все лайки события"""
    try:
        return crud.get_event_likes(db, event_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/users/{user_id}/liked-events/", response_model=List[schemas.Event])
def get_user_liked_events(user_id: int, db: Session = Depends(get_db)):
    """Получить все события, которые лайкнул пользователь"""
    try:
        return crud.get_user_liked_events(db, user_id)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))