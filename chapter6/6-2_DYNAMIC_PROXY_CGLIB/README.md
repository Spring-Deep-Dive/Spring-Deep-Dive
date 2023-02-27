Spring AOP는 프록시 기반으로 JDK Dynamic Proxy와 CGLIB을 활용하여 AOP 제공하고 있다.

이 글에서는 두 방식의 차이에 대해 정리한다.

### **차이점 핵심은 인터페이스 유무**

**Dynamic Proxy**는 인터페이스를 기반으로 프록시를 생성한다. Java.lang.reflect.Proxy의 newProxyInstance() 메소드를 통해 프록시 객체를 생성한다.

**CGLIB**은 클래스 기반으로 ASM 프레임워크를 이용하여 바이트 코드를 조작하고 프록시 객체를 생성한다.

간단한 코드로 이해해보자.

#### **Dynamic Proxy**

인터페이스 기반으로 프록시를 생성하므로 인터페이스부터 생성한다.

```
public interface Member {
    void join();
}
```

멤버 인터페이스에는 간단하게 가입하는 기능만 정의되어 있다.

```
public class Jiseunghyeon implements Member {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void join() {
        logger.info("{}님께서 멤버로 가입합니다.", this.getClass().getSimpleName());
    }
}
```

Member 인터페이스의 구현체를 하나 만들고, join() 메소드 안에서 로깅을 하도록 했다.

```
public class MemberProxyHandler implements InvocationHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Object target;

    public MemberProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        logger.info("Before");
        result = method.invoke(target, args);
        logger.info("After");

        return result;
    }
}
```

Aspect가 될 핸들러를 만들었다. 메서드 호출 전후로 로깅을 한다.

```
class MemberTest {

    @Test
    public void dynamicProxy() {
        Member jiseunghyeon = (Member) Proxy.newProxyInstance(
                Member.class.getClassLoader(),
                new Class[]{Member.class},
                new MemberProxyHandler(new Jiseunghyeon()));

        jiseunghyeon.join();
    }
}
```

```
11:33:16.112 [Test worker] INFO com.example.demo.MemberProxyHandler - Before
11:33:16.115 [Test worker] INFO com.example.demo.Jiseunghyeon - Jiseunghyeon님께서 멤버로 가입합니다.
11:33:16.117 [Test worker] INFO com.example.demo.MemberProxyHandler - After
```

단순히 newProxyInstance() 메소드를 사용하여 프록시 객체를 만들고, join() 메소드를 호출하는 테스트 코드다.

여기서 프록시 객체가 생성되는 과정은 다음과 같다.

1.  타깃의 인터페이스를 자체적인 검증 로직을 통해 ProxyFactory에 의해 타깃의 인터페이스를 상속한 Proxy 객체 생성
2.  Proxy 객체에 InvocationHandler를 포함시켜 하나의 객체로 반환

핵심은 **인터페이스**를 기준으로 프록시를 생성한다는 점이다.

이를 이해하지 못한다면 다음과 같은 코드를 짜게 될 수 있다.

```
class MemberTest {

    @Test
    public void dynamicProxy() {
    	// 런타임 에러
        Jiseunghyeon jiseunghyeon = (Jiseunghyeon) Proxy.newProxyInstance(
                Member.class.getClassLoader(),
                new Class[]{Member.class},
                new MemberProxyHandler(new Jiseunghyeon()));

        jiseunghyeon.join();
    }
}
```

Jiseunghyeon 클래스는 Member 인터페이스를 상속받고 있기 때문에, Dynamic Proxy 방식으로 프록시 객체를 생성한다. 위와 같은 코드에선 인터페이스 타입이 아닌 클래스 타입으로 지정해줬고, 런타임 에러가 발생하게 된다.

따라서 Dynamic Proxy 방식에서 구현체는 인터페이스를 상속받아야 하고, 프록시 객체를 사용하기 위해선 반드시 인터페이스 타입으로 지정해줘야 한다.

#### **CGLIB**

이번엔 CGLib을 이용한 코드를 작성해보자.

클래스의 바이트 코드를 조작하여 프록시 객체를 생성하므로, 인터페이스가 필요 없다.

```
public class Jiseunghyeon {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void join() {
        logger.info("{}님께서 멤버로 가입합니다.", ClassUtils.getUserClass(this).getSimpleName());
    }
}
```

join() 메소드 안에서 메소드 이름을 출력하는 부분이 바뀌었는데, 클래스를 감싼 프록시 객체가 아닌 클래스 이름을 출력하기 위함이다.

```
public class JiseunghyeonLogInterceptor implements MethodInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;
        logger.info("Before");
        result = proxy.invokeSuper(obj, args);
        logger.info("After");
        return result;
    }
}
```

org.springframework.cglib.proxy.MethodInterceptor를 사용하여 호출 전후로 로깅을 한다.

```
@Test
public void cglib() {
    Enhancer jiseunghyeonEnhancer = new Enhancer();
    jiseunghyeonEnhancer.setSuperclass(Jiseunghyeon.class);
    jiseunghyeonEnhancer.setCallback(new JiseunghyeonLogInterceptor());
    Jiseunghyeon jiseunghyeon = (Jiseunghyeon) jiseunghyeonEnhancer.create();

    jiseunghyeon.join();
}
```

