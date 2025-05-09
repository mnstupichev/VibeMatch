from datetime import datetime
from typing import Optional
from pydantic import BaseModel, EmailStr  # EmailStr для валидации email

# Базовые схемы для валидации данных

class UserBase(BaseModel):
    """Базовые поля пользователя (для создания и чтения)"""
    email: EmailStr  # Валидация формата email
    username: str

class UserCreate(UserBase):
    """Схема для создания пользователя (добавляем пароль)"""
    password: str  # Пароль будет хешироваться перед сохранением

class User(UserBase):
    """Полная схема пользователя (для ответов API)"""
    user_id: int
    gender: Optional[str] = None  # Все поля необязательные
    city: Optional[str] = None
    telegram_link: Optional[str] = None
    speciality: Optional[str] = None
    profile_picture_url: Optional[str] = None
    bio: Optional[str] = None
    is_active: bool  # Флаг активности

    class Config:
        orm_mode = True  # Позволяет использовать с SQLAlchemy моделями

class EventBase(BaseModel):
    """Базовые поля события"""
    title: str
    description: Optional[str] = None
    location: str
    start_time: datetime
    end_time: Optional[datetime] = None

class EventCreate(EventBase):
    """Схема для создания события (пока без изменений)"""
    pass

class Event(EventBase):
    """Полная схема события (для ответов API)"""
    event_id: int
    created_at: datetime
    is_active: bool

    class Config:
        orm_mode = True

class EventParticipantBase(BaseModel):
    """Базовые поля участника события"""
    status: str  # "interested", "going", "not_going"

class EventParticipantCreate(EventParticipantBase):
    """Схема для создания связи пользователь-событие"""
    event_id: int
    user_id: int

class EventParticipant(EventParticipantBase):
    """Полная схема участника события"""
    event_id: int
    user_id: int
    registered_at: datetime

    class Config:
        orm_mode = True