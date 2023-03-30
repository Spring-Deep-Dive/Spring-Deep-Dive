# QueryDSL 세팅 방법

> Querydsl defines a general statically typed syntax for querying on top of persisted domain model data. JDO and JPA are the primary integration technologies for Querydsl. This guide describes how to use Querydsl in combination with JPA.

**JDO(Java Data Objects)**: 데이터 영속을 위한 POJO, standard way to access persistent data in DB, using POJO to represent persistent data.

QueryDSL은 영속된 최상위 도메인 모델의 데이터에 따라서 정적으로 쿼리를 정의한다. 


- 출처
    - Tutorials, QueryDSL,http://querydsl.com/static/querydsl/latest/reference/html/ch02.html
    - Java Data Objects, https://db.apache.org/jdo/


## dependency

QueryDSL을 사용하기 위해 필요한 의존성 라이브러리는 다음과 같다.

- QueryDSL core
    - DB쿼리 생성을 위한 코어.
- QueryDSL JPA Library(optional)
    - DB에 접근하기 위해 사용되며, 이를 위해 QueryDSL JPA 라이브러리가 포함되어야 한다. JPA-specific 쿼리 타입과 표현식이 지원된다.
- QueryDSL SQL Library(optional)
    - SQL DB와 함께 QueryDSL 사용 시 포함된다. SQL-specific 쿼리 타입과 표현식이 지원된다.
- DB driver
- Java 8 or higher




- Maven

```Java
<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-apt</artifactId>
  <version>${querydsl.version}</version>
  <scope>provided</scope>
</dependency>

<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>${querydsl.version}</version>
</dependency>
```

```

```