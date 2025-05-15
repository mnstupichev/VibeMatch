from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os
from dotenv import load_dotenv
import logging
import locale

# Настройка логирования
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

# Устанавливаем локаль для корректной обработки русских сообщений
try:
    locale.setlocale(locale.LC_ALL, 'ru_RU.UTF-8')
except locale.Error:
    try:
        locale.setlocale(locale.LC_ALL, 'Russian_Russia.UTF-8')
    except locale.Error:
        logger.warning("Could not set Russian locale, using default")

# Загружаем переменные окружения из .env файла
load_dotenv()

# Получаем параметры подключения
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "1727131323")
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "5432"))
DB_NAME = os.getenv("DB_NAME", "friend_finder")

# Формируем URL подключения
SQLALCHEMY_DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

# Создание движка SQLAlchemy с минимальными параметрами
engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    connect_args={
        "client_encoding": "utf8"
    },
    pool_pre_ping=True,
    pool_recycle=3600,
    echo=False
)

# Фабрика сессий для работы с БД
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Базовый класс для моделей
Base = declarative_base()

# Генератор сессий для Dependency Injection в FastAPI
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()