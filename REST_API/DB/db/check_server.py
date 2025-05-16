import requests
import sys
from database import SessionLocal
from models import User, Event, EventLike

def check_database():
    print("Проверка подключения к базе данных...")
    db = SessionLocal()
    try:
        # Проверяем таблицу пользователей
        users_count = db.query(User).count()
        print(f"✓ База данных доступна")
        print(f"✓ Количество пользователей: {users_count}")
        
        # Выводим данные каждого пользователя
        users = db.query(User).all()
        for user in users:
            print(f"\nПользователь: {user.user_id} | {user.username} | {user.email}")
            print(f"  Город: {user.city}")
            print(f"  Пол: {user.gender}")
            print(f"  Telegram: {user.telegram_link}")
            print(f"  Bio: {user.bio}")
            print(f"  Возраст: {user.age}")
            # Найти лайкнутые события
            liked_events = db.query(EventLike).filter(EventLike.user_id == user.user_id).all()
            liked_event_ids = [like.event_id for like in liked_events]
            print(f"  Лайкнутые события (event_id): {liked_event_ids}")
        
        # Проверяем таблицу событий
        events_count = db.query(Event).count()
        print(f"✓ Количество событий: {events_count}")
        return True
    except Exception as e:
        print(f"✗ Ошибка при подключении к базе данных: {e}")
        return False
    finally:
        db.close()

def check_api():
    print("\nПроверка API endpoints...")
    base_url = "http://localhost:8000"
    
    # Проверяем доступность сервера
    try:
        response = requests.get(f"{base_url}/docs")
        if response.status_code == 200:
            print("✓ API документация доступна")
        else:
            print(f"✗ API документация недоступна (код {response.status_code})")
            return False
    except requests.exceptions.ConnectionError:
        print("✗ Сервер недоступен. Убедитесь, что он запущен на порту 8000")
        return False
    
    # Проверяем основные endpoints
    endpoints = [
        "/events/",
        "/users/",
    ]
    
    for endpoint in endpoints:
        try:
            response = requests.get(f"{base_url}{endpoint}")
            print(f"✓ {endpoint} - {response.status_code}")
        except Exception as e:
            print(f"✗ {endpoint} - ошибка: {e}")
            return False
    
    return True

if __name__ == "__main__":
    print("=== Проверка работоспособности сервера ===\n")
    
    db_ok = check_database()
    api_ok = check_api()
    
    print("\n=== Результаты проверки ===")
    print(f"База данных: {'✓ OK' if db_ok else '✗ Ошибка'}")
    print(f"API сервер: {'✓ OK' if api_ok else '✗ Ошибка'}")
    
    if not (db_ok and api_ok):
        sys.exit(1) 