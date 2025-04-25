@app.post("/users/", response_model=schemas.User)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    """
    Создание нового пользователя
    Args:
        user: Данные из тела запроса (автоматически валидируются схемой UserCreate)
        db: Сессия БД (автоматически внедряется)
    Returns:
        Созданный пользователь (автоматически конвертируется в схему User)
    Raises:
        HTTPException: Если email уже занят
    """
    try:
        return crud.create_user(db=db, user=user)
    except ValueError as e:
        # Преобразование ошибки в HTTP-ответ
        raise HTTPException(
            status_code=400,
            detail=str(e)
        )