from sqlalchemy.orm import Session
import models, schemas
from typing import Optional, Dict, Any, List

# Создание пользователя

def create_user(db: Session, user: schemas.UserCreate):
    db_user = get_user_by_email(db, email=user.email)
    if db_user:
        raise ValueError("Email already registered")
    fake_hashed_password = user.password + "_notreallyhashed"
    db_user = models.User(
        email=user.email,
        password_hash=fake_hashed_password,
        username=user.username
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

# Получить пользователя по email

def get_user_by_email(db: Session, email: str):
    return db.query(models.User).filter(models.User.email == email).first()

# Получить пользователя по id

def get_user_by_id(db: Session, user_id: int):
    return db.query(models.User).filter(models.User.user_id == user_id).first()

# Получить всех пользователей по городу

def get_users_by_city(db: Session, city: str, skip: int = 0, limit: int = 100):
    return db.query(models.User).filter(models.User.city == city).offset(skip).limit(limit).all()

# Получить всех пользователей

def get_users(db: Session, skip: int = 0, limit: int = 100):
    """Get list of users with pagination"""
    return db.query(models.User).offset(skip).limit(limit).all()

# Получить все события

def get_all_events(db: Session, skip: int = 0, limit: int = 100):
    """Get list of active events with pagination"""
    return db.query(models.Event).filter(models.Event.is_active == True).offset(skip).limit(limit).all()

# Получить событие по id

def get_event_by_id(db: Session, event_id: int):
    return db.query(models.Event).filter(models.Event.event_id == event_id).first()

# Получить участников события

def get_event_participants(db: Session, event_id: int):
    return db.query(models.EventParticipant).filter(models.EventParticipant.event_id == event_id).all()

# Получить события пользователя

def get_user_events(db: Session, user_id: int):
    return db.query(models.EventParticipant).filter(models.EventParticipant.user_id == user_id).all()

# Присоединить пользователя к событию

def join_event(db: Session, event_id: int, user_id: int, status: str = "interested"):
    participant = models.EventParticipant(event_id=event_id, user_id=user_id, status=status)
    db.add(participant)
    db.commit()
    db.refresh(participant)
    return participant

def update_user(db: Session, user_id: int, update_data: Dict[str, Any]) -> Optional[models.User]:
    """
    Обновляет данные пользователя
    Args:
        db: Сессия базы данных
        user_id: ID пользователя
        update_data: Словарь с полями для обновления
    Returns:
        Обновленный пользователь или None, если пользователь не найден
    """
    db_user = get_user_by_id(db, user_id)
    if not db_user:
        return None
        
    # Обновляем только те поля, которые переданы в update_data
    for field, value in update_data.items():
        if hasattr(db_user, field) and value is not None:
            setattr(db_user, field, value)
    
    try:
        db.commit()
        db.refresh(db_user)
        return db_user
    except Exception as e:
        db.rollback()
        raise e

def create_event(db: Session, event: schemas.EventCreate) -> models.Event:
    """Create a new event"""
    db_event = models.Event(
        title=event.title,
        description=event.description,
        location=event.location,
        start_time=event.start_time,
        end_time=event.end_time
    )
    db.add(db_event)
    db.commit()
    db.refresh(db_event)
    return db_event

def toggle_event_like(db: Session, event_id: int, user_id: int) -> Optional[models.EventLike]:
    """Поставить или убрать лайк событию"""
    # Проверяем существование лайка
    existing_like = db.query(models.EventLike).filter(
        models.EventLike.event_id == event_id,
        models.EventLike.user_id == user_id
    ).first()
    
    if existing_like:
        # Если лайк существует - удаляем его
        db.delete(existing_like)
        db.commit()
        return None
    else:
        # Если лайка нет - создаем новый
        new_like = models.EventLike(event_id=event_id, user_id=user_id)
        db.add(new_like)
        db.commit()
        db.refresh(new_like)
        return new_like

def get_event_likes(db: Session, event_id: int) -> List[models.EventLike]:
    """Получить все лайки события"""
    return db.query(models.EventLike).filter(
        models.EventLike.event_id == event_id
    ).all()

def get_user_liked_events(db: Session, user_id: int) -> List[models.Event]:
    """Получить все события, которые лайкнул пользователь"""
    return db.query(models.Event).join(
        models.EventLike,
        models.Event.event_id == models.EventLike.event_id
    ).filter(
        models.EventLike.user_id == user_id
    ).all() 