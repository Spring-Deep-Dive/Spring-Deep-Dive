# N+1문제와 해결법

앞서 N+1문제에 대해 설명했다.<br>
N+1은 1개의 동작을 수행하기위해 N번의 추가 쿼리가 발생하는 것을 말한다<br>
대표적으로 조회에 있어 연관관계에 있는 객체에 대해 추가로 발생하는 케이스가 있으며,<br>
이후에 `em.remove`를 수행하기 위해 select 쿼리가 추가로 발생하거나 `@OrderColumn`사용 시 기준 컬럼을 위한 추가 쿠러기 발생할 수 있다.<br>

<br>


## 즉시로딩의 예시

```Java
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();
}

public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;
    
}
```

여기서 Member는 orders에 대해 즉시로딩으로 설정하였다.<br>

```java
em.find(Member.class, id);
```
에 대한 발생 쿼리는 다음과 같다.

```SQL
SELECT M.*, O.*
FROM
    MEMBER M
OUTER JOIN ORDERS O ON M.ID = O.MEMBER_ID
```

JOIN을 통해서 한번에 회원과 주문정보를 조회한다.<br>
하지만 JPQL을 사용하게 되면 즉시로딩의 여부가 무의미해진다.<br>

```JAVA
em.createQuery("select m from Member m", Member.class).getResultList();
```

당연히도 위의 쿼리에 대한 동작은 다음과 같다.

```SQL
SELECT * FROM MEMBER;
SELECT * FROM ORDERS WHERE MEMBER_ID=?
```

Member 엔티티에 대한 로딩을 시도한 후, 즉시로딩으로 설정된 필드를 위해 추가 쿼리가 발생하는 것이다.<br>
만약 Member가 여러명이라면 추가로 order를 위한 쿼리가 각개 발생한다<br>

```Java
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
```

```SQL
SELECT * FROM MEMBER;
SELECT * FROM ORDERS WHERE MEMBER_ID=?
SELECT * FROM ORDERS WHERE MEMBER_ID=?
SELECT * FROM ORDERS WHERE MEMBER_ID=?
SELECT * FROM ORDERS WHERE MEMBER_ID=?
...
```

## 지연로딩의 예시

```Java
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}

public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;
    
}
```

위와 같은 예시에서 지연로딩으로 설정했다.<br>


```java
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
```
에 대한 발생 쿼리는 다음과 같다.

```SQL
SELECT * FROM MEMBER;
```

당연히도 지연로딩으로 설정되었기때문에 order에 대한 추가 쿼리가 발생하지않는다.<br>

```Java
List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
for(Member member : members) {
    member.getOrders().size();
}
```

위와 같이 각 회원에 대해 주문량을 조회하는 로직이 있을때, 쿼리는 다르게 동작할까?<br>

```SQL
SELECT * FROM MEMBER;
SELECT * FROM ORDERS WHERE MEMBER_ID=1 
SELECT * FROM ORDERS WHERE MEMBER_ID=2
SELECT * FROM ORDERS WHERE MEMBER_ID=3
SELECT * FROM ORDERS WHERE MEMBER_ID=4
...
```

즉시로딩과 마찬가지로 각 멤버에 대한 추가 주문조회 쿼리가 발생한다.<br>

## Fetch Join 사용!

N+1문제를 해결하는 가장 일반적인 방법은 fetch join을 통해 조회할때 필요 엔티티를 함께 로드하는 것이다.<br>

```Java
select m from Member m join fetch m.orders
```

이에 대한 실행 쿼리는 다음과 같다.

```SQL
SELECT M.*, O.* FROM MEMBERM M
INNER JOIN ORDERS O ON M.ID = O.MEMBER_ID
```

이때, 여기서 fetch join을 기본적으로 여러번 사용할 수 있으나, Bag형태의 컬렉션에 대해서는 오직 1개의 fetch join만 가능하다.<br>
그 이외의 toOne 관계의 엔티티는 제한없이 걸 수 있다.<br>
그 이유는 2개 이상의 bag 타입 컬렉션에 대해 fetch join을 시도하면 카르테시안 곱이 발생하여 결과가 증폭되며 중복된 결과에 대한 제어가 불가하다.<br>

## BatchSize 조정

BatchSize를 조절하여 조회 쿼리를 발생시킬때 IN절의 조건을 추가하여 한번에 쿼리를 진행하도록 할 수 있다.<br>

```Java


public class Member {
    @BatchSize(size = 5)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}

for(Member member : members) {
    member.getOrders().size();
}
```

앞서 예제와 유사한 경우이다.<br>
다른 점은 orders에 대해 batch size를 5로 설정하였다.<br>
Member의 orders를 조회할 때, 5개의 단위로 조회할 수 있게 된다.<br>


```SQL
SELECT * FROM MEMBER;
SELECT * FROM ORDERS WHERE MEMBER_ID IN ( ?,?,?,?,? );
```


이외에도 N+1문제를 해결하는 방법은 존재한다.<br>
SQLMapper를 이용하거나 JDBC로 네이티브 쿼리를 정의하거나, 엔티티 그래프, QueryDSL을 사용할 수 있다.<br>
