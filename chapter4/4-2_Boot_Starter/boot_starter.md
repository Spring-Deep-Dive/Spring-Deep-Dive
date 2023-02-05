# Spring Boot

> Spring Boot helps you to create stand-alone, production-grade Spring-based applications that you can run. We take an opinionated view of the Spring platform and third-party libraries, so that you can get started with minimum fuss. Most Spring Boot applications need very little Spring configuration.

<br>

스프링 부트는 한 번에(stand-alone) 프로덕션 레벨(production-grade)의 스프링 기반(spring-based)어플리케이션을 생성하고 실행할 수 있도록 해준다. 스프링 부트에서 스프링 플랫폼과 써드파티 라이브러리에 대한 관리를 제공함으로써 개발자는 환결설정으로부터 더 자유로워질 수 있다.

## Spring vs Spring Boot

Spring은 엔터프라이즈 레벨의 어플리케이션 제작을 위해 사용되는 경량 오픈소스 프레임워크로, Spring AOP, Spring ORM, Spring Web MVC 등과 같은 다양한 모듈(서브 프레임워크)로 구성되어있다.

Spring Boot는 Spring 프레임워크의 모든 기능과 모듈을 지원하는 마이크로서비스 기반의 프레임워크를 말하며, 모든 것에 대한 자동 환경설정을 제공한다. 

| Spring | Spring Boot |
|:------:|:-----------:|
| DI 제공 | Auto Configuration 제공|
| 낮은 결합도 | stand-alone 어플리케이션 환경 제공|
| 실행을 위한 명시적 설정 | 임베디드 서버 등 제공 |
| DB 설정 필요 | in-memery DB 제공 |
| pom.xml을 통한 의존성 명시 | pom.xml이 내부적으로 필요한 의존성 관리 |

## Installation

Spring Boot는 다른 자바 라이브러리와 마찬가지로 jar파일(spring-boot-*.jar)을 클래스패스에 포함시켜서 사용된다. Maven/Gradle과 같은 의존성 관리 빌드 툴 사용을 권장하고 있다.

## Build System

Spring Boot의 각 버전에서는 지원하고 있는 dependency 리스트를 제공한다. 따라서, 버전을 명시하지 않더라도 명시되어 있는 버전으로 설정되며, Spring Boot 버전을 업그레이드하더라도 관련 dependency 또한 업그레이드된다.

*스프링에서는 버전을 명시하지 않는 것을 더 권장한다.

## Starter
Starter는 스프링 기술과 관련되어 있는 dependency들을 그룹화하여 제공한다. 스프링과 JPA를 사용하여 데이터베이스 조작을 하려고 한다면 `spring-boot-starter-data-jap`만 추가해주면 관련 dependency들이 모두 설치되는 겪이다.

starter는 다음과 같은 패턴을 지닌다.<br>
`spring-boot-start-*`<br>
*에는 관련 기술이 위치한다.

|          name             |                        descrption                      |
|:-------------------------:|:-------------------------------------------------------|
| spring-boot-starter       | Core 스타터로, auth-configuration, logging, YAML 포함     |
| spring-boot-starter-aop   | aspect-oriented programming을 위한 Spring AOP, AspectJ  |
| spring-boot-starter-cache | 스프링 프레임워크의 캐싱을 지원                               |
| spring-boot-starter-json  | JSON 입출력 지원                                         |

## Configuration
Spring Boot는 자바 기반의 환경설정을 권장한다. 인터넷에서 대부분 XML을 통한 설정을 안내하는데, 가능하다면 `Enable*`으로 시작하는 어노테이션을 통한 자바수준의 환경설정을 추천한다.

### Auto-configuration
Spring Boot의 auto-configuration은 `@EnableAutoConfiguration` 또는 `@SpringBootApplication`어노테이션을 통해 이루어지는데, 이는 `@Configuration` 클래스를 기반으로 한다.<br>
아래는 main메소드가 위치한 스프링 어플리케이션 실행 클래스의 `@SpringBootApplication`의 관계를 나타낸다.<br>
 `@Configuration` -> `@SpringBootConfiguration` -> `@SpringBootApplication`<br>


특정 클래스에 대한 자동설정을 해지하고 싶다면 아래와 같이 exclude 옵션을 통해 명시할 수 있으며, 또는 `spring.autoconfigure.exclue`프로퍼티를 통해 할 수 있다.
```Java
@SpringBootApplication(exclue = {DataSourceAutoConfiguration.class})
public class MyApplication {
    // ...
}
```






