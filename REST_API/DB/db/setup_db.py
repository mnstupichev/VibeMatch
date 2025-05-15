import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from database import engine, Base, DB_USER, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME
from models import User, Event, EventParticipant, EventLike
import logging

# Настройка логирования
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def create_database():
    """Создает базу данных, если она не существует"""
    try:
        # Подключаемся к postgres для создания базы данных
        conn = psycopg2.connect(
            database='postgres',
            user=DB_USER,
            password=DB_PASSWORD,
            host=DB_HOST,
            port=DB_PORT,
            client_encoding='utf8'
        )
        conn.autocommit = True
        cur = conn.cursor()
        
        # Проверяем существование базы данных
        cur.execute("SELECT 1 FROM pg_database WHERE datname = %s", (DB_NAME,))
        exists = cur.fetchone()
        
        if not exists:
            logger.info(f"Database {DB_NAME} does not exist, creating...")
            # Закрываем все соединения с базой данных
            cur.execute("""
                SELECT pg_terminate_backend(pg_stat_activity.pid)
                FROM pg_stat_activity
                WHERE pg_stat_activity.datname = %s
                AND pid <> pg_backend_pid()
            """, (DB_NAME,))
            # Создаем базу данных
            cur.execute(f"CREATE DATABASE {DB_NAME} ENCODING = 'UTF8'")
            logger.info(f"Database {DB_NAME} created successfully!")
        else:
            logger.info(f"Database {DB_NAME} already exists")
        
        cur.close()
        conn.close()
        
    except Exception as e:
        logger.error(f"Error creating database: {e}")
        raise

def init_tables():
    """Создает таблицы в базе данных"""
    try:
        logger.info("Creating database tables...")
        Base.metadata.create_all(bind=engine)
        logger.info("Database tables created successfully!")
    except Exception as e:
        logger.error(f"Error creating tables: {e}")
        raise

if __name__ == "__main__":
    logger.info("=== Database Setup ===")
    try:
        create_database()
        init_tables()
        logger.info("\n=== Setup completed successfully! ===")
    except Exception as e:
        logger.error(f"\n=== Setup failed: {e} ===")
        exit(1) 