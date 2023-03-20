# JPA의 fetch 전략

JPA에서 연관된 객체를 호출하는 것을 `Fetch`라고 하며 2가지의 방법이 존재한다: Eager, Lazy<br>
여기서, 연관 객체 호출은 DB에서 해당 데이터를 로드해 PersistenceContext(영속)에 객체 상태로 저장하는 것을 의미힌다.<br>

## 기본 FetchType

기본 FetchType은 해당 엔티티의 카디널리티에 따라 다르다.<br>
모든 `to-one` 관계는 Eager를 기본으로 하며, 모든 `to-many` 관계는 Lazy로 설정된다.<br>
<br>
여기서, Eager는 root 엔티티가 호출될 때, 연관 엔티티도 기본적으로 함께 로드되어 영속상태로 만드는 것을 뜻한다.<br>
Lazy는 root 엔티티가 호출될 때, root 엔티티만 로드하여 영속상태로 만들고, 연관 엔티티는 프록시의 형태로 보관한다.<br>
이후 프록시 상태의 연관 엔티티가 사용되는 시점에 쿼리를 발생시켜 데이터를 로드해 영속상태로 만든다.<br>
<br>

### Proxy & Fetch
엔티티를 용속 상태로 만들 때, 엔티티에 컬렉션이 있으면 하이버네이트는 내부적으로 해당 컬렉션을 내장 컬렉션으로 변환한다.<br>

- Java의 컬렉션


| 자바 컬렉션 | 중복 허용 | 순서 보장 |
| : - :    |  : - :  | : - :   |
| Set    | X | X |
| List | O | O |
| Map | X | X |


- Hibernate의 내장 컬렉션

| 인터페이스 | 내장 컬렉션 | 중복 허용 | 순서 보관 |
| Collection, List, | PersistenceBag | O | X |
| Set | PersistenceSet | X | X |
| List + @OrderColumn | PersistenceList | O | O |

```Java
@OneToMany
private List<Member> myList = new ArrayList<>();

@OneToMany
private Collection<Member> collection = new ArrayList<>();

@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
private Set<Member> mySet = new HashSet<>();

@OneToMany
@OrderColumn(name = "name")
private List<Member> orderColumnList = new ArrayList<>();


// 영속전
// list = class java.util.ArrayList
// collection = class java.util.ArrayList
// set = class java.util.HashSet

// 영속후
// list = org.hibernate.collection.internal.PersistencetBag
// collection = org.hibernate.collection.internal.PersistenceBag
// set = org.hibernate.collection.internal.PersistenetSet


```

이때, PersistenceSet은 중복을 허용하지 않기때문에, 엔티티를 추가할 때, 중복검사를 실행하기 때문에 추가적인 로딩에 지연이 발생한다.<br>
즉, Set 컬렉션으로 지정된 엔티티에 추가를 하게되면 `insert`가 동작하기 전에 `select`가 먼저 실행되게 된다.<br>
<br>
List컬렉션에 @OrderColumn을 사용하여 DB에 순번을 부여해 컬럼을 관리할 수 있으며 순서의 개념을 적용시킬 수 있다.<br>
이렇게 된 List의 경우, PersistneceList로 패킹된다. -> 이전의 기본 List는 PersistenceBag.<br>

### @OrderColumn의 문제
@OrderColumn을 사용하면 해당 컬렉션의 순서를 위해 POSITION 값을 함꼐 사용하게 된다.<br>
```java
@OneToMany(mappedBy = "team")
@OrderColumn(name = "POSITION")
private List<Member> members = new ArrayList<>();
```

TEAM 내부에서 Member의 순서를 지정하기 위해 Position이라는 값을 사용하는 것이다.<br>
다음과 같이 컬렉션을 어노테이션과 함께 선언하면 아래와 같은 테이블이 형성된다.

- Member 테이블


| MEMBER_ID | NAME | POSITION | TEAM_ID |
| :-: | :-: | :-: | :-: |


- 연관관계의 주인(Team)이 다른 엔티티(Member)의 테이블을 관리하게 된다.
    POSITION은 Member테이블에 속해있지만, Member엔티티는 POISITION 값에 대해 알 수 없다.<br>

- List에 생성, 수정, 삭제 등의 작업이 이루어지면 POSITION값을 재정렬하기 위해 로직이 크게 증가한다.
    1번 Position을 삭제하게되면, 2번 이후의 모든 값들을 재정렬해야한다.<br>

- NPE 발생 가능성
    1,2,3,4의 Member가 존재할 때, 2번 Member를 강제로 삭제하면 Position값은 0,2,3이 된다.<br>
    이경우 Members를 조회하면 1번 위치에 Null이 존재하기때문에 NPE가 발생하게 된다.<br>

`

## N+1 이슈

이러한 두가지의 Fetch 전략을 통해서 개발자는 비즈니스 로직에서 사용되는 엔티티만을 위한 쿼리를 발생시키도록 유도할 수 있다.<br>
하지만 fetch 전략을 비효율적으로 설정하게 되면 1개의 로직 수행을 위해 N개의 쿼리가 발생할 수 있으며,<br>
사용되지 않는 엔티티를 Eager로 설정하면 사용하지도 않을 객체를 위한 쿼리가 늘어나게 된다.<br>
전자의 상황을 JPA의 `N+1 이슈`라고 말한다.<br>






