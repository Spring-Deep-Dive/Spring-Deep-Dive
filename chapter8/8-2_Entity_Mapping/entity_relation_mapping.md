# 연관관계 매핑

## 연관관계
ORM기술은 객체와 테이블 중심의 각기 다른 패러다임에서 발생하는 문제를 해결하기 위해 등장했으며, 이를 통해 개발자는 서비스 로직을 짜면서 객체에 온전히 집중하여 개발할 수 있게 되고, DB의 테이블에 대한 고민을 최소화 할 수 있다.<br><br>

서로 다른 패러다임이지만, 둘 모두 `연관관계`가 존재한다. OOP에서의 연관관계는 객체 간 협력을 목표로 하며, DB의 테이블에서는 효율적으로 데이터를 적재/관리하기 위함이다.<br><br>

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

| id | nickname | teamId |
|:--:|:--------:|:------:|
| 0  |   John   |   10   |

| id |   name  |
|:--:|:-------:|
| 10  | Spring |


### 테이블 세계의 연관관계


### 객체 세계의 연관관계


