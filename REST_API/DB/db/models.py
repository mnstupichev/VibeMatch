from sqlalchemy import Column, Integer, String, Boolean, Text, DateTime, ForeignKey
from sqlalchemy.sql import func  # Для SQL-функций типа NOW()
from database import Base


class User(Base):
    """Модель пользователя"""
    __tablename__ = "users"  # Название таблицы в БД

    # Основные поля
    user_id = Column(Integer, primary_key=True, index=True)  # Первичный ключ
    email = Column(String(255), unique=True, index=True, nullable=False)
    password_hash = Column(String(255), nullable=False)  # Хеш пароля
    username = Column(String(100), nullable=False)  # Уникальное имя пользователя

    # Дополнительные поля профиля
    gender = Column(String(20))  # Пол (может быть NULL)
    city = Column(String(100))  # Город проживания
    telegram_link = Column(String(100))  # Ссылка на Telegram
    speciality = Column(String(100))  # Специальность/профессия

    # Медиа и описание
    profile_picture_url = Column(String(255))  # URL аватарки
    bio = Column(Text)  # Описание профиля

    # Служебные поля
    created_at = Column(DateTime(timezone=True), server_default=func.now())  # Автоматическая установка времени создания
    updated_at = Column(DateTime(timezone=True), server_default=func.now(),
                        onupdate=func.now())  # Автоматическое обновление
    is_active = Column(Boolean, default=True)  # Флаг активности пользователя


class Event(Base):
    """Модель события"""
    __tablename__ = "events"

    event_id = Column(Integer, primary_key=True, index=True)
    title = Column(String(255), nullable=False)  # Название события
    description = Column(Text)  # Подробное описание
    location = Column(String(255), nullable=False)  # Место проведения

    # Временные метки
    start_time = Column(DateTime(timezone=True), nullable=False)  # Обязательное поле
    end_time = Column(DateTime(timezone=True))  # Необязательное поле
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    is_active = Column(Boolean, default=True)  # Активно ли событие


class EventParticipant(Base):
    """Модель связи пользователей и событий (многие-ко-многим)"""
    __tablename__ = "event_participants"

    # Составной первичный ключ
    event_id = Column(Integer, ForeignKey("events.event_id"), primary_key=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), primary_key=True)

    status = Column(String(20), default="interested")  # Статус участия
    registered_at = Column(DateTime(timezone=True), server_default=func.now())  # Время регистрации


class EventLike(Base):
    """Модель лайков событий"""
    __tablename__ = "event_likes"

    # Составной первичный ключ
    event_id = Column(Integer, ForeignKey("events.event_id"), primary_key=True)
    user_id = Column(Integer, ForeignKey("users.user_id"), primary_key=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now())