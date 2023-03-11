# 연관관계 매핑

## 연관관계
ORM기술은 객체와 테이블 중심의 각기 다른 패러다임에서 발생하는 문제를 해결하기 위해 등장했으며, 이를 통해 개발자는 서비스 로직을 짜면서 객체에 온전히 집중하여 개발할 수 있게 되고, DB의 테이블에 대한 고민을 최소화 할 수 있다.<br><br>

서로 다른 패러다임이지만, 둘 모두 `연관관계`가 존재한다. OOP에서의 연관관계는 객체 간 협력을 목표로 하며, DB의 테이블에서는 효율적으로 데이터를 적재/관리하기 위함이다.<br><br>

한 팀에서 여러 회원을 가질 수 있다면, 팀과 회원의 관계는 1:N(일대다)라고 할 수 있다. 이러한 관계를 각각의 입장에서 구현하면 아래와 같다.



### 테이블 세계의 연관관계
- Member 테이블

| id | nickname | teamId |
|:--:|:--------:|:------:|
| 0  |   John   |   10   |

- Team 테이블

| id |   name  |
|:--:|:-------:|
| 10  | Spring |

테이블에서 `외래 키`를 기반으로 JOIN해서 연관 테이블을 찾는다.



### 객체 세계의 연관관계

```JAVA
public class Member {
    private Long id;
    private String nickname;
    // ...
}

public class Team {
    private Long id;
    private String name;
    Member[] members;
    // ...
}
```

객체는 `참조`를 통해 연관된 객체를 찾는다.


## JPA를 활용한 연관관계 매핑



<img src="/assets/images/JPA/erm_1.png">

```JAVA
@Entity
public class Member {
	@Id @GeneratedValue
	@Column(name = "MEMBER_ID")
	private Long memberId;

	@Column(name = "USERNAME")
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "TEAM_ID") 
	private Team team; // Team 객체
}

@Entity
public class Team {
	@Id @GeneratedValue
	@Column(name = "TEAM_ID")
	private Long teamId;

	private String nickname;

	
}

```


```JAVA
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setUserName("member1");
member.setTeam(team); // Member에 직접 Team객체 주입
em.persist(member);

Member findMember = em.find(Member.class, member.getId());
//Team findTeam = em.find(Team.class, findTeamId);
Team findTeam = findMember.getTeam();

```

`@ManyToOne`, `@JoinColumn` 을 통해 연관관계를 명시한다.
하지만 이때, Member에서는 Team으로 접근이 가능하지만, Team에서는 소속 Member에 접근할 수 없다.
이렇게 한쪽만 참조/접근이 가능한 관계를 단방향 연관관계라고 한다.

위의 코드를 활용한 테이블 상황

- Member 테이블

| MEMBER_ID | AGE | NICKNAME | TEAM_ID |
| :-:       | :-: | :-:      | :-:     |
| 1         | 20  | John     | 1       |

- Team 테이블

| TEAM_ID | NAME    | 
| :-:     | :-:     |
| 1       | TeamOne |


### 양방향 연관관계 매핑


<img src="/assets/images/JPA/erm_2.png">

```Java
@Entity
public class Member {
	@Id @GeneratedValue
	private Long id;

	private String nickname;
	
	@ManyToOne // Member의 입장에서 Many가 된다.
	@JoinColumn(name = "TEAM_ID") 
	private Team team; // Team 객체
}

@Entity
public class Team {
	@Id @GeneratedValue
	@Column(name = "TEAM_ID")
	private Long teamId;
	
	@Column(name = "USERNAME")
	private String name;


	@OneToMany(mappedBy = "team") 
	private List<Member> members = new ArrayList<>(); // 추가된 필드

}
```

1:N 관계에서 1에 해당하는 Team에 `@OneToMany`를 통해 Member객체(테이블)을 매핑한다.
`mappedBy`속성을 통해 해당 외부 객체의 필드 이름을 지정한다.

```Java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setUsername("member1");
member.setTeam(team);
em.persist(member);

em.flush();
em.clear();

Member findMember = em.find(Member.class, member.getId());
List<Member> members = findMember.getTeam().getMembers(); // 해당 팀에 속한 모든 멤버 로드
```

위의 예시를 통한 데이터베이스 상황

- Member 테이블

| MEMBER_ID | AGE | NICKNAME | TEAM_ID |
| :-:       | :-: | :-:      | :-:     |
| 1         | 20  | John     | 1       |

- Team 테이블

