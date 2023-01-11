## **스프링 빈이란?**

Spring Bean은 Spring IoC 컨테이너가 관리하는 "**오브젝트**"이다.  
근데 몇몇 블로그 글들을 보면 IoC 컨테이너가 관리하는 "**POJO**"라고 설명하고 있다.  
POJO 충족 조건에 대해 찾아보았다.

_1\. Extend prespecified classes  
2\. Implement prespecified interfaces  
**3\. Contain prespecified annotations**_

3번에서 어노테이션 포함하면 안 된다고 하는데 @Component, @Bean으로도 빈 등록하지 않나? 그럼 이건 POJO가 아닌 것이고 따라서 Bean이 아니게 되는 건가?

_Some developers prefer having the wiring close to the source while others argue that **annotated classes are no longer POJOs** and, furthermore, that the configuration becomes decentralized and harder to control.  
No matter the choice, **Spring can accommodate both styles** and even mix them together._

공식 문서에 의하면 어노테이션 붙으면 POJO가 아니라고 주장하는 사람들도 있지만 어쨌든 사용할 수 있다고 한다. 잠깐, 그럼 그 블로거들 말대로 Spring Bean이 POJO가 맞긴 한 건가? 스프링 공식 문서에서 Spring Bean에 대해 뒤져보았다.

_In Spring, **the objects** that form the backbone of your application and **that are managed by the Spring IoC container are called beans**. A bean is an object that is instantiated, assembled, and otherwise managed by a Spring IoC container._

정확히는 IoC 컨테이너에 의해 관리되는 **오브젝트**라고 설명하고 있다.  
Spring Bean은 항상 POJO인건가 아닌 건가 한참 고민하다가 위키피디아 POJO 문서를 다시 보았는데, 어노테이션 지웠을 때 POJO 되는 것들은 그냥 POJO라고 부르는 듯 하다.

_However, due to technical difficulties and other reasons, many software products or frameworks described as POJO-compliant actually still require the use of prespecified annotations for features such as persistence to work properly. The idea is that if the object (actually class) were a POJO before any annotations were added, and would return to POJO status if the annotations are removed then **it can still be considered a POJO**. Then the basic object remains a POJO in that it has no special characteristics (such as an implemented interface) that makes it a "Specialized Java Object" (SJO or (sic) SoJO)._

어쨌거나 스프링 공식문서에 의하면 **Spring Bean**은 Spring IoC 컨테이너가 관리하는 "POJO"가 아니라 "**Object**"이다.

## **빈 등록 과정**

**IoC Container**라고 함은 **BeanFactory**, **ApplicationContext**를 말하는데, 특별한 경우가 아니라면 **BeanFactory**의 확장체인 **ApplicationContext**를 사용하게 된다.

```
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {
        ...
        
        AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
```

**ApplicationContext**를 살펴보면 내부에 **AutowireCapableBeanFactory**가 합성 관계로 존재하는데, 얘가 의존 관계를 주입하면서 Bean을 생성해준다.

SpringBoot에서는 애플리케이션 종류에 따라 각기 다른 종류의 **ApplicationContext**의 구현체가 내부에 생성된다.

_웹 애플리케이션이 아닌 경우: AnnotationConfigApplicationContext_

_서블릿 기반의 웹 애플리케이션 (Spring WebMVC): **AnnotationConfigServletWebServerApplicationContext**_

_리액티브 웹 애플리케이션인 경우 (Spring WebFlux): AnnotationConfigReactiveWebServerApplicationContext_

얘네 3개는 전부 **GenericApplicationContext**를 상속받고 있는데, **GenericApplicationContext** 내부를 살펴보면 Bean들을 관리해주는 **DefaultListableBeanFactory**를 생성하고 있는 걸 알 수 있다.

```
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;

	public GenericApplicationContext() {
		this.beanFactory = new DefaultListableBeanFactory();
	}

	
	public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}
    
    	public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		this(beanFactory);
		setParent(parent);
	}
 
 ...
}
```

정리하자면 **ApplicationContext**는 빈들을 관리하는 **BeanFactory** 구현체인 **DefaultListableBeanFactory**를 합성 관계로 내부에 가지고 있고, **ApplicationContext**에 빈을 등록하거나 찾아달라는 처리 요청이 오면 **BeanFactory**로 이러한 요청을 위임하여 처리하는 것이다.

## **참고**

[https://en.wikipedia.org/wiki/Plain\_old\_Java\_object](https://en.wikipedia.org/wiki/Plain_old_Java_object)

[https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction)

[https://mangkyu.tistory.com/210](https://mangkyu.tistory.com/210)
