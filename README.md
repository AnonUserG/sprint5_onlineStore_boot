# Online store

Витрина интернет-магазина на основе Spring Boot, reactive stack.

### 🚀 Как запустить проект на Windows (cmd)

#### 📦 Требования
- Git
- Docker

#### 📁 Запуск

1. Клонируй ветку reactive_stack из репозитория используя git bash
   ```bash
   git clone --branch reactive_stack --single-branch https://github.com/AnonUserG/sprint5_onlineStore_boot.git
2. Запусти на машине Docker
3. Перейди в папку с проектом
4. Собери multistage docker image (тесты прогонятся автоматически)
   ```bash
   docker build -t online-store .
5. Запусти контейнер
   ```bash
   docker run -p 8080:8080 online-store

6. Перейди в браузере на [http://localhost:8080/](http://localhost:8080/)
