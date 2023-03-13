# 다양한 연관관계 매핑

연관관계 매핑 시 고려해야 할 사항 3가지
- 다중성
    - 1:N, N:1, 1:1, M:N
- 단방향/양방향
    - 기본적으로 단방향
    - 필요에 따라 mappedBy를 통한 양방향 적용
- 연관관계의 주인
    - 외래키를 관리하는 참조
    - 주인의 반대편: 외래키에 영향을 주지 못하고 단순 조회만 허용

<br>

---

<br>

## N:1 - 다대일
연관관계의 주인을 N쪽으로 하는 경우를 N:1 관계로 칭한다.


<img src="/assets/images/JPA/n_1_relation1.png">

```Java
class Member {
		// ...
	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team;
}

class Team {
		// ...
	@Column(name = "TEAM_ID")
	private Long id;

}
```

<img src="/assets/images/JPA/n_1_relation2.png">

```Java
class Member {
		// ...
	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team; // mappedBy의 변수명
	
}

class Team {
		// ...
	@Column(name = "TEAM_ID")
	private Long id;

	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

}
```

<br>

---

<br>

## 1:N - 일대다

> 실무에서 거의 사용하지 않는 매핑법이다.

```JAVA
public class Member {
    @GeneratedValue
    @Id
    private Long memberId;

    private String nickname;
}

public class Team {

    @GeneratedValue
    @Id
    private Long teamId;

    private String name;

    @OneToMany
    @JoinColumn(name = "TEAM_ID")
    private List<Member> members = new ArrayList<>();
}
```

일대다 단방향에서 일(1)이 연관관계의 주인이 된다. 테이블 일대다 관계에서는 Member측에 TEAM_ID(FK)가 다대일 관계와 마찬가지로 구성된다.<br>
하지만, Member 엔티티에는 외래 키를 매핑할 수 있는 참조 필드가 없고, Team 쪽에 참조 필드인 members가 존재한다.

- Member 테이블

| MEMBER_ID | AGE | NICKNAME | TEAM_ID |
| :-:       | :-: | :-:      | :-:     |
| 1         | 20  | John     | 1       |

- Team 테이블

| TEAM_ID | NAME    | 
| :-:     | :-:     |
| 1       | TeamOne |

### 일대다 매핑의 단점
매핑한 객체가 관리하는 외래 키가 다른 테이블에 있다는 것이 단점이다.<br>

```Java
	Team t1 = new Team();
	t1.setName("Alpha");
	em.persist(t1);

	Member m1 = new Member();
	m1.setNickname("John");
	em.persist(m1);

	t1.getMembers().add(m1);

	em.flush();
	em.clear();
```

다음과 같은 테스트코드에서 발생되는 쿼리는 다음과 같다.

```SQL
INSERT INTO team VALUES (?, ?)
INSERT INTO member VALUES (?, ?)
UPDATE member SET team_id = ?
```

엔티티에 대한 저장을 위한 INSERT 쿼리와 연관관계 처리를 위한 UPDATE 쿼리가 발생하게 된다.<br>
이러한 경우, 일대다 단방향 매핑을 하기보다 다대일 양방향 매핑을 통해 관리하는 것이 좋다.

<br>

---

<br>


## 1:1 - 일대일
일대일 관계에서는 양측 엔티티(테이블) 둘 중 어디든 외래키를 가질 수 있다.

```Java
public class Team {

    @GeneratedValue
    @Id
    private Long teamId;
    private String name;

    @OneToOne
    @JoinColumn(name = "OFFICE_ID")
    private Office teamOffice;

}

public class Office {
    @GeneratedValue
    @Id
    private Long officeId;
    private String location;
}
```
다음과 같은 상황에서, Team과 Office는 동등한 1:1관계에 있다.

- Office 테이블

| OFFICE_ID | LOCATION |
| :-:       | :-:      |
| 1         | Seoul    |

- Team 테이블

| TEAM_ID | NAME    | OFFICE_ID |
| :-:     | :-:     | :-:       |
| 1       | TeamOne | 1         |

위의 예시에 따른 테이블은 다음과 같다.<br>
여기서 외래키를 관리하는 주체는 Team이며, Office는 Team의 FK를 통해 참조된다.<br>
즉, 

```Java
public class Team {

    @GeneratedValue
    @Id
    private Long teamId;
    private String name;

}

public class Office {
    @GeneratedValue
    @Id
    private Long officeId;
    private String location;

	@OneToOne
	@JoinColumn(name = "TEAM_ID")
	private Team hostTeam;
}
```
똑같은 1:1관계에 있는 엔티티들에서 외래키의 관리를 Office측으로 변경했다.

- Office 테이블

| OFFICE_ID | LOCATION | TEAM_ID   |
| :-:       | :-:      | :-:       |
| 1         | Seoul    | 1         |

