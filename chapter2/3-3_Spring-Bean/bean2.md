### **Bean Annotations**

**1\. @Bean**

```
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
	
    @AliasFor("name")
    String[] value() default {};

    @AliasFor("value")
    String[] name() default {};

    boolean autowireCandidate() default true;

    String initMethod() default "";

    String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;
}
```

어노테이션이나 메서드 위에 붙여줄 수 있다. 메서드 위에 붙였을 경우 메서드 이름으로 Bean 이름이 결정된다. 개발자가 직접 제어가 불가능한 라이브러리를 사용할 때 붙여주면 된다.

**2\. @Configuration**

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	@AliasFor(annotation = Component.class)
	String value() default "";

	boolean proxyBeanMethods() default true;

	boolean enforceUniqueMethods() default true;
}
```

@Configuration 클래스 먼저 빈 등록하고, 해당 클래스를 파싱 해서 클래스 내부의 @Bean 메서드들을 찾아 빈 등록을 해준다. @Configuration 안에서 @Bean을 써야 Singleton을 보장해 주기 때문에 특별한 경우가 아니라면 @Bean은 @Configuration과 함께 쓰게 된다.

**3\. @Component**

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Component {

	String value() default "";
}
```

스프링은 Component Scan 할 때 @Component가 붙은 클래스들(@Component, @Configuration, @Controller, @RestController, @Service, @Repository)을 스캔해서 빈 등록을 자동으로 해준다. @ComponentScan으로 탐색 범위를 지정해 줘야 한다.

### **Bean Scopes**

| **Scope** | **Description** |  |
| --- | --- | --- |
| [singleton](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-singleton) | (Default) Scopes a single bean definition to a single object instance per Spring IoC container. |  |
| [prototype](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-prototype) | Scopes a single bean definition to any number of object instances. |  |
| [request](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-request) | Scopes a single bean definition to the lifecycle of a single HTTP request; that is, each HTTP request has its own instance of a bean created off the back of a single bean definition. Only valid in the context of a web-aware Spring ApplicationContext. |  |
| [session](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-session) | Scopes a single bean definition to the lifecycle of an HTTP Session. Only valid in the context of a web-aware Spring ApplicationContext. |  |
| [global session](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-global-session) | Scopes a single bean definition to the lifecycle of a global HTTP Session. Typically only valid when used in a portlet context. Only valid in the context of a web-aware Spring ApplicationContext. |  |
| [application](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes-application) | Scopes a single bean definition to the lifecycle of a ServletContext. Only valid in the context of a web-aware Spring ApplicationContext. |  |

스프링은 기본적으로 모든 Bean을 Singleton으로 생성하여 관리한다. Spring IoC Container에 하나의 인스턴스만 등록된다. 이는 CGLIB이 프록시 객체를 생성하여 Bean으로 등록하기 때문이다. 예를 들어,

```
@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }
}
```

memberRepository 메서드는 memberService, orderService 에서 호출되어 각각 새로운 인스턴스를 생성하고 있지만 Bean은 프록시 객체가 등록되어 있기 때문에 싱글톤이 보장된다. 스프링이 @Configuration이 붙은 AppConfig를 상속한 임의의 프록시 객체를 만들고 빈으로 등록해 주기 때문이다. @Configuration 옵션인 proxyBeanMethods을 false로 주게 되면 프록시 객체를 생성하지 않는다.

### **Bean Life Cycles**

컨테이너 생성 => 빈 생성 => 의존관계 주입 => 초기화 콜백 => 사용 => 소멸 전 콜백

스프링은 의존관계 주입이 완료된 후 스프링 빈에 콜백 메서드를 통해서 초기화 시점을 알려준다.

크게 3가지 방법으로 빈 라이프사이클 콜백을 지원한다.

**1\. 인터페이스**

```
public class HelloWorld implements InitializingBean, DisposableBean {
 
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Bean HelloWorld has been instantiated and I'm the init() method");
    }
 
    @Override
    public void destroy() throws Exception {
        System.out.println("Container has been closed and I'm the destroy() method");
    }
}
```

InitializingBean은 afterPropertiesSet 메서드로 초기화 지원, DisposableBean는 destroy 메서드로 소멸을 지원한다.

**2\. XML**

```
<!DOCTYPE
    beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN"
        "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
             
<beans>
    <bean id="hw" class="beans.HelloWorld" init-method="init" destroy-method="destroy"/>
</beans>
```

xml로 빈 등록 시 생명주기 설정을 할 수 있다.

**3\. 어노테이션**

```
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class HelloWorld {

    @PostConstruct
    public void init() throws Exception {
        System.out.println("Bean HelloWorld has been instantiated and I'm the init() method");
    }

    @PreDestroy
    public void destroy() throws Exception {
        System.out.println("Container has been closed and I'm the destroy() method");
    }
}
```

어노테이션 패키지를 보면 알겠지만 스프링에 종속적이지 않다. JSR-250 자바 표준이다. 스프링이 아니더라도 동작한다. 한 가지 단점으로, 외부 라이브러리에는 적용할 수 없다.

**참고**

[https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes](https://docs.spring.io/spring-framework/docs/4.2.5.RELEASE/spring-framework-reference/html/beans.html#beans-factory-scopes)

[https://www.geeksforgeeks.org/bean-life-cycle-in-java-spring/](https://www.geeksforgeeks.org/bean-life-cycle-in-java-spring/)
