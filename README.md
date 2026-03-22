# My Bank App

Микросервисное приложение «Банк» на Spring Boot и Spring Cloud.

## Функциональность

- Редактирование данных аккаунта (ФИО, дата рождения)
- Пополнение и снятие виртуальных денег со счёта
- Перевод виртуальных денег на счёт другого клиента
- Уведомления о выполненных операциях

## Архитектура

```
User -> Front UI (8080) -> Gateway (8081) -> { Accounts, Cash, Transfer }

Cash -> Accounts (через ZooKeeper discovery)
Cash -> Notifications
Transfer -> Accounts
Transfer -> Notifications
Accounts -> Notifications
```

## Сервисы

| Сервис | Описание |
|--------|----------|
| front-ui | Веб-интерфейс (Thymeleaf), OAuth2 клиент |
| gateway-service | API Gateway (Spring Cloud Gateway MVC) |
| accounts-service | Управление аккаунтами и счетами |
| cash-service | Пополнение и снятие денег |
| transfer-service | Переводы между счетами |
| notification-service | Уведомления |
| Keycloak | Identity Provider |
| PostgreSQL | База данных |
| ZooKeeper | Service Discovery и конфигурация |

## Технологии

- Java 21, Spring Boot 4.0.3, Spring Cloud 2025.1.1 (Oakwood)
- Spring MVC, Spring Data JPA, Hibernate
- PostgreSQL (schema-per-service: `accounts`, `notifications`)
- ZooKeeper (Service Discovery + Distributed Config)
- Spring Cloud Gateway MVC
- Keycloak (OAuth 2.0: Authorization Code для UI, Client Credentials для сервисов)
- Resilience4j (Circuit Breaker)
- Spring Cloud Contract (контрактные тесты)
- Docker + Docker Compose

## Запуск через Docker Compose

### Требования

- Docker и Docker Compose
- Java 21 и Maven (для сборки)

### Сборка и запуск

```bash
# Сборка всех модулей
mvn clean package -DskipTests

# Запуск всего стека
docker compose up --build
```

Откройте http://localhost:8080 — произойдёт редирект на страницу входа Keycloak.

### Тестовый пользователь

| Логин | Пароль |
|-------|--------|
| ivanov@test.com | password |

## Тестирование

```bash
# Все тесты (unit + integration + contract)
mvn clean test
```

### Виды тестов

- **Модульные тесты**: сервисный слой с замоканными зависимостями
- **Интеграционные тесты**: полный Spring-контекст + Testcontainers (PostgreSQL)
- **Контрактные тесты**: Spring Cloud Contract
  - Producer: `accounts-service` (3 контракта)
  - Consumers: `cash-service`, `transfer-service` (stub-runner)

