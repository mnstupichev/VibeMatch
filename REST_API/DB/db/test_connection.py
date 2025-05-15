import psycopg2
import os
from dotenv import load_dotenv
import logging
import binascii
import sys

# Настройка логирования
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def analyze_string(s, name):
    """Анализирует строку на наличие проблемных символов"""
    logger.debug(f"\nAnalyzing {name}:")
    logger.debug(f"String value: {s}")
    logger.debug(f"Length: {len(s)}")
    logger.debug(f"Bytes: {binascii.hexlify(s.encode('utf-8', errors='replace'))}")
    try:
        s.encode('utf-8')
        logger.debug("✓ Valid UTF-8")
    except UnicodeEncodeError as e:
        logger.debug(f"✗ Invalid UTF-8: {e}")

def test_connection():
    # Загружаем переменные окружения
    load_dotenv()
    
    # Получаем параметры подключения
    DB_USER = os.getenv("DB_USER", "postgres")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "1727131323")
    DB_HOST = os.getenv("DB_HOST", "localhost")
    DB_PORT = int(os.getenv("DB_PORT", "5432"))
    DB_NAME = os.getenv("DB_NAME", "friend_finder")
    
    # Анализируем каждый параметр
    analyze_string(DB_USER, "DB_USER")
    analyze_string(DB_PASSWORD, "DB_PASSWORD")
    analyze_string(DB_HOST, "DB_HOST")
    analyze_string(str(DB_PORT), "DB_PORT")
    analyze_string(DB_NAME, "DB_NAME")
    
    try:
        # Пробуем подключиться с минимальными параметрами
        # Сначала пробуем подключиться к postgres
        logger.info("Trying to connect to postgres database...")
        conn = psycopg2.connect(
            database='postgres',
            user=DB_USER,
            password=DB_PASSWORD,
            host=DB_HOST,
            port=DB_PORT,
            client_encoding='utf8'
        )
        conn.autocommit = True  # Важно для CREATE DATABASE
        logger.info("Successfully connected to postgres database!")
        
        # Проверяем существование нашей базы данных
        cur = conn.cursor()
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
        
        cur.close()
        conn.close()
        
        # Теперь пробуем подключиться к нашей базе данных
        logger.info(f"Trying to connect to {DB_NAME} database...")
        conn = psycopg2.connect(
            database=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD,
            host=DB_HOST,
            port=DB_PORT,
            client_encoding='utf8'
        )
        logger.info(f"Successfully connected to {DB_NAME} database!")
        conn.close()
        return True
        
    except Exception as e:
        logger.error(f"Connection error: {str(e)}")
        logger.error(f"Error type: {type(e)}")
        logger.error(f"Error args: {e.args}")
        if hasattr(e, '__cause__'):
            logger.error(f"Caused by: {e.__cause__}")
        return False

if __name__ == "__main__":
    try:
        test_connection()
    except Exception as e:
        logger.error(f"Unexpected error: {str(e)}")
        logger.error(f"Error type: {type(e)}")
        logger.error(f"Error args: {e.args}")
        if hasattr(e, '__cause__'):
            logger.error(f"Caused by: {e.__cause__}")
        sys.exit(1) 