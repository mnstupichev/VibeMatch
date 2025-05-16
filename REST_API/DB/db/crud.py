from sqlalchemy.orm import Session
import models, schemas
from typing import Optional, Dict, Any, List
import logging

# Настройка логирования
logger = logging.getLogger(__name__)

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
    try:
        logger.debug(f"Attempting to toggle like for event_id: {event_id}, user_id: {user_id}")
        
        # Проверяем существование события
        event = get_event_by_id(db, event_id)
        if not event:
            logger.warning(f"Event not found with id: {event_id}")
            raise ValueError("Event not found")
            
        # Проверяем существование пользователя
        user = get_user_by_id(db, user_id)
        if not user:
            logger.warning(f"User not found with id: {user_id}")
            raise ValueError("User not found")
            
        logger.debug(f"Found event: {event.title} and user: {user.username}")
        
        # Проверяем существование лайка
        existing_like = db.query(models.EventLike).filter(
            models.EventLike.event_id == event_id,
            models.EventLike.user_id == user_id
        ).first()
        
        if existing_like:
            # Если лайк существует - удаляем его
            logger.debug(f"Removing existing like for event_id: {event_id}, user_id: {user_id}")
            db.delete(existing_like)
            db.commit()
            return None
        else:
            # Если лайка нет - создаем новый
            logger.debug(f"Creating new like for event_id: {event_id}, user_id: {user_id}")
            new_like = models.EventLike(event_id=event_id, user_id=user_id)
            db.add(new_like)
            db.commit()
            db.refresh(new_like)
            return new_like
            
    except ValueError as e:
        logger.error(f"Validation error in toggle_event_like: {str(e)}")
        raise
    except Exception as e:
        logger.error(f"Database error in toggle_event_like: {str(e)}", exc_info=True)
        db.rollback()
        raise ValueError(f"Database error while toggling like: {str(e)}")

def get_event_likes(db: Session, event_id: int) -> List[models.EventLike]:
    """Получить все лайки события"""
    return db.query(models.EventLike).filter(
        models.EventLike.event_id == event_id
    ).all()

def get_user_liked_events(db: Session, user_id: int) -> List[models.Event]:
    """Получить все события, которые лайкнул пользователь"""
    try:
        logger.debug(f"Getting liked events for user_id: {user_id}")
        
        # Проверяем существование пользователя
        user = get_user_by_id(db, user_id)
        if not user:
            logger.warning(f"User not found with id: {user_id}")
            raise ValueError("User not found")
        
        logger.debug(f"User found: {user.username}")

        # Проверяем наличие лайков
        likes_count = db.query(models.EventLike).filter(
            models.EventLike.user_id == user_id
        ).count()
        logger.debug(f"Found {likes_count} likes for user")

        # Получаем список лайкнутых событий
        try:
            liked_events = db.query(models.Event).join(
                models.EventLike,
                models.Event.event_id == models.EventLike.event_id
            ).filter(
                models.EventLike.user_id == user_id,
                models.Event.is_active == True
            ).all()
            
            logger.debug(f"Successfully retrieved {len(liked_events)} liked events")
            return liked_events
            
        except Exception as db_error:
            logger.error(f"Database error during event query: {str(db_error)}")
            raise ValueError(f"Database error while fetching liked events: {str(db_error)}")
            
    except ValueError as e:
        # Пробрасываем ошибку валидации дальше
        logger.error(f"Validation error: {str(e)}")
        raise
    except Exception as e:
        # Логируем ошибку и пробрасываем её дальше
        logger.error(f"Unexpected error in get_user_liked_events: {str(e)}", exc_info=True)
        raise ValueError(f"Database error while fetching liked events: {str(e)}")

def get_event_participants_with_users(db: Session, event_id: int):
    participants = db.query(models.EventParticipant).filter(models.EventParticipant.event_id == event_id).all()
    result = []
    for p in participants:
        user = get_user_by_id(db, p.user_id)
        result.append({
            "event_id": p.event_id,
            "user_id": p.user_id,
            "status": p.status,
            "registered_at": p.registered_at,
            "user": user
        })
    return result 