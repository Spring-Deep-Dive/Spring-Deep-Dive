Projection의 대상이 한 컬럼일 경우 그냥 아래와 같이 쓸 수 있다.

```
List<Account> result = queryFactory.select(account)
            .from(account)
            .fetch();
```

그러나 Projection의 대상이 두 컬럼 이상일 경우 반환타입이 Tuple이다. 따라서 아래와 같이 사용해야 한다.

```
List<Tuple> result = queryFactory.select(account.name, account.age)
            .from(account)
            .fetch();
```

Tuple은 Model 객체를 로직에서 사용하는 것과 같은 문제를 가지고 있기 때문에 Repository 안에서만 쓰는 걸 권장한다.

그래서 우리는 엔티티의 DTO를 만들어서 반환받게 코드를 작성하게 되는데, 이때 Projection이 유용하게 사용될 수 있다.

Project을 이용하는 방법은 크게 4가지다.

1.  **Projections.bean 사용 (Default Constructor 필요, Setter 필요)**
2.  **Projections.fields 사용 (Default Constructor 불필요, Setter 불필요)**
3.  **Projections.constructor 사용 (All Arguments Constructor 필요, Setter 불필요)**
4.  **@QueryProjection 사용**

결론부터 말하자면 **@QueryProjection** 사용이 가장 권장된다. 각 패턴의 장단점을 훑어보자.

### **Projections.bean**

```
@ToString
@Setter
public class AccountDto {

    private String name;
    private int age;
}
```

```
List<AccountDto> result = queryFactory
            .select(Projections.bean(AccountDto.class, account.name, account.age))
            .from(account)
            .fetch();
result.forEach(System.out::println);
```

```
AccountDto(name=Account1, age=1)
AccountDto(name=Account2, age=2)
AccountDto(name=Account3, age=3)
AccountDto(name=Account4, age=4)
...
```

**Projections.bean** 방식은 Setter 기반으로 작동한다. 그래서 DTO에 Setter를 달아줘야 한다.

그런데 Setter를 사용하게 되면 Response, Request로 사용될 데이터를 담은 객체(DTO)가 도중에 변경될 위험이 있어보인다.

DTO는 Immutable한 것이 좋다고 생각하기 때문에 이 방법은 지양한다.

### **Projections.feilds**

```
@ToString
public class AccountDto {

    private String name;
    private int age;
}
```

```
List<AccountDto> result = queryFactory
            .select(Projections.fields(AccountDto.class, account.name, account.age))
            .from(account)
            .fetch();
```

AccountDto 클래스에서 Setter를 없앴고, 위의 코드에서 bean을 fields로 바꾸었다.

result에 담기는 결과값은 bean 방식과 동일하다.

Setter를 없앴으니 bean 방식보다 좀더 만족스럽다.

그러나 fields 방식은 필드 이름을 기준으로 매핑을 하기 때문에, DTO와 엔티티의 필드 이름을 맞춰줘야 한다.

여전히 불만족스럽다.

### **Projections.constructor**

```
@ToString
@AllArgsConstructor
public class AccountDto {

    private String n;
    private int a;
}
```

```
List<AccountDto> result = queryFactory
            .select(Projections.constructor(AccountDto.class, account.name, account.age))
            .from(account)
            .fetch();
```

AccountDto 클래스에 모든 필드를 파라미터로 갖는 생성자를 추가하고, feilds를 constructor로 바꾸었다.

이 경우도 마찬가지로 결과값이 같다.

DTO의 필드 이름이 엔티티의 필드 이름과 달라도 되고, Setter도 없어서 DTO를 Immutable하게 가져갈 수 있다.

다만 바인딩 시 **주의할 점**이 있다.

```
public static <T> ConstructorExpression<T> constructor(Class<? extends T> type, Expression<?>... exprs) {
	return new ConstructorExpression<T>(type, exprs);
}
```

constructor 방식은 DTO의 생성자에 바인딩 하는 것이 아니라, Expression<?>... 값을 넘기는 방식으로 진행한다.

따라서 값을 넘길 때 생성자의 파라미터와 순서를 정확히 일치시켜야 한다.

값이 많아질 경우 개발자가 순서 입력에서 실수할 수 있는 여지가 있다.

### **@QueryProjection**

```
@ToString
public class AccountDto {

    private String n;
    private int a;

    @QueryProjection
    public AccountDto(String n, int a) {
        this.n = n;
        this.a = a;
    }
}
```

DTO에 All Arguments Constructor를 만들고, @QueryProjection을 붙여준다.

컴파일을 해보면 다음과 같은 자바 파일이 생성돼있음을 확인할 수 있다.

```
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAccountDto extends ConstructorExpression<AccountDto> {

    private static final long serialVersionUID = -153527146L;

    public QAccountDto(com.querydsl.core.types.Expression<Integer> a, com.querydsl.core.types.Expression<String> n) {
        super(AccountDto.class, new Class<?>[]{int.class, String.class}, n, a);
    }

}
```

이후엔 이 클래스를 다음과 같이 사용하면 된다.

```
List<AccountDto> result = queryFactory
            .select(new QAccountDto(account.age, account.name)) // 순서 바꾸면 컴파일 에러
            .from(account)
            .fetch();
```

파라미터 순서를 정확히 입력해주지 않으면 컴파일 타임에 에러를 발생시키기 때문에 다른 방법들보다 더욱 안전해졌다.

### **DTO까지 QueryDSL이랑 의존성 연결을 해야하냐?**

싫으면 안 해도 된다. @QueryProjection 방식은 Projection을 더 안전하게 사용할 수 있게 해주지만, 어쨌든 추후에 규격이 바뀌든 Datasource가 바뀌든 그 영향이 DTO까지 미치게 된다.

There's no silver bullet. 정답은 없다.

런타임의 안전성을 취할 것인지 DTO와의 decoupling으로 좀 더 객체지향적으로 설계할 것인지 생각해 보아야 한다.

### **참고**

[http://querydsl.com/static/querydsl/4.0.1/reference/ko-KR/html\_single/](http://querydsl.com/static/querydsl/4.0.1/reference/ko-KR/html_single/)

[https://devkingdom.tistory.com/253](https://devkingdom.tistory.com/253)

[https://cheese10yun.github.io/querydsl-projections/](https://cheese10yun.github.io/querydsl-projections/)

[https://jaime-note.tistory.com/75](https://jaime-note.tistory.com/75)
