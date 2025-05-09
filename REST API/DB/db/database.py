from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# URL подключения к PostgreSQL (формат: postgresql://user:password@host/dbname)
SQLALCHEMY_DATABASE_URL = "postgresql://postgres:postgres@db:5432/friend_finder"

# Создание движка SQLAlchemy
engine = create_engine(SQLALCHEMY_DATABASE_URL)

# Фабрика сессий для работы с БД
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Базовый класс для моделей
Base = declarative_base()

# Генератор сессий для Dependency Injection в FastAPI
def get_db():
    db = SessionLocal()
    try:
        yield db  # Возвращаем сессию для использования в route
    finally:
        db.close()  # Гарантированное закрытие сессии после использования