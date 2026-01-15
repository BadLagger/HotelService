### Hotel Microservices Project ###

Микросервисная система для управления отелями и бронированиями с JWT-аутентификацией и ролевой моделью.

## Архитектура: ##
```
┌─────────────────────────────────────────────┐
│            API Gateway (9999)               │
│       Единая точка входа для всех запросов  │
└────────────────┬────────────────────────────┘
                 │
    ┌────────────┼─────────────────────┐
    │            ▼                     │
┌───▼────┐  ┌──────────┐  ┌──────────┐│
│Eureka  │  │Booking   │  │Hotel     ││
│(9996)  │  │(9998)    │  │(9997)    ││
└────────┘  └──────────┘  └──────────┘│
    ▲            ▲                     │
    └────────────┴─────────────────────┘
          Service Discovery
```
## Технологический стек ##
+ Java 22
+ Spring Boot 3.5.6
+ Spring Security + JWT
+ Spring Data JPA + H2 (in-memory)
+ Eureka Service Discovery
+ Spring Cloud Gateway
+ Feign Client для межсервисного взаимодействия
+ Maven Multi-module

## Структура проекта ##
```
hotel-microservices/
├── service-discovery/     # Eureka Server (порт: 9996)
├── api-gateway/          # Spring Cloud Gateway (порт: 9999)
├── booking-service/      # Сервис бронирований (порт: 9998)
├── hotel-service/        # Сервис отелей (порт: 9997)
└── pom.xml              # Родительский конфиг
```
## Аутентификация и безопасность ##
+ JWT токены с сроком действия 1 час
+ Роли: USER и ADMIN
+ Каждый сервис самостоятельно проверяет JWT (Resource Server)
+ Gateway - единая публичная точка входа

## Дефолтные пользователи: ##
+ ADMIN: admin/admin123

## Конфигурация переменных окружения ###
JWT_SECRET	- Секрет для JWT
EUREKA_USERNAME	- Логин для Eureka (для каждого проекта отдельно)
EUREKA_PASSWORD	- Пароль для Eureka (для каждого проекта отдельно)
JWT_EXPIRATION - Время жизни токена (мс)

##  Hotel Service (порт: 9997) ##

### Управление отелями: ###
+ GET /hotels - список всех отелей (USER, ADMIN)
+ POST /hotels - создать отель (только ADMIN)
+ GET /status - статус сервиса (публичный)

### Управление номерами: ###
+ GET /rooms - все доступные номера (USER, ADMIN)
+ POST /rooms - добавить номер (только ADMIN)
+ GET /rooms/recommend - рекомендованные номера, отсортированные по кол-ву заказов (USER)
+ GET /rooms/hotel/{hotelId}/available - список доступных номеров в отеле(USER)

### Для межсервисного взаимодействия: ###
+ POST /internal/rooms//{roomId}/confirm-availability - проверка номера на доступность
+ POST /internal/rooms/{hotelId}/find-available - возвращает список доступных номеров в отеле
+ POST /internal/rooms//{roomId}/booked - увеличивает счётчик заказов номера

## Booking Service (порт: 9998) ##

+ GET /status - статус сервиса (публичный)

### Аутентификация/Регистрация: ###
+ POST /user/register - регистрация пользователя (публичный)
+ POST /user/auth - авторизация (публичный)
+ GET /user - список всех пользователей (ADMIN)
+ POST /user - создать пользователя (ADMIN)
+ DELETE /user/{id} - удалить пользователя (ADMIN)
+ POST /bookings - создать бронирование (USER) (при autoSelect: true номер выбирается автоматически)
+ GET /bookings - история бронирований пользователя (USER)
+ GET /bookings/{id} - получить бронирование по ID (USER)
+ DELETE /bookings/{id} - отменить бронирование (USER)

## Маршрутизация через Gateway ##
Все запросы идут через Gateway (порт 9999):
+ http://localhost:9999/api/hotels/** → Hotel Service
+ http://localhost:9999/api/rooms/** → Hotel Service
+ http://localhost:9999/api/bookings/** → Booking Service

## Запуск проекта ##
### Способ 1: Запуск через IDE ###
Запустить в порядке:
+ ServiceDiscoveryApplication (порт 9996)
+ HotelServiceApplication (порт 9997)
+ BookingServiceApplication (порт 9998)
+ ApiGatewayApplication (порт 9999)
Проверить регистрацию в Eureka.
### Способ 2: Через Maven ###
```
# Собрать все модули
mvn clean install

# Запустить каждый сервис отдельно
cd service-discovery && mvn spring-boot:run
cd hotel-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## Базы данных ##
### Hotel Service (H2): ###
+ URL: http://localhost:9997/h2-console
+ JDBC URL: jdbc:h2:mem:hotelsdb
+ User: sa
+ Password: (пусто)

## Booking Service (H2): ##
+ URL: http://localhost:9998/h2-console
+ JDBC URL: jdbc:h2:mem:bookingdb
+ User: sa
+ Password: (пусто)

### Тестирование через Postman ###
Все основные функции для проверки сохранены в директории POSTMAN. Для использования необходимо импортировать в Postman.

### Особенности реализации ###
Независимые сервисы - каждый сервис имеет свою БД
JWT в claims - все данные пользователя хранятся в токене
Межсервисная коммуникация через Feign Client
Автоматическая регистрация в Eureka
StripPrefix в Gateway для чистых URL
Ролевая модель с @PreAuthorize аннотациями

##  Возможные улучшения ##
+ Добавить PostgreSQL вместо H2
+ Добавить Docker контейнеризацию
+ Настроить centralized configuration (Config Server)
+ Добавить распределенное логирование (ELK)
+ Реализовать пагинацию и фильтрацию
+ Добавить кэширование (Redis)
+ Добавить интеграционные тесты



