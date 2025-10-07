# Online store

Витрина интернет-магазина на основе Spring Boot, Redis, reactive stack, payment service.

### 🚀 Как запустить проект на Windows (cmd)

#### 📦 Требования
- Git
- Docker
- Порты 8080, 8081, 6379 - должны быть свободны


#### 📁 Запуск
!!! VPN может помешать подняться проекту через docker compose

1. Клонируй ветку multi-project-payments из репозитория используя git bash
   ```bash
   git clone --branch multi-project-payments --single-branch https://github.com/AnonUserG/sprint5_onlineStore_boot.git
2. Запусти на машине Docker
3. Перейди в папку с проектом
4. Подними проект используя (тесты прогонятся автоматически)
   ```bash
   docker compose up --build ИЛИ docker-compose up --build

5. Перейди в браузере на [http://localhost:8080/](http://localhost:8080/)