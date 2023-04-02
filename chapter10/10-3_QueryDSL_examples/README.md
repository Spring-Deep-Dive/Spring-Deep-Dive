### 1\. 조건

**com.querydsl.core.types.dsl.SimpleExpression.java**에서 사용가능한 검색 조건 메서드를 확인할 수 있다.

```
member.name.eq("member1") // username = 'member1'
member.name.ne("member1") // username != 'member1'
member.name.eq("member1").not() // username != 'member1'
member.name.isNotNull() // name is not null
member.name.like("member%") // like member% escape '!'
member.name.startsWith("member") // like ‘member%’
member.name.contains("member") // like ‘%member%'

member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) // between 10, 30
member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30
```

WHERE절에서 AND 조건을 파라미터로 처리 가능하다.

**com.querydsl.core.support.QueryBase.java**를 확인해보면 where 메서드에서 파라미터로 Predicate를 받고있음을 확인할 수 있다.

```
public Q where(Predicate o) {
    return queryMixin.where(o);
}

public Q where(Predicate... o) {
    return queryMixin.where(o);
}
```

따라서 다음과 같이 사용 가능하다.

```
member.name.startsWith("member").and(member.age.in(10, 20))

member.name.startsWith("member"), member.age.in(10, 20))
```

### 2\. 조회

```
// fetch(): List<T> 반환
List<Member> members = query.from(member).fetch();

// fetchOne(): T 반환
Member found = query.from(member).where(member.id.eq(1L)).fetchOne();

// fetchFirst(): limit(1).fetchOne() 반환
Member found = query.from(member).fetchFirst();
```

### 3\. 정렬

```
member.age.asc() // 오름차순
member.age.desc() // 내림차순
member.age.asc().nullFirst() // null일 경우 가장 앞으로
member.age.asc().nullLast() // null일 경우 가장 뒤로
```

### 4\. 페이징

```
// offset(long offset): offset 적용
query.from(member).offset(50).fetch();
                
// limit(long limit): limit 적용
query.from(member).limit(50).fetch();
```

### 5\. 집합

JPAQuery의 select를 사용하여 집합 결과를 얻을 수 있다.

```
@Override
public JPAQuery<Tuple> select(Expression<?>... exprs) {
    queryMixin.setProjection(exprs);
    JPAQuery<Tuple> newType = (JPAQuery<Tuple>) this;
    return newType;
}
```

다음과 같이 groupBy, having을 사용할 수 있다.

```
List<Tuple> result = query
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .groupBy(member.age)
                .having(member.age.goe(20))
                .fetch();
```

### 6\. 조인

```
// inner join
query.select(post).from(post).join(post.author, member).fetch();

// left join
query.select(post).from(post).leftJoin(post.author, member).fetch();
                
// right join
query.select(post).from(post).rightJoin(post.author, member).fetch();

// theta join
query.select(post).from(post, member).where(member.name.eq(post.title)).fetch();
```

JPA 2.1부터 on절을 사용하여 다음과 같이 작성 가능하다.

```
query.select(post, member)
    .from(post)
    .leftJoin(post.author, member)
    .on(post.author.name.eq("member1"))
    .fetch();
```

### 7\. 페치 조인

페치 조인을 사용하여 연관관계에서 FetchType이 LAZY인 엔티티들을 한번에 가져올 수 있다.

```
Post found = query
                .select(post)
                .from(post)
                .where(post.title.eq("post1"))
                .fetchOne();
boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(found.getAuthor());
assertThat(isLoaded).isFalse();
```

```
select p1_0.id, p1_0.author_id, p1_0.content, p1_0.title 
from post p1_0 
where p1_0.title=?
```

Member와 Post는 1:N 관계이고, FetchType을 Lazy로 설정했기 때문에 Member를 조회하는 SQL이 생성되지 않고, 엔티티가 컨텍스트에 로딩되지 않는다.

```
Post found = query
                .select(post)
                .from(post)
                .join(post.author, member)
                .where(post.title.eq("post1"))
                .fetchOne();
boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(found.getAuthor());
assertThat(isLoaded).isFalse();
```

```
select p1_0.id, p1_0.author_id, p1_0.content, p1_0.title 
from post p1_0 
join member a1_0 
	on a1_0.id=p1_0.author_id 
where p1_0.title=?
```

Join 절이 추가됐지만 마찬가지로 Member에 대한 조회가 발생하지 않는다.

```
Post found = query
                .select(post)
                .from(post)
                .join(post.author, member)
                .fetchJoin()
                .where(post.title.eq("post1"))
                .fetchOne();
boolean isLoaded = emf.getPersistenceUnitUtil().isLoaded(found.getAuthor());
assertThat(isLoaded).isTrue();
```

```
select p1_0.id, a1_0.id, a1_0.age, a1_0.name, p1_0.content, p1_0.title 
from post p1_0 
join member a1_0 
	on a1_0.id=p1_0.author_id 
where p1_0.title=?
```

fetchJoin()을 사용하면 Post와 연관된 Member도 즉시 로딩하게 된다.
