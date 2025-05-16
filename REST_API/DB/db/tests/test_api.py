def test_create_user_api(client):
    response = client.post("/users/", json={
        "email": "apiuser@test.com",
        "username": "apiuser",
        "password": "pass"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["username"] == "apiuser"

def test_update_user_api(client):
    # create user
    response = client.post("/users/", json={
        "email": "apiupdate@test.com",
        "username": "apiupdate",
        "password": "pass"
    })
    user_id = response.json()["user_id"]
    # update user
    response = client.put(f"/users/{user_id}/", json={"city": "API City"})
    assert response.status_code == 200
    assert response.json()["city"] == "API City"

def test_create_event_api(client):
    from datetime import datetime
    response = client.post("/events/", json={
        "title": "API Event",
        "description": "API Desc",
        "location": "API Loc",
        "start_time": datetime.now().isoformat()
    })
    assert response.status_code == 200
    assert response.json()["title"] == "API Event"

def test_toggle_event_like_api(client):
    from datetime import datetime
    # create user and event
    user_resp = client.post("/users/", json={"email": "likeapi@test.com", "username": "likeapi", "password": "pass"})
    user_id = user_resp.json()["user_id"]
    event_resp = client.post("/events/", json={"title": "Like API Event", "description": "", "location": "Loc", "start_time": datetime.now().isoformat()})
    event_id = event_resp.json()["event_id"]
    # like
    like_resp = client.post(f"/events/{event_id}/like/?user_id={user_id}")
    assert like_resp.status_code == 200
    # unlike
    like_resp2 = client.post(f"/events/{event_id}/like/?user_id={user_id}")
    assert like_resp2.status_code == 200

def test_join_event_api(client):
    from datetime import datetime
    user_resp = client.post("/users/", json={"email": "joinapi@test.com", "username": "joinapi", "password": "pass"})
    user_id = user_resp.json()["user_id"]
    event_resp = client.post("/events/", json={"title": "Join API Event", "description": "", "location": "Loc", "start_time": datetime.now().isoformat()})
    event_id = event_resp.json()["event_id"]
    join_resp = client.post(f"/events/{event_id}/join/?user_id={user_id}")
    assert join_resp.status_code == 200
    # get participants
    part_resp = client.get(f"/events/{event_id}/participants/")
    assert part_resp.status_code == 200
    assert any(p["user_id"] == user_id for p in part_resp.json())

def test_get_event_likes_with_users_api(client):
    from datetime import datetime
    user_resp = client.post("/users/", json={"email": "likeuserapi@test.com", "username": "likeuserapi", "password": "pass"})
    user_id = user_resp.json()["user_id"]
    event_resp = client.post("/events/", json={"title": "Like Users API Event", "description": "", "location": "Loc", "start_time": datetime.now().isoformat()})
    event_id = event_resp.json()["event_id"]
    client.post(f"/events/{event_id}/like/?user_id={user_id}")
    likes_users_resp = client.get(f"/events/{event_id}/likes_with_users/")
    assert likes_users_resp.status_code == 200
    assert any(u["user_id"] == user_id for u in likes_users_resp.json())

def test_create_user_duplicate_email_api(client):
    resp1 = client.post("/users/", json={"email": "dupapi@test.com", "username": "dupapi", "password": "pass"})
    assert resp1.status_code == 200
    resp2 = client.post("/users/", json={"email": "dupapi@test.com", "username": "dupapi2", "password": "pass"})
    assert resp2.status_code == 400 