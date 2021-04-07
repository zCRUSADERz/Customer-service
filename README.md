[![Build Status](https://www.travis-ci.com/zCRUSADERz/Customer-service.svg?branch=master)](https://www.travis-ci.com/github/zCRUSADERz/Customer-service)
[![codecov](https://codecov.io/gh/zCRUSADERz/Customer-service/branch/master/graph/badge.svg)](https://codecov.io/gh/zCRUSADERz/Customer-service)

# Customer service
## Установка
Для запуска необходимы инструменты: [Docker engine](https://docs.docker.com/engine/install/#server) и 
[Docker compose](https://docs.docker.com/compose/install/). После установки инструментов необходимо 
скачать compose.yaml файл из проекта или проект полностью и в корне проекта выполнить команду 
```
docker compose -p customer-service up
```
## Описание
REST веб сервис для хранения, поиска и работы с клиентами
## Примеры HTTP запросов
```curl -v localhost/api/``` точка входа в приложение
#### REST ресурс адрес
1. ```curl -v localhost/api/addresses``` коллекция REST ресурса - адрес

2. ```
    curl -v -X POST -H "Content-Type: application/json" \
      --data '{"country": "Россия", "region": "Новосибирская обл.", "city": "Новосибирск", "street": "Ленина", "house": "1", "flat": "1"}' \
      localhost/api/addresses
   ``` 
   POST запрос на создание адреса.
   
3. ```curl -v localhost/api/addresses/1``` GET запрос на получение адреса с id 1

4. ```
    curl -v -X PUT -H "Content-Type: application/json" \
      --data '{"country": "Россия", "region": "Новосибирская обл.", "city": "Новосибирск", "street": "Ленина", "house": "1", "flat": "1"}' \
      localhost/api/addresses/1
   ```
   PUT запрос для полного редактирования адреса с id 1
   
3. ```
    curl -v -X PATCH -H "Content-Type: application/json" \
      --data '{"country": "США"}' \
      localhost/api/addresses/1
   ```
   PATCH запрос для частичного редактирования адреса с id 1
  
4. ``` curl -v -X DELETE localhost/api/addresses/1``` DELETE запрос для удаления адреса с id 1

#### REST ресурс клиент
1. ```curl -v localhost/api/customers``` коллекция REST ресурса - клиент

2. ```curl -v localhost/api/customers/1``` GET запрос на получение клиента с id 1

3. ```
    curl -v -X POST -H "Content-Type: application/json" \
      --data '{"registeredAddress": "http://localhost/api/addresses/1", "actualAddress": "http://localhost/api/addresses/1", "firstName": "Сергей", "lastName": "Сергеев", "middleName": "Сергеевич", "sex": "MALE"}' \
      localhost/api/customers
   ```
   POST запрос на создание клиента. Для полей registeredAddress и actualAddress указывается URI 
   соответствующего ресурса. Поле sex имеет два возможных значения - MALE, FEMALE

4. ```
    curl -v -X PUT -H "Content-Type: text/uri-list" \
      --data 'http://localhost/api/addresses/1' \
      localhost/api/customers/1/actualAddress
   ```
   PUT запрос для изменения фактического адреса для клиента с id 1. В теле запроса передается URI ресурса "адрес".

5. ```
    curl -v -G --data-urlencode "firstName=Василий" --data-urlencode "lastName=Васильев" \
      localhost/api/customers/search/find-by-first-and-last-names
   ``` 
    GET запрос для поиска клиентов по имени и фамилии с параметрами firstName(Имя), lastName(Фамилия) 
    и опциональными параметрами page, size, sort

6. Для ресурса "клиент" доступны так же PUT и PATCH запросы для изменения, подобно описанным для ресурса адрес.