# My Bank App

Микросервисное приложение «Банк» на Spring Boot и Spring Cloud.

## Функциональность

- Редактирование данных аккаунта (ФИО, дата рождения)
- Пополнение и снятие виртуальных денег со счёта
- Перевод виртуальных денег на счёт другого клиента
- Уведомления о выполненных операциях

## Архитектура

```
User -> Front UI -> { Accounts, Cash, Transfer, Notification }

Cash -> Accounts (прямой вызов по DNS)
Cash -> Notifications
Transfer -> Accounts
Transfer -> Notifications
Accounts -> Notifications
```

## Сервисы

| Сервис | Описание |
|--------|----------|
| front-ui | Веб-интерфейс (Thymeleaf), OAuth2 клиент |
| accounts-service | Управление аккаунтами и счетами |
| cash-service | Пополнение и снятие денег |
| transfer-service | Переводы между счетами |
| notification-service | Уведомления |
| Keycloak | Identity Provider |
| PostgreSQL | База данных |

## Технологии

- Java 21, Spring Boot 4.0.3, Spring Cloud 2025.1.1 (Oakwood)
- Spring MVC, Spring Data JPA, Hibernate
- PostgreSQL (schema-per-service: `accounts`, `notifications`)
- Keycloak (OAuth 2.0: Authorization Code для UI, Client Credentials для сервисов)
- Resilience4j (Circuit Breaker)
- Spring Cloud Contract (контрактные тесты)
- Docker + Docker Compose
- Kubernetes + Helm

## Вариант 1: Docker Compose

### Требования

- Docker и Docker Compose

### Сборка и запуск

```bash
docker compose up --build
```

Откройте http://localhost:8080 — произойдёт редирект на страницу входа Keycloak.

## Вариант 2: Kubernetes (Helm)

### Требования

- Docker Desktop с включённым Kubernetes
- Helm 3
- NGINX Ingress Controller

### Установка Ingress Controller (один раз)

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/cloud/deploy.yaml
```

### Сборка и деплой

```bash
# Сборка Docker-образов (доступны в K8s через Docker Desktop)
docker compose build

# Установка Helm-чарта
helm install my-bank helm/my-bank-app

# Port-forward для Keycloak (NodePort не доступен напрямую в Docker Desktop)
kubectl port-forward svc/keycloak 30180:8180

# Проверка подов
kubectl get pods

# Запуск Helm-тестов
helm test my-bank
```

### Доступ

- **Приложение**: http://localhost/
- **Keycloak**: http://localhost:30180

## Тестовый пользователь

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
