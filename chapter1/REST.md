<br>

# REST

REST: **Re**presentational **S**tate **T**ransfer

자원을 이름으로 구분하여 해당 자원의 정보를 주고 받는 모든 행위를 의미한다

> HTTP URI를 통해 자원을 명시하고, HTTP Method를 통해 해당 자원에 대한 CRUD 동작을 수행하는 것

## Glossary

자원(resource)의 표현(representation)에 의한 상태 전달

- 자원(resource)의 표현(representation)
    - 자원: 소프트웨어가 관리하는 모든 것<br>
    ie. DB, 데이터, SW etc

    - 표현: 자원을 표현하기 위한 이름<br>
    ie. DB의 정보가 자원이라면, student를 자원의 표현으로 정의
- 상태(정보) 전달
    - 데이터가 요청되어지는 시점에 자원의 상태를 전달
    - JSON 혹은 XML를 통해 데이터를 주고 받는 것이 일반적

## Concepts

REST는 자원 중심의 구조(Resource Oriented Architecture, ROA)에 HTTP Method를 통해 자원을 조작 및 전달한다.
