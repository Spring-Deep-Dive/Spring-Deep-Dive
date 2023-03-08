### 1\. JPQL(Java Persistence Query Language)

SQL을 추상화하여 사용하는 객체지향 쿼리 언어.

테이블이 아닌 객체를 대상으로 쿼리를 수행하므로 특정 DB에 종속적이지 않다.

### 2\. JPQL 문법

```
select_문 :: =
  select_절
  from_절
  [where_절]
  [groupby_절]
  [having_절]
  [orderby_절]
update_문 :: = update_절 [where_절]
delete_문 :: = delete_절 [where_절]
```

```
SELECT m FROM Member AS m WHERE m.username = 'Hello'
```

-   대소문자 구분: 엔티티와 속성은 대소문자를 구별한다. Member와 member는 다름. 그러나 SELECT, FROM과 같은 JPQL 키워드는 대소문자 구별 안 함.
-   엔티티 이름: FROM 이후에 오는 대상은 테이블 이름이 아니라 **엔티티 이름**이다.
-   Alias는 필수: Alias를 필수적으로 사용해야 하는데, 위의 예시에서 AS 뒤에 오는 m이 Member의 Alias이다. AS 키워드는 생략 가능.

### 3\. TypedQuery, Query

JPQL을 실행시키기 위해 만드는 쿼리 객체이다.

JPQL 리턴 타입을 명확하게 알면 TypedQuery 쓰고, 아니라면 Query 쓰면 된다.

```
// 조회 대상이 정확히 Member 엔티티이므로 TypedQuery 사용 가능
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);

// 조회 대상이 String, Integer로 명확하지 않으므로 Query 사용
Query query = em.createQuery("SELECT m.username, m.age FROM Member m");
```

### 4\. Parameter Binding

2가지 방식의 파라미터 바인딩을 지원한다.

```
SELECT m FROM Member m WHERE m.username = ?0 // 위치 기반
SELECT m FROM Member m WHERE m.username = :name // 이름 기반
```

개발자 입장에서 더 명확하게 이해되는 이름 기반이 바인딩이 더 좋은 것 같아 보인다.

```
TypedQuery<Member> query = 
    em.createQuery("SELECT m FROM Member m WHERE m.username = :username", Member.class)
    .setParameter("username", "Hello");

List<Member> result = query.getResultLst();
```

username은 Member 클래스에 정의된 프로퍼티 이름이다. 앞에 :를 붙여서 바인딩한다.

username에 Hello가 바인딩 될 것이다.

LIKE 연산처럼 % 같은 특수문자가 필요할 경우엔 전달하는 파라미터에 붙여주면 된다.

```
TypedQuery<Member> query = 
    em.createQuery("SELECT m FROM Member m WHERE m.username LIKE :username", Member.class)
    .setParameter("username", "%Hello%");
```

바인딩 데이터는 SQL 문법이 아닌 컴파일 언어로 처리되기 때문에 문법적 의미를 가질 수 없으므로, 바인딩 변수에 Injection Query를 넣더라도 의미있는 쿼리로 동작하지 않는다.

따라서 아래와 같이 변수를 직접 String으로 붙여넣는 끔찍한 코드를 사용하지 않는한 SQL Injection에 안전하다.

```
String sql = "SELECT m FROM Member m WHERE id=" + id;
```

### 5\. NamedQuery (정적 쿼리)

JPA는 동적 쿼리(JPQL, Criteria, Specification, Querydsl) 뿐만 아니라 정적 쿼리도 지원한다.

미리 정의해서 이름 부여해놓고 사용한다. 어노테이션이나 XML에 정의해놓고 쓰면 된다.

```
@Entity
@NamedQuery(
   name = "Member.findByUsername",
   query = "select m from Member m where m.username = :username")
public class Member {
...
}
```

```
List<Member> resultList =
   em.createNamedQuery("Member.findByUsername", Member.class)
      .setParameter("username", "John")
      .getResultList();
```

정적 쿼리는 애플리케이션 로딩 시점에 JPQL 문법을 체킹하고 미리 파싱해둔다.

따라서 오류를 빠르게 확인 가능하고, 사용 시점에 파싱된 결과를 재사용하고, 변하지않는 정적 SQL이므로 DB 레벨에서도 성능상 이점을 얻을 수 있다.

### 6\. NativeQuery

JPA는 네이티브 쿼리 작성을 지원한다.

"그럼 그냥 JDBC API 쓰는 거랑 무슨 차이야?"라는 의문이 든다.

NativeQuery는 엔티티를 조회 가능하고, 영속성 컨텍스트의 이점을 그대로 취할 수 있다.

@Query 어노테이션을 사용하여 쿼리를 입력하고, nativeQuery 옵션을 true로 주면 된다.

```
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "SELECT * FROM member WHERE id=:id", nativeQuery = true)
    Optional<Member> findById(String id);
}
```

또는 다음과 같이 raw sql을 EntityManager의 createNativeQuery()에 첫번째 인자로 넣으면 된다.

```
String sql = "SELECT * FROM member WHERE id=:id";
List<Member> result = em.createNativeQuery(sql, Member.class).setParameter("id", "1").getResultList();
```

근데 이 방식이 SQL Injection에 안전한 것인지 궁금해져서 로그를 찍어봤다.

```
[main] hello.Application                        : InjectionTest:
[main] org.hibernate.SQL                        : SELECT * FROM MEMBER WHERE id=?
Hibernate: SELECT * FROM MEMBER WHERE id=?
[main] o.h.type.descriptor.sql.BasicBinder      : binding parameter [1] as [VARCHAR] - [admin' OR '1'='1]
```

내부적으로 PreparedStatement처럼 동작하여 injection으로 동작하는 것이 아닌 value로 값이 들어가게 되어 안전한 상태임을 확인 할 수 있다.

#### 참고

[https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)

[https://product.kyobobook.co.kr/detail/S000000935744](https://product.kyobobook.co.kr/detail/S000000935744)

[https://joont92.github.io/jpa/JPQL/](https://joont92.github.io/jpa/JPQL/)

[https://blog.voidmainvoid.net/173](https://blog.voidmainvoid.net/173)
