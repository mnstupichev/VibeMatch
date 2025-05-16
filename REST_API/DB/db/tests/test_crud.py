from db import crud, schemas
from datetime import datetime

def test_create_and_get_user(db):
    user_create = schemas.UserCreate(email="test@crud.com", username="cruduser", password="pass")
    user = crud.create_user(db, user_create)
    assert user.email == "test@crud.com"
    assert user.username == "cruduser"
    fetched = crud.get_user_by_id(db, user.user_id)
    assert fetched.email == "test@crud.com"

def test_update_user(db):
    user_create = schemas.UserCreate(email="update@crud.com", username="updateuser", password="pass")
    user = crud.create_user(db, user_create)
    crud.update_user(db, user.user_id, {"city": "TestCity", "bio": "Test bio"})
    updated = crud.get_user_by_id(db, user.user_id)
    assert updated.city == "TestCity"
    assert updated.bio == "Test bio"

def test_create_and_get_event(db):
    event_create = schemas.EventCreate(
        title="Test Event",
        description="Desc",
        location="TestLoc",
        start_time=datetime.now()
    )
    event = crud.create_event(db, event_create)
    assert event.title == "Test Event"
    fetched = crud.get_event_by_id(db, event.event_id)
    assert fetched.title == "Test Event"

def test_toggle_event_like(db):
    user = crud.create_user(db, schemas.UserCreate(email="like@crud.com", username="likeuser", password="pass"))
    event = crud.create_event(db, schemas.EventCreate(title="Like Event", description="", location="Loc", start_time=datetime.now()))
    like = crud.toggle_event_like(db, event.event_id, user.user_id)
    assert like is not None
    # Unlike
    like2 = crud.toggle_event_like(db, event.event_id, user.user_id)
    assert like2 is None

def test_join_event_and_get_participants(db):
    user = crud.create_user(db, schemas.UserCreate(email="join@crud.com", username="joinuser", password="pass"))
    event = crud.create_event(db, schemas.EventCreate(title="Join Event", description="", location="Loc", start_time=datetime.now()))
    participant = crud.join_event(db, event.event_id, user.user_id)
    assert participant is not None
    participants = crud.get_event_participants(db, event.event_id)
    assert any(p.user_id == user.user_id for p in participants)

def test_get_event_likes_with_users(db):
    user = crud.create_user(db, schemas.UserCreate(email="likeuser2@crud.com", username="likeuser2", password="pass"))
    event = crud.create_event(db, schemas.EventCreate(title="Like Event2", description="", location="Loc", start_time=datetime.now()))
    crud.toggle_event_like(db, event.event_id, user.user_id)
    users = db.query(crud.models.User).filter(crud.models.User.user_id == user.user_id).all()
    assert len(users) == 1

def test_create_user_duplicate_email(db):
    user_create = schemas.UserCreate(email="dup@crud.com", username="dupuser", password="pass")
    crud.create_user(db, user_create)
    try:
        crud.create_user(db, user_create)
        assert False, "Should raise ValueError for duplicate email"
    except ValueError:
        assert True 