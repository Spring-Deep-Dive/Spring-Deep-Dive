
# 스프링 컨테이너

- 스프링 컨테이너의 개념 및 구조
- IoC
- 예시코드

---

`IoC Container`: 어플리케이션 클래스를 초기화하며, 객체(빈)의 생성 및 관리 작업을 수행한다.

Bean Factory: 빈을 생성하고 관계를 설정하는 IoC의 기본 기능
BeanFactory 인터페이스: 객체 관리 기능 제공


Application Context: 어플리케이션 전반에 걸쳐 모든 구성요소의 제어 작업을 담당하는 IoC엔진. BeanFactory의 superset으로 AOP, 메시지 자원 처리, 이벤트 전이 등과 같은 기능들이 추가됨.


`org.springframework.context.ApplicationContext`는 스프링 IoC 컨테이너를 대표하는 인터페이스로, Bean의 생성 및 관리 역할을 담당한다. 

---


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
