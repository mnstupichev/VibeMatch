from sqlalchemy.orm import Session
import models, schemas

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