from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
import crud, schemas
from database import get_db

router = APIRouter()

@router.post("/test/add_users_who_like_first_event/")
def add_users_who_like_first_event(db: Session = Depends(get_db)):
    """
    Создаёт 5 пользователей и ставит лайк каждому к первому событию
    """
    # 1. Создать 5 пользователей (если их нет)
    users = []
    for i in range(1, 6):
        email = f'user{i}@example.com'
        username = f'user{i}'
        city = f'City{i}'
        gender = 'Мужской' if i % 2 == 0 else 'Женский'
        telegram_link = f'@user{i}_tg'
        bio = f'Тестовый пользователь {i}'
        age = 20 + i
        try:
            user = crud.get_user_by_email(db, email)
            if not user:
                user = crud.create_user(db, schemas.UserCreate(email=email, username=username, password='password'))
                # Обновить профиль дополнительной информацией
                crud.update_user(db, user.user_id, {
                    'city': city,
                    'gender': gender,
                    'telegram_link': telegram_link,
                    'bio': bio,
                    'age': age
                })
            users.append(user)
        except Exception as e:
            continue
    # 2. Найти первое событие
    event = None
    events = crud.get_all_events(db, skip=0, limit=1)
    if events:
        event = events[0]
    if not event:
        raise HTTPException(status_code=404, detail="No events found")
    # 3. Поставить лайк от каждого пользователя
    for user in users:
        try:
            crud.toggle_event_like(db, event.event_id, user.user_id)
        except Exception as e:
            continue
