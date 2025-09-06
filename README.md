# Online store

Витрина интернет-магазина на основе Spring Boot, H2 и JPA.

### 🚀 Как запустить проект на Windows (cmd)

#### 📦 Требования
- Java 21+
- Git
- Docker

#### 📁 Запуск

1. Клонируй ветку main из репозитория используя git bash
   ```bash
   git clone --branch main --single-branch https://github.com/AnonUserG/sprint5_onlineStore_boot.git
2. Запусти на машине Docker
3. Перейди в папку с проектом
4. Собери проект (тесты прогонятся автоматически)
   ```bash
   gradlew clean build
5. Собери docker image
   ```bash
   docker build -t online-store .
6. Запусти контейнер
   ```bash
   docker run -p 8080:8080 online-store

7. Перейди в браузере на [http://localhost:8080/](http://localhost:8080/)
