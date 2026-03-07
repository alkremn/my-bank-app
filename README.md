# My Bank App

Микросервисное приложение «Банк» на Spring Boot и Spring Cloud.

## Функциональность

- Редактирование данных аккаунта (ФИО, дата рождения)
- Пополнение и снятие виртуальных денег со счёта
- Перевод виртуальных денег на счёт другого клиента
- Уведомления о выполненных операциях

## Сервисы

| Сервис | Порт | Описание |
|--------|------|----------|
| front-ui | 8080 | Веб-интерфейс (Thymeleaf) |
| gateway-service | 8081 | API Gateway (Spring Cloud Gateway) |
| accounts-service | 8082 | Управление аккаунтами и счетами |
| cash-service | 8083 | Пополнение и снятие денег |
| transfer-service | 8084 | Переводы между счетами |
| notification-service | 8085 | Уведомления |

## Технологии

- Java 21, Spring Boot 4, Spring Cloud 2025.1
- Spring Data JPA + PostgreSQL
- ZooKeeper (Service Discovery, Distributed Config)
- Keycloak (OAuth 2.0)
- Docker, Docker Compose
