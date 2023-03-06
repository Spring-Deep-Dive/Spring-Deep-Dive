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

일대다 단방향에서 일(1)이 연관관계의 주인이 된다. 테이블 일대다 관계는 항상 다(N)쪽에 외래키가 존재한다.
