## 🧩 Описание проекта

**Film Parser** — это Java-приложение, предназначенное для массового парсинга фильмов с Кинопоиска.

### 🔍 Ключевые особенности

- Парсинг фильмов с сайта [Кинопоиск](https://www.kinopoisk.ru/lists/movies/?b=films)
- Использование **Chromium headless** для динамической загрузки страниц
- Хранение данных в PostgreSQL
- Конфигурация через `application.yml` и переменные окружения
- Логгирование количества сохранённых фильмов
- Параметр `movieCount` задаёт, сколько фильмов парсить (по умолчанию 1000)
- Параметр `startPage` задаёт, с какой страницы начать парсинг (по умолчанию 1)

---

## ⚙️ Технологии

| Категория       | Технология                  |
|------------------|----------------------------|
| Язык             | Java 21                    |
| Фреймворк        | Spring Boot 3.5            |
| Парсинг          | Jsoup + Chromium (headless) |
| База данных      | PostgreSQL                 |
| ORM              | Spring Data JPA + Hibernate |
| Docker           | Многослойная сборка, CDS   |
| Логгирование     | SLF4J               |

---

## 🐳 Поддержка Docker Compose

В проекте есть файл `compose.yaml` для удобного запуска всех необходимых сервисов:

### Описание сервисов

- **postgres** — контейнер с PostgreSQL, хранит базу данных `kinopoisk`
    - Используется образ `postgres:latest`
    - Переменные окружения:
        - `POSTGRES_DB=kinopoisk`
        - `POSTGRES_USER=postgres`
        - `POSTGRES_PASSWORD=postgres`
    - Проброшен порт `5432:5432`
    - Данные сохраняются в volume `postgres_data`
    - Настроен healthcheck с использованием `pg_isready`

- **film-parser** — приложение для парсинга фильмов
    - Используется локальный билд из Dockerfile (тег `film-parser:latest`)
    - Переменные окружения:
        - `SPRING_PROFILES_ACTIVE=dev`
        - `DB_HOST=postgres`
        - `DB_USER=postgres`
        - `DB_PASSWORD=postgres`
        - `MOVIE_COUNT=1000`
        - `START_PAGE=1`
    - Проброшен порт `8080:8080`
    - Зависит от сервиса `postgres` (`depends_on`)

### Запуск

```bash
docker-compose up --build
```
## 📂 Экспортированные данные
[Скачать JSON с фильмами](./export/movies.json)
