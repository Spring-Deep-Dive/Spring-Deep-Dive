# Spring Triangle (PSA, IOC , AOP)

---

## Spring ****Triangle****

스프링 트라이앵글이란 스프링을 구성하는 3대 핵심요소이다.

---

### IoC (Inversion of Control)

- IoC는 제어의 역전이란 의미로 객체의 생성 및 의존 관리 등의 생명 주기를 개발자가 직접 하는것이 아니라, 객체의 생명주기에 대한 권한을 스프링 컨테이너에게 위임한다.
- 객체 생명주기의 관리 주체가 개발자 → 스프링 컨테이너로 바뀌었으므로, 이를 제어의 역전 IoC라고 부른다.
- 이전에는 인터페이스를 구현하는 구현체를 직접 주입하여 사용했지만, 요구 사항이 변경될 시, 코드의 수정이 일어나야하고, [DIP 원칙](https://yoongrammer.tistory.com/100)에 어긋나는 코드이다.
- IoC로 인하여 개발자는 비즈니스 로직에 더욱 집중할 수 있고, 객체간 결합도를 낮춰 유지보수성이 좋은 코드 작성이 가능하다.
- `@ComponentScan`  어노테이션이 붙은 클래스를 찾아 그 클래스에 속한 하위 클래스들의 설정된 클래스들의 컴포넌트들을 등록한다.

```jsx
@Component
@Service
@Controller
@Repository
```

위 어노테이션들이 IoC컨테이너에 등록될 대상이 되고, 위 어노테이션들을 **[라이프 사이클 콜백](https://velog.io/@hyungjungoo95/Spring-%EB%B9%88-%EC%83%9D%EB%AA%85%EC%A3%BC%EA%B8%B0-%EC%BD%9C%EB%B0%B1)**이라고 부른다

- 위 방법대로 사용하면 Bean 객체로써 IoC 컨테이너가 관리하는 객체가 되고, 이를 적절하게 주입한다.

- Bean 객체를 직접 정의하는 방법

```java
@Configuration
public class SampleConfig {

    @Bean
    public SampleController sampleController() {
        return new SampleController;
    }
}
```

`Configuration` 어노테이션을 추가함으로써, 컴포넌트 스캔 대상이 되게 설정하고, 해당 클래스 안에서 Bean 어노테이션으로 사용하여 IoC 컨테이너에 직접 정의할 수 있다.

---

### AOP (**Aspect Oriented Programming)**

AOP는 관심사 기반의 프로그래밍이다.

관심사 기반이라는 말이 어렵게 들릴 수 있지만,

많은 부분에 동일하게 들어간 코드들을 한 클래스에서 관리하면서

해당 코드가 들어갈 부분을 지정하여 비즈니스 로직과 새로 추가되어야하는 기능에 대해 관심사를 분리하는데에 목적이 있다.

### 동작 방식

- Spring은 AOP를 구현할 때, [프록시 패턴](https://coding-factory.tistory.com/711)이라는 디자인 패턴을 적용하는데 어떤 한 클래스의 빈이 생성되는 시점에 프록시 객체를 만들어서 중간에 끼워 넣고 이를 대신 빈으로 등록한다.

### 예제

```java

public class TxExample {
    private TransactionManager tx;

    public void query(String query) {
        try {
            tx.query(query);
						tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }
    }
}
```

DB에 쿼리를 날리는 데이터 접근 계층이 있고 쿼리를 처리하다가 Exception이 발생하면 해당 트랜잭션을 롤백하는 코드가 있다고 가정하자.

이 코드가 단순 한 두개만 있다면 작성하면 그만이지만, 도메인 별로 데이터 접근 계층이 많아질수록, 매 번 해당 코드를 작성해줘야하는 불편함이 있다.

```java
@Transactional
```

어노테이션이 Spring AOP 기능을 사용한 어노테이션에 해당하는데, Transactional 어노테이션은 데이터 접근 계층에 앞 뒤에 setAutoCommit()과 commit(),rollback()을 추가하여 매 번 같은 코드의 양을 줄일 수 있는 것이다.

[구체적인 사용 방법 참고](https://engkimbs.tistory.com/746)

---

## PSA (Portable Service Abstraction)

- PSA란?
    - 어떤 기술을 사용할 때 그 기술의 내부 구조를 감추고, 개발자에게 간편하게 사용할 수 있는 API를 제공하는 것.

---

### 예제

```java
public class ServletExample extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
```

서블릿 예시 코드

- Spring PSA 이후

```java
@Controller("/api/example")
public class ServletExample2 {
    @GetMapping("/cook")
    protected String getCook() {
        return "PSA Cook !";
    }

    @PostMapping
    protected int postExample() {
        return 1;
    }
}
```

- 위 예시 코드는 기존 서블릿을 Spring이 Spring MVC를 추상화하여 Get,Post 등 다양한 매핑을 개발자가 손쉽게 사용할 수 있도록 적용된 코드의 예시이다.

### 예시 2

```java
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.show_sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.use_sql_comments=false
```

Spring Data JPA를 사용할 때, 위와 같이 *.properties 파일에 설정을 할 수 있는데.

만약 PSA가 적용되지 않고 DB를 다른 DB 바꿔야하는 상황이 온다면 코드의 수정이 해당 DB에 맞게 수정이 일어나야한다.

그렇지만 Spring은 PSA를 통해 내부를 추상화하여 DB의 방언에 맞게 알아서 변경해주고, 개발자는 사용할 DB만 잘 설정해주면 된다.

---

## 참고 자료

[[예제로 배우는 스프링 입문] Spring IoC, AOP, PSA](https://zion830.tistory.com/109)

[[Spring] 스프링 AOP 개념 이해 및 적용 방법](https://atoz-develop.tistory.com/entry/Spring-%EC%8A%A4%ED%94%84%EB%A7%81-AOP-%EA%B0%9C%EB%85%90-%EC%9D%B4%ED%95%B4-%EB%B0%8F-%EC%A0%81%EC%9A%A9-%EB%B0%A9%EB%B2%95)

[[디자인패턴] 프록시패턴(Proxy Pattern)](https://velog.io/@newtownboy/%EB%94%94%EC%9E%90%EC%9D%B8%ED%8C%A8%ED%84%B4-%ED%94%84%EB%A1%9D%EC%8B%9C%ED%8C%A8%ED%84%B4Proxy-Pattern)

[[Design Pattern] 프록시 패턴(Proxy Pattern)에 대하여](https://coding-factory.tistory.com/711)

[[Spring] PSA(Portable Service Abstraction)란?](https://dev-coco.tistory.com/83)%