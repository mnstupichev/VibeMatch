import uvicorn
import os

if __name__ == "__main__":
    # Получаем текущую директорию
    current_dir = os.path.dirname(os.path.abspath(__file__))
    
    # Переходим в директорию с main.py
    os.chdir(current_dir)
    
    # Запускаем сервер
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True
    ) 