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

## N+1 이슈

이러한 두가지 전략을 통해서 개발자는 비즈니스 로직에서 사용되는 엔티티만을 위한 쿼리를 발생시키도록 유도할 수 있다.<br>
하지만 fetch 전략을 비효율적으로 설정하게 되면 1개의 로직 수행을 위해 N개의 쿼리가 발생할 수 있으며,<br>
사용되지 않는 엔티티를 Eager로 설정하면 사용하지도 않을 객체를 위한 쿼리가 늘어나게 된다.<br>
전자의 상황을 JPA의 `N+1 이슈`라고 말한다.<br>


## 예제 코드

```JAVA
public class Team {
    // ...
    @ManyToMany
    @JoinTable(name = "TEAM_WELFARE",
            joinColumns = @JoinColumn(name = "TEAM_ID"),
            inverseJoinColumns = @JoinColumn(name = "WELFARE_ID")
        )
    private List<Welfare> welfares = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "OFFICE_ID")
    private Office teamOffice;

    @OneToMany(mappedBy = "memberId", cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();
}
```





