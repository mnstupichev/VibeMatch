from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
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