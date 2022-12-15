# 목차

---

| 일자 | 내용 |
| --- | --- |
| 2022-12-15 | 목록 세부화 |
|  |  |

# 운영 방식

## 정리

- Github에 목차대로 패키지 분리
- markdown으로 내용 간단히 정리 및 해당 패키지에 예시코드 작성

## 진행 방향 (논의 필요)

1. 주제별 기간을 정해서 정리
2. 1~5번 목차까지 각각 한 파트씩 진행 후(운영방식 정리2 참조), 발표 이후 6~10 진행

## 12/12 회의 사항

- 주제별 기간을 정해서 정리 → 볼륨이 큰 항목은 하위주제를 세분화해서
- 주제 선정 방식 : 10개중 각각 1개 주제는 본인이 선정 , 나머지 1개 주제는 추첨
- 다른 인원에게도 발표 내용 공유
- 정기 발표 시간 : 매주 월요일 10시? (임시)
- 하위 주제를 디테일하게 추가하기
- 이후에 모든 인원 참여한 회의에서 목차 피드백, 일정 구체화하기

## 진행 논의

[양식 (1)](https://www.notion.so/1-5723ed644f2c4159a40db7fd5113804f)

# 목차

## 1.HTTP와 서블릿

- HTTP
    - HTTP Request ,HTTP Response
    - HTTP 캐시 , 조건부 요청
    - HTTP version
- HTTP Method , Status
- HTTP의 구조 ( Spec )
- 쿠키 & 세션
- 서블릿
    - 서블릿의 개념 , 특징
    - 서블릿의 동작 방식
    - 서블릿 API 계층 구조와 기능
    - 서블릿의 생명주기 메소드
    - 서블릿을 이용한 MVC 패턴 어플리케이션 구현 예시ㄹ
    - JSP (개념만)
- REST

## 2.객체지향과 스프링

- 객체 지향
    - 객체 지향에 대한 개념
    - 객체 지향 특징  ( 캡슐화 , 상속 , 추상화 , 다형성 )
    - 객체 지향 생활 체조
    - 객체 지향 설계 원칙 - SOLID
- 스프링 개념
    - 스프링이란?
    - 스프링이 등장한 배경
    - 스프링 트라이앵글 (PSA, IOC , AOP)
    - IOC , AOP , PSA 예시 코드

## 3.스프링 컨테이너와 빈

- 스프링 컨테이너
    - 스프링 컨테이너의 개념 및 구조
    - IOC
- DI
    - Injection 종류 및 개념 소개
- 의존 자동주입
- Bean
    - Bean Scope , Bean Definition
    - 빈 등록 (`@Configuration` , `@Component` ,`@Bean`)
    - Bean 생명주기
- ~~Dependency Lookup~~
    - ~~Dependency Lookup의 개념~~

## 4. Spring Boot와 Spring

- 내장 톰캣 , jar
- Boot Starter
- Profile,Auto Configuration
- Actuator

## 5. Spring Web MVC

- MVC
    - MVC 디자인 패턴이란?
    - MVC 패턴으로 작성된 패키지 구조 작성 및 소개
    - 레이어드 아키텍쳐
- 스프링 웹 MVC 구조 1
    - Dispatcher Servlet
- 스프링 웹 MVC 구조 2
    - Handler Adaptor (Controller Advice)
    - Handler Mapping
- 스프링 웹 MVC 구조 3
    - Filter
    - Interceptor
    - Argument Resolver
        - Message Converter
        - View Resolver
- 스프링 웹 MVC 구조 4
    - 비즈니스 로직 ( Service )
    - 데이터 접근 계층 ( Repository )
- ~~템플릿 엔진 사용법~~

## 6. Spring AOP

- 패턴
    - Decorator
    - Proxy
- 동적 프록시
    - CGLIB , JDK Dynamic Proxy
- Spring AOP
    - 핵심 개념
    - AOP weaving
    - PointCut
    - Aspect
    - Target
    - Advice
    - JointPoint

---

### 6번까지 발표 이후에 상세 목차 자세히 기술

## 7.Spring Database

- JDBC
- DataSource
- Connection Pool
- DataBase Transaction
- Spring Transaction 전략 (ex:`@Transactional`)
- Spring Data
    - 관련된 프로젝트들 소개.

## 8.Hibernate(JPA)

- Hibernate구현체로써의 JPA
- JPA의 사용 개요
- 연관 관계 매핑
- JPA 값 타입
- 영속성
    - 영속성 생명주기
    - 영속성 Context (Entity manager)
    - 영속성 전이
- JPQL
    - Native Query
    - Named Query
- Fetch 전략 (즉시,지연로딩)
    - proxy
    - N+1 문제와 Fetch JOIN
- Auditing

## 9.Spring Data JPA

- 공통 인터페이스
    - JPA Repository
- **쿼리 메소드 기능**
- Domain Class Converter

## 10.Query DSL

- Query DSL 세팅 방법
- QClass의 개념
- Query DSL의 기본 문법과 몇가지 예제
    - Group by
    - join
    - subquery
- Dynamic Query를 위한 BooleanBuilder
- SQL 함수

## 11.[JUnit5](https://junit.org/junit5/docs/current/user-guide/#overview)

- TDD,BDD
    - 테스트 자동화
    - 단위 테스트 ( 비즈니스 로직 검증, JPA Test )
    - 통합 테스트 ( Boot Test, Web MVC Test)
- 테스트 도구
    - Assertions
    - Mockito (Mocking)
- [Jacoco](https://techblog.woowahan.com/2661/) (테스트 커버리지 툴)%
