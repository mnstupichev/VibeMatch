def create_user(db: Session, user: schemas.UserCreate):
    """
    Создание нового пользователя в БД
    Args:
        db: Сессия SQLAlchemy
        user: Данные пользователя из запроса (схема UserCreate)
    Returns:
        Созданный пользователь (SQLAlchemy модель)
    """
    # Проверка на существующего пользователя
    db_user = get_user_by_email(db, email=user.email)
    if db_user:
        raise ValueError("Email already registered")

    # В реальном приложении здесь должно быть хеширование пароля!
    fake_hashed_password = user.password + "_notreallyhashed"

    # Создание объекта модели
    db_user = models.User(
        email=user.email,
        password_hash=fake_hashed_password,
        username=user.username
    )

    # Добавление в сессию
    db.add(db_user)
    # Фиксация транзакции
    db.commit()
    # Обновление объекта (получение ID)
    db.refresh(db_user)

    return db_user


def get_users_by_city(db: Session, city: str, skip: int = 0, limit: int = 100):
    """
    Получение пользователей по городу с пагинацией
    Args:
        city: Город для фильтрации
        skip: Сколько записей пропустить (для пагинации)
        limit: Максимальное количество записей
    """
    return db.query(models.User).filter(
        models.User.city == city  # Фильтр по городу
    ).offset(skip).limit(limit).all()  # Пагинация