```
13:06:59.921 [Test worker] INFO com.example.demo.JiseunghyeonLogInterceptor - Before
13:06:59.949 [Test worker] INFO com.example.demo.Jiseunghyeon$$EnhancerByCGLIB$$6bcfec37 - Jiseunghyeon님께서 멤버로 가입합니다.
13:06:59.950 [Test worker] INFO com.example.demo.JiseunghyeonLogInterceptor - After
```

CGLIB의 Enhancer라는 클래스를 이용하면 프록시 객체를 생성할 수 있다.

setSuperClass()로 프록시 할 객체를 정해주고, setCallback으로 인터셉터를 정해줬다.

실행 후 로그를 보면 **Jiseunghyeon$$EnhancerByCGLIB$$6bcfec37** 프록시 객체의 메소드가 호출된 것을 확인할 수 있다.

CGLIB은 Target 클래스에 포함된 모든 메소드를 재정의하여 프록시 객체를 생성한다. 때문에 final 메소드 또는 클래스에 대한 재정의를 할 수 없으므로 이에 대한 프록시 객체를 생성할 수 없다는 단점이 있다.

그러나 바이트코드를 조작하여 프록시 객체를 생성해주고, 한번 호출 이후 조작된 바이트코드를 재사용하기 때문에 퍼포먼스에 대한 측면에선 Dynamic Proxy보다 우수하다.

### **ProxyFactory**

인터페이스를 구현하면 Dynamic Proxy, 아니면 CGLIB으로 프록시를 생성하는 것은 ProxyFactory가 인터페이스 유무를 판단해주었기 때문이다. 따라서 ProxyFactory로 다음과 같은 테스트가 가능하다.

```
@Test
@DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
void interfaceProxy(){
    MemberService target = new MemberServiceImpl();
    ProxyFactory proxyFactory = new ProxyFactory(target);
    MemberService proxy = (MemberService) proxyFactory.getProxy();
    logger.info("targetClass = {}", target.getClass());
    logger.info("proxyClass = {}", proxy.getClass());

    assertTrue(AopUtils.isAopProxy(proxy));
    assertTrue(AopUtils.isJdkDynamicProxy(proxy));
    assertFalse(AopUtils.isCglibProxy(proxy));
}

@Test
@DisplayName("구체 클래스만 있으면 CGLIB 사용")
void concreteProxy(){
    FooService target = new FooService();
    ProxyFactory proxyFactory = new ProxyFactory(target);
    FooService proxy = (FooService) proxyFactory.getProxy();
    logger.info("targetClass = {}", target.getClass());
    logger.info("proxyClass = {}", proxy.getClass());

    assertTrue(AopUtils.isAopProxy(proxy));
    assertFalse(AopUtils.isJdkDynamicProxy(proxy));
    assertTrue(AopUtils.isCglibProxy(proxy));
}

@Test
@DisplayName("proxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용")
void proxyTargetClass(){
    MemberService target = new MemberServiceImpl();
    ProxyFactory proxyFactory = new ProxyFactory(target);
    proxyFactory.setProxyTargetClass(true);
    MemberService proxy = (MemberService) proxyFactory.getProxy();
    logger.info("targetClass = {}", target.getClass());
    logger.info("proxyClass = {}", proxy.getClass());

    assertTrue(AopUtils.isAopProxy(proxy));
    assertFalse(AopUtils.isJdkDynamicProxy(proxy));
    assertTrue(AopUtils.isCglibProxy(proxy));
}
```

### **Spring Boot에선 기본적으로 CGLIB를 사용**

@EnableAspectJAutoProxy(proxyTargetClass = true)를 쓰게되면 항상 CGLIB으로 프록시를 생성하게 된다.

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

	boolean proxyTargetClass() default false;
	boolean exposeProxy() default false;
}
```

일단, 보다시피 proxyTargetClass()의 기본값은 false이다. 그런데 Spring Boot는 어떻게 기본값을 true로 설정할까?

Spring Boot는 애플리케이션을 실행할 때 AutoConfigure를 위한 정보들을 spring-boot-autoconfigure의 spring-configuration-metadata.json에서 관리하고 있다.

그리고 AutoConfigure를 진행할 때 해당 값을 참조해서 설정을 진행한다. 그러므로 proxyTargetClass의 기본값이 true라는 것은 Spring MVC가 아닌 Spring Boot에서만 해당하는 내용이다. 만약 Spring Boot에서 proxyTargetClass의 값을 false로 설정하고 싶다면 프로퍼티에서 defaultValue를 false로 주면 된다.

### 참고

[https://jojoldu.tistory.com/71](https://jojoldu.tistory.com/71)

[https://gmoon92.github.io/spring/aop/2019/04/20/jdk-dynamic-proxy-and-cglib.html](https://gmoon92.github.io/spring/aop/2019/04/20/jdk-dynamic-proxy-and-cglib.html)

[https://velog.io/@gmtmoney2357/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-%EB%8F%99%EC%A0%81-%ED%94%84%EB%A1%9D%EC%8B%9C-%EA%B8%B0%EC%88%A0CGLIB-ProxyFactory](https://velog.io/@gmtmoney2357/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B6%80%ED%8A%B8-%EB%8F%99%EC%A0%81-%ED%94%84%EB%A1%9D%EC%8B%9C-%EA%B8%B0%EC%88%A0CGLIB-ProxyFactory)

[https://mangkyu.tistory.com/175](https://mangkyu.tistory.com/175)
