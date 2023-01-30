**Spring Boot Profiles**

특정한 프로파일에서만 특정 빈을 등록하고 싶을 때

애플리케이션의 동작을 특정 프로파일일 경우 빈 설정을 다르게 하고 동작을 다르게 하고 싶을 때

---

**@Profile 어노테이션**

config package를 생성하고 각 Configuration Class를 만든 다음 아래와 같이 지정

```
@Profile("test")
@Configuration
public class TestConfiguration {

    @Bean
    public String hello() {
        return "hello test";
    }
}
```

---

**spring.profiles.active**

properties에 직접 설정해서 사용

우선순위에 영향을 받음

```
spring.profiles.active=test
```

커맨드 라인에서 직접 설정시 우선순위가 더 높아서 변경됨

```
java -jar target/XXX.jar --spring.profiles.active=test
```

---

**application-{profile}.properties**

-   application.properties
-   applciation-test.properties
-   application-prod.properties

이렇게 만든 properties 파일은 기본적인 properties보다 우선순위가 더 높기 때문에 기존의 값을 덮어쓴다.

---

**include 를 통해 모듈화**

-   spring.profiles.include

프로퍼티에서 include로 원하는 프로퍼티들을 등록할 수 있다.

---



**@SpringBootApplication**

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
...
```

스프링 부트 어플리케이션은 2단계에 걸쳐 Bean을 등록한다.

1단계: @ComponentScan

2단계: @EnableAutoConfiguration

**@ComponentScan**

자기 자신부터 하위 패키지까지 훑어서 @Component 달린 클래스들을 Bean으로 등록한다.

**@EnableAutoConfiguration**

_Spring Boot 2.7 introduced a new META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports file for registering auto-configurations, while maintaining backwards compatibility with registration in spring.factories. With this release, support for registering auto-configurations in spring.factories has been removed in favor of the imports file._

Spring Boot 2.7부터 자동 설정 대상을

META-INF/spring.factories 대신

META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 에 명시한다.

```
...
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
...
```

이런 클래스들을 조건에 따라 Bean 등록시킨다.

예시로 WebMvcAutoConfiguration을 보자.

```
@AutoConfiguration(after = { DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
      ValidationAutoConfiguration.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@ImportRuntimeHints(WebResourcesRuntimeHints.class)
public class WebMvcAutoConfiguration {
```

@Conditional... 어노테이션들이 어떤 경우에 Bean 등록을 해줄지 정해주는 역할을 한다.
