from datetime import datetime, timedelta
import requests
import json

BASE_URL = "http://localhost:8000"

# Список событий для создания
events = [
    {
        "title": "Hackathon VibeMatch 2025",
        "description": "Ежегодный хакатон для разработчиков и дизайнеров. Создавайте инновационные проекты, находите единомышленников и выигрывайте призы!",
        "location": "Москва, ул. Ленина 10, Коворкинг 'Инновации'",
        "start_time": (datetime.now() + timedelta(days=7)).isoformat(),
        "end_time": (datetime.now() + timedelta(days=9)).isoformat()
    },
    {
        "title": "Встреча IT-сообщества",
        "description": "Еженедельная встреча разработчиков для обмена опытом, обсуждения новых технологий и нетворкинга.",
        "location": "Санкт-Петербург, Невский пр. 20, Коворкинг 'Технологии'",
        "start_time": (datetime.now() + timedelta(days=3)).isoformat(),
        "end_time": (datetime.now() + timedelta(days=3, hours=3)).isoformat()
    },
    {
        "title": "Мастер-класс по Flutter",
        "description": "Практический мастер-класс по разработке мобильных приложений на Flutter. От основ до продвинутых тем.",
        "location": "Казань, ул. Баумана 5, IT-Академия",
        "start_time": (datetime.now() + timedelta(days=5)).isoformat(),
        "end_time": (datetime.now() + timedelta(days=5, hours=4)).isoformat()
    },
    {
        "title": "Конференция 'Будущее AI'",
        "description": "Международная конференция о последних достижениях в области искусственного интеллекта и машинного обучения.",
        "location": "Москва, ул. Тверская 15, Конгресс-центр",
        "start_time": (datetime.now() + timedelta(days=14)).isoformat(),
        "end_time": (datetime.now() + timedelta(days=16)).isoformat()
    },
    {
        "title": "Воркшоп по UI/UX дизайну",
        "description": "Практический воркшоп по созданию современных пользовательских интерфейсов. От прототипирования до реализации.",
        "location": "Екатеринбург, пр. Ленина 30, Дизайн-студия 'Креатив'",
        "start_time": (datetime.now() + timedelta(days=10)).isoformat(),
        "end_time": (datetime.now() + timedelta(days=10, hours=6)).isoformat()
    }
]

def create_events():
    """Создание событий через API"""
    for event in events:
        try:
            response = requests.post(
                f"{BASE_URL}/events/",
                json=event,
                headers={"Content-Type": "application/json"}
            )
            if response.status_code == 200:
                print(f"Событие '{event['title']}' успешно создано!")
            else:
                print(f"Ошибка при создании события '{event['title']}': {response.text}")
        except Exception as e:
            print(f"Ошибка при создании события '{event['title']}': {str(e)}")

if __name__ == "__main__":
    create_events() 