| TEAM_ID | NAME    | 
| :-:     | :-:     |
| 1       | TeamOne |

앞서 예시코드와 달라진점은 Team에서 Members가 추가되었다는 점이다.
Members에 대해 직접 추가하지 않았지만, 조회 시 `mappedBy`옵션을 통해 실제 쿼리에서 join을 통해 조회하게 된다.

```SQL
select
        m1_0.member_id,
        m1_0.nickname,
        t1_0.team_id,
        t1_0.name 
    from
        member m1_0 
    left join
        team t1_0 
            on t1_0.team_id=m1_0.team_id 
    where
        m1_0.member_id=?

```

위의 코드는 Team의 members를 조회하는 SQL쿼리이다.

<br>

---

<br>


## 연관관계의 주인(Owner)과 mappedBy

객체에서 양방향 연관관계는 실제로 단방향 연관관계 2개를 통해 구현된다.
```Java
class A {
    B b;
}

class B {
    A a;
}

A a = new A();
B b = new B();
a.b = b;
b.a = a;
```

반면에 테이블 구조에서 양방향 연관관계는 외래키를 통해 한번에 구현된다.
```SQL
SELECT *
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

SELCT *
FROM TEAM T
JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

### 외래키의 관리

<img src="/assets/images/JPA/FK_owner.png">

- in 테이블
Member의 TEAM_ID(FK)만 변경되면 된다.

- in 객체
객체에서는 memberA가 Team A에 속해있을때, memberA의 Team B로 변경하려면, Team의 List 필드를 수정하거나, MemberA의 Team필드를 수정해야한다.

이러한 차이점이 존재한다.<br>
DB입장에서 외래키가 존재하는 쪽이 무조건 다(Many)인 상태이며, 참조하는 쪽은 일(One)일 가능성이 높다. 따라서, 외래키를 관리하는 주인은 Many(위의 예시에서 Member)가 되는 것이 좋다.


<img src="/assets/images/JPA/FK_owner2.png">

- 양방향 매핑 규칙
    - 객체의 두 관계 중 하나를 연관관계의 주인으로 지정
    - 연관관계의 주인은 외래키의 위치를 기준으로 한다. -> Many인 쪽이 주인
    - 연관관계의 주인만이 외래키를 관리(등록 및 수정)
    - 주인이 아닌 쪽은 읽기 및 참조
    - 주인은 mappedBy 속성 사용 X
    - 주인이 아니면 mappedBy 속성으로 주인을 지정


### Bad Practice

```Java
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setName("member1");

// 역방향(주인이 아닌 방향)만 연관관계 설정
team.getMembers().add(member);
em.persist(member);
```

위 코드에 대한 DB의 상황



- Member 테이블

| MEMBER_ID  | NICKNAME | TEAM_ID |
| :-:        | :-:      | :-:     |
| 1          | John     | <null>  |

- Team 테이블

| TEAM_ID | NAME    | 
| :-:     | :-:     |
| 1       | TeamA   |



위의 예시코드를 실행 시, 실제 DB에 Member테이블에 TEAM_ID(FK)는 null이 들어가게 된다.<br>
member에서 team에 대한 지정을 해주지 않았기때문이다.<br>

> Cascading 옵션으로 가능한지?

### Recommend Practice

```JAVA
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setName("member1");
member.setTeam(team); // Member-Team간의 연관관계를 맺도록 하는 코드 부분
em.persist(member);

team.getMembers().add(member); // 연관관계 매핑에 대해 명시적으로 표현하기 위함

```



```Java
// in Team 클래스
// 메소드에서 양방향 지정하는 것이 좋다.
public void addMember(Member member) {
	member.setTeam(this);
	this.members.add(member);
}
```

자바(JPA) 수준에서 단방향 매핑만으로도 이미 연관관계는 매핑되며, DB상으로 FK를 통해 관계가 설정된다.<br>
양방향 매핑과의 차이는 그래프 탐색기능을 통해 자바 수준에서 반대 방향(Team->Member)에 대한 조회가 가능하다는 것이다.<br>
따라서 양방향 매핑은 필요에 따라, 반대 방향에 대한 조회가 필요한 경우 적용하면 되며, 이는 테이블에 영향을 미치지 않는다.<br>

> 가능하면 양방향 매핑을 피하도록 한다.
- 양방향 매핑으로 인해 순환이 발생할 수 있다.
- 양방향으로 설정함으로 엔티티 간 관계 복잡도가 증가될 수 있다.