- Team 테이블

| TEAM_ID | NAME    | 
| :-:     | :-:     | 
| 1       | TeamOne | 

위의 예시에 따른 테이블은 다음과 같다.<br>
앞의 예시와 달리, 외래키를 관리하는 주체가 Office가 변경되었기에 Office에 TEAM_ID(FK)가 추가되었다.

### 어디를 외래키의 주인으로 해야하나?
`객체지향의 입장`에서는 주 테이블(TEAM)에서 외래키를 관리하도록 하여, 외래키를 객체의 참조와 유사하게 사용하도록 할 수 있다는 장점이 있다.<br>
위의 예시에서는 TEAM이 한개의 OFFICE를 갖는 다는 가정인데, 여러 OFFICE를 갖게 되면 추가되는 OFFICE를 위해 테이블의 구조를 변경해야만 한다.<br>
`테이블의 입장`에서는 대상 테이블(OFFICE)에 외래키를 주도록 하여, 테이블 관계를 일대일에서 일대다로 변경할 때 테이블의 구조를 그대로 유지할 수 있다.<br>






<br>

---

<br>


## N:M - 다대다

객체의 다대다 관계를 위해서는 @ManyToMany 를 통해 설정할 수 있다.

### 일반적인 단방향 

```JAVA
public class Team {

    @GeneratedValue
    @Id
    @Column(name = "TEAM_ID")
    private Long teamId;

    private String name;

    @ManyToMany
    @JoinTable(name = "TEAM_WELFARE",
            joinColumns = @JoinColumn(name = "TEAM_ID"),
            inverseJoinColumns = @JoinColumn(name = "WELFARE_ID")
        )
    private List<Welfare> welfares = new ArrayList<>();
}

public class Welfare {

    @Id
    @GeneratedValue
    @Column(name = "WELFARE_ID")
    private Long welfareId;

    private String name;
    private Long price;

}
```

DB 테이블을 통해서 2개의 엔티티에 대해 다대다 관계를 직접 풀어낼 수 없다.<br>
두개의 테이블의 중간에서 매개역할을 하는 연결 테이블을 사용해야 한다.<br>

- TEAM

| TEAM_ID | NAME |
| :-:     | :-:  |

- WELFARE

| WELFARE_ID | NAME | PRICE |
| :-:        | :-:  | :-:   |


- TEAM_WELFARE

| TEAM_ID | WELFARE_ID |
| :-:     | :-:        | 


실제 테이블에서는 TEAM_WELFARE라는 연결 테이블을 이용해 참조를 하고 있지만,<br>
객체 레벨에서는 중간 매개 엔티티 없이 참조가 진행되었다.<br>

### 양방향 매핑

```JAVA
public class Team {

    @GeneratedValue
    @Id
    @Column(name = "TEAM_ID")
    private Long teamId;

    private String name;

    @ManyToMany
    @JoinTable(name = "TEAM_WELFARE",
            joinColumns = @JoinColumn(name = "TEAM_ID"),
            inverseJoinColumns = @JoinColumn(name = "WELFARE_ID")
        )
    private List<Welfare> welfares = new ArrayList<>();
}

public class Welfare {

    @Id
    @GeneratedValue
    @Column(name = "WELFARE_ID")
    private Long welfareId;

    private String name;
    private Long price;

	@ManyToMany(mappedBy = "welfares")
	private List<Team> teams = new ArrayList<>();

}
```

`mappedBy`를 통해서 양방향 설정을 할 수 있다.

```JAVA
Welfare w = new Welfare();
        w.setName("foo");
        w.setPrice(1000L);
        em.persist(w);

        Team t1 = new Team();
        t1.setName("TeamOne");
        t1.getWelfares().add(w);
        w.getTeams().add(t1);
        em.persist(t1);

        em.flush();
        em.clear();


        Team findTeam = em.find(Team.class, 1L);
        Welfare findWelfare = em.find(Welfare.class, 1L);

        System.out.println(findTeam.getWelfares().get(0).getName());
        System.out.println(findWelfare.getTeams().get(0).getName());

```

Team에서도 Welfare를 조회할 수 있고, Welfare에서도 등록된 Team을 조회할 수 있다.<br>

### 다대다 연결 테이블만의 한계

단순히 `team` - `welfare` 엔티티 간에 매핑만 해야하는 상황이라면 위의 예시처럼 연결 테이블을 사용하도록 하고, 엔티티 간 직접 연결해서 사용할 수 있다.<br>
하지만, Team에서 Welfare에 대한 추가정보(만료기간, 유용횟수 등 메타데이터)가 필요하다면, 두 테이블을 연결하는 `연결 엔티티`를 사용해야 한다.<br>
확장성을 고려한다면 직접 엔티티를 연결하기 보단 연결 엔티티를 사용하는 것이 좋다.<br>

