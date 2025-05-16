# VibeMatch

VibeMatch is a modern social event discovery and participation platform that helps users find and join events that match their interests. The platform enables users to create profiles, discover events, like events they're interested in, and join events they want to attend.

## 🌟 Features

### Android App
- **User Authentication**
  - Secure login and registration
  - Profile management with customizable fields
  - Logout functionality

- **Profile Management**
  - Complete user profiles with:
    - Username
    - Age
    - Gender
    - Bio
    - City
    - Telegram contact
  - Profile editing with validation
  - Profile viewing for both own and other users' profiles

- **Event Discovery**
  - Browse available events
  - View event details
  - Like/unlike events
  - Join events as a participant
  - View event participants
  - See who liked an event

- **Modern UI/UX**
  - Clean and intuitive interface
  - Responsive design
  - Profile icons and images
  - Interactive like buttons
  - Form validation
  - Proper navigation flow

### Backend (FastAPI)
- **RESTful API**
  - User management endpoints
  - Event management endpoints
  - Like/unlike functionality
  - Event participation system
  - User profile operations

- **Database**
  - SQLite database with SQLAlchemy ORM
  - Proper schema design
  - Efficient queries
  - Data validation

- **Testing**
  - Comprehensive test suite
  - Unit tests for CRUD operations
  - API endpoint tests
  - Test fixtures and utilities

## 🛠 Technical Stack

### Android
- Kotlin
- Android Jetpack Components
- MVVM Architecture
- ViewBinding
- Retrofit for API calls
- Unit Testing with JUnit

### Backend
- Python 3.x
- FastAPI
- SQLAlchemy
- Pydantic
- SQLite
- Pytest for testing

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version)
- Python 3.x
- pip (Python package manager)

### Backend Setup
1. Navigate to the REST_API directory:
   ```bash
   cd REST_API
   ```
2. Create and activate virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```
3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Run the server:
   ```bash
   uvicorn db.main:app --reload
   ```

### Android Setup
1. Open the project in Android Studio
2. Sync project with Gradle files
3. Build and run the application

## 📱 Current Development Status

### In Progress
- iOS client development
- Enhanced event discovery features
- Real-time notifications
- Chat functionality between event participants
- Event recommendations based on user preferences
- Location-based event filtering
- Event categories and tags
- User ratings and reviews
- Event sharing functionality

### Planned Improvements
- Push notifications
- Offline mode support
- Image upload for profiles and events
- Social media integration
- Advanced search and filtering
- Event analytics for organizers
- User blocking and reporting system
- Multi-language support

## 🧪 Testing

### Backend Tests
Run the test suite:
```bash
cd REST_API/DB
pytest
```

### Android Tests
Run tests through Android Studio:
- Unit tests
- UI tests
- Integration tests

## 📝 API Documentation

The API documentation is available at:
- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👥 Authors

- Stupuchev Mikhail - Backend and Android developer
- Levchuk Sofia - Android developer 
- Ivanova Aaya - IOS developer 

