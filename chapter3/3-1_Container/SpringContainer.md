
# 스프링 컨테이너

`일반적인 의미의 컨테이너`: 인스턴스의 생명주기를 관리하며, 생성된 인스턴스들에게 추가적인 기능을 제공

`IoC Container`: 빈의 의존성을 관리하고, 객체를 생성하며, 생성된 객체를 빈으로 등록시키는 등의 관리 수행

<br>

---

<br>

####  빈 팩토리
 `org.springframework.beans.factory.BeanFactory`

팩토리 디자인 패턴을 구현한 것으로, 빈을 생성하고 관계를 설정하는 DI의 기본 기능 제공.

getBean()이 호출되면, 팩토리는 의존성 주입을 툥해 빈을 인스턴스화하고 특성을 설정함.



#### 어플리케이션 컨텍스트 ApplicationContect

`org.springframework.context.ApplicationContext`

어플리케이션 전반에 걸쳐 모든 구성요소의 제어 작업을 담당하는 IoC엔진. BeanFactory의 superset으로 AOP, 메시지 자원 처리, 이벤트 전이 등과 같은 기능들이 추가됨.


---

## ApplicationContext의 생성/초기화

![Alt text](https://docs.spring.io/spring-framework/docs/current/reference/html/images/container-magic.png)

ApplicationContext는 별도의 정보(configuration metadata)를 참조하여 빈의 생성, 관계설정 등의 제어 작업을 총괄한다.
이 때, 설정정보는 xml파일, 어노테이션, 자바코드 등으로 제공된다. 

즉, 개발자가 정의한 객체와 메타데이터를 참조하여 ApplicationContext를 초기화하여 프로그램 실행 환경을 제공한다.

<br>

## 예시 코드

### configuration metadata(annotation)
```JAVA
@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
```
`@Configuration` 어노테이션을 통해서 빈 팩토리를 위한 오브젝트 설정을 담당하는 클래스임을 명시한다.

`@Bean` 어노테이션을 통해 오브젝트를 생성해주는 메소드임을 명시한다.

이 두 어노테이션을 통해서 스프링 프레임워크의 빈 팩토리(어플리케이션 컨텍스트)가 IoC방식의 기능을 제공할 때 사용할 설정정보를 제공한다.

<br>

### configuration metadata(xml)
```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
	<bean id = "animal" class = "spring.entity.Animal">
          <constructor-arg type="java.lang.String" value="BINGO"/>
          <constructor-arg type="int" value="5"/>
	</bean>
	<bean id = "console" class = "spring.console.AnimalPrintAge">
		<property name = "animal" ref = "animal"/>
	</bean>

</beans>
```
xml태그를 통해 bean 정보를 명시


<br>

### ApplicationContext initialize

ApplicationContext를 만드는 방법은 어떠한 방식의 설정정보를 사용하느냐에 따라 달라진다. 

- ClassPathXmlApplicationContext: ClassPath에 위치한 xml파일을 읽어 설정정보 로딩

- FileSystemXmlApplicationContext: 파일 경로의 xml을 읽어 설정정보 로딩

- xmlWebApplicationContext: 웹 어플리케이션에 위치한 곳에서 xml파일을 읽어 설정정보 로딩

- AnnotationConfigApplicationContext: @Configuration 어노테이션이 붙은 클래스를 이용하여 설정정보 로딩

```JAVA
ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

UserDao dao = context.getBean("userDao", UserDao.class);
```
어노테이션 기반 ApplicationContext 생성

```JAVA
ApplicationContext context = 
		new ClassPathXmlApplicationContext("spring/di/setting.xml");
Animal animal = context.getBean("animal");
```
XML 기반 ApplicationContext 생성

<br>

---

<br>

## IoC Container

즉, `IoC컨테이너`는 객체 생성, 관계 설정, 소멸 등에 대해 관리하는 `ApplicationContext 구현체`라고 할 수 있다. <br>


### IoC Container 계층구조


![Alt text](https://jaehun2841.github.io/2018/10/21/2018-10-21-spring-context/99A34C3359FEAA8410.png)



스프링을 개발할 때, 이 어플리케이션 컨텍스트를 계층 구조를 띈다.

최상위의 부모역할의 `root-application context`와 `servlet-application context`로 구성되며, 계층 구조 안의 모든 컨텍스트는 각자 독립적인 설정정보를 통해서 빈을 생성하고 관리한다.


#### Application Context
- Web application 최상단에 위치하는 Context
- `root-context.xml`, `applicationContext.xml` 파일은 Application Context 생성 시 필요한 설정정보를 담은 파일 
- 특정 servlet 설정과 관계 없는 설정을 한다 (@Service, @Repository, @Configuration, @Component)
- 서로 다른 서블릿에서 공통적으로 공유해서 사용할 수 있는 빈을 선언
- Application Context의 빈은 Servlet Context에서 정의된 빈을 사용할 수 없다

#### Servlet Context

- 서블릿 단위로 생성되는 context
- 스프링에서 servlet-context.xml 파일은 DispatcherServlet 생성 시 필요한 설정 정보를 담은 파일(Interceptor, Bean생성, ViewResolver etc)
- URL 설정이 있는 Bean 생성 (@Controller, Interceptor)
- Application Context를 부모 context로 사용
- Bean 탐색 순서
    - Servlet Context 내 Bean 검색
    - Application Context 내 Bean 검색

>왜 계층을 나눠서 구성하는가?

<img src="/assets/images/container/application_context_hierachey.png">

1. 구성을 나누게 될 경우, 애플리케이션 컨텍스트는 트리 형식으로 구조화될 수 있다. 이렇게 되면, 자식 노드들은 루트를 통해 설정을 공유할 수 있다. 반면 형제 노드끼리는 독립적이기때문에 기존 설정을 수정하지 않고 일부 빈 구성에 대한 변경이 가능하다.


2. 





