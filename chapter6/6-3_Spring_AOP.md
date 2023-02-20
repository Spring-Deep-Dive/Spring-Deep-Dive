# Spring AOP

AOP는 프로그래밍 패러다임으로, 기존의 코드에 대한 수정 없이 추가적인 동작을 수행할 수 있도록 한다. 

<br>

---

<br>



## 예제코드

```Java
@Aspect // Aspect: PointCut + Advice 조합의 AOP 기본 모듈
@Component
@Slf4j
public class TimeTraceAop {

    @Around("execution(* springdeepdive.demoproject..*(..))") // PointCut: where to apply
    public Object execute(ProceedingJoinPoint jointPoint) throws Throwable {
        // Advice: 부가기능
        long start = System.currentTimeMillis();
        log.debug("START: " + jointPoint.toShortString());
        System.out.println("START: " + jointPoint.toShortString());

        try {
            return jointPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;

            log.debug("END: " + jointPoint.toShortString() + " " + timeMs + "ms");
            System.out.println("END: " + jointPoint.toShortString() + " " + timeMs + "ms");
        }
    }
}
```

<br>

---

<br>

## Glossary

- Target<br>
> (Where) 어떤 대상에게 기능을 부여할것인가?

부가기능을 부여할 대상.<br>
실질적인 비즈니스 로직을 구현하고 있는 코드.<br>
핵심기능을 담은 클래스 또는 부가기능을 제공하는 객체.<br><br>

- Advice<br>
> (What) 어떤 부가기능을 부여할 것인가?

타깃에게 제공할 부가기능을 담은 모듈.<br>
객체로 정의하기도 하며, 메소드 레벨에서 정의될 수 있다.<br>
MethodInterceptor처럼 메소드 호출 과정에 참여하기도 하며,<br>
예외 발생시에만 동작하는 어드바이스처럼 메소드 호출 과정의 일부에서만 동작하기도한다.<br><br>

- Join Point<br>
> (When) 언제 부가기능을 부여할 것인가?

Advice의 부가기능을 수행할 '시점'<br>
프록시 AOP에서 조인 포인트는 메소드의 실행단계를 말한다.<br>
타깃 객체가 구현한 인터페이스의 모든 메소드가 조인 포인트가 된다.<br><br>

- Point Cut<br>
> (Which) advice가 어떤 Join Point에 사용될 것인가?

Advice를 적용할 조인 포인트를 선별하는 작업 또는 그 기능을 정의한 모듈.<br>
AOP의 조인 포인트는 메소드의 실행을 말함으로, 포인트컷은 메소드를 선정하는 기능을 뜻한다.<br>
따라서 포인트컷 표현식은 execution으로 시작하며, 메소드의 시그니처를 비교하는 방법을 주로 사용한다. 메소드의 선정은 결국 해당 메소드의 클래스부터 시작됨으로, 클래스 선정 -> 메소드 선정 순으로 일어난다.<br><br>

`execution 명시자` <br>
advice를 적용할 메소드를 명시할 때 사용한다.<br>

execution(* com.edu.aop.BoardService.*(..))<br>
com.edu.aop.BoardService 인터페이스에 속한 파마리터가 0개 이상인 모든 메서드<br><br>

`within 명시자`<br>
특정 타입에 속하는 메소드를 JoinPoint로 설정되도록 명시할 때 사용.<br>

within(com.edu.aop.*)<br>
com.edu.aop 패키지의 모든 메소드<br><br>

`bean 명시자` <br>
스프링 버전 2.5이후로 지원하며, 스프링 빈을 이용하여 JoinPoint를 설정<br>

bean(someBean)<br>
이름이 someBean인 빈의 모든 메소드<br><br>

- Adviser<br>
포인트컷과 어드바이스를 하나씩 지닌 객체이다. 어드바이저는 어떤 부가기능을 어디에 전달할지 알고 있는 AOP의 기본 모듈이다. <br><br>

- Aspect<br>
한개 또는 그 이상의 포인트컷+어드바이스 조합으로 만들어진 AOP의 기본 모듈.<br>
싱글톤 형태로 존재하며, Adviser는 아주 단순한 형태의 Aspect라고 할 수 있다.<br><br>

- Weaving (n.엮기)<br>

> the process of linking aspect with other application types or objects to create an advised object.

Aspect를 대상 객체에 연결시켜 Aspect-Oriented 객체로 만드는 과정을 뜻함.<br>
즉, 비즈니스 로직에 Advice를 삽입시키는 것을 말함.<br><br>

- Proxy<br>
클라이언트와 타깃 사이에서 부가기능을 제공하는 객체를 말한다. DI를 통해서 타깃 대신 클라이언트에게 주입되며, 클라이언트의 메소드 호출을 대신 받아서 타깃에 위임해주면서, 그 과정에서 부가기능을 부여하게 된다. 스프링은 전적으로 프록시를 통해 AOP기능을 제공한다.<br><br>



<br>

---

<br>

## AOP vs AspectJ

Spring AOP는 IoC를 통해 AOP 구현을 목표로 하며, 프록시 기반 AOP 프레임워크를 말한다. 대상 객체에 Aspect를 적용하기 위해 대상 객체의 프록시를 생성하며, 스프링 컨테이너에 의해 관리되는 빈에 대해 적용이 가능하다. 

AspectJ는 완전한 AOP를 제공하기 위한 기술로, Aspect Weaving을 위해 AspectJ Compiler(ajc)라는 컴파일러를 이용한다. 클래스들이 Aspect와 함께 바로 컴파일되기에 Spring AOP에 비교했을때, AspectJ는 런타임시에는 아무것도 하지 않는다. 또한, 모든 객체에 대해 적용이 가능하다.

<img src="/assets/images/AOP/aop_aspectj.png">



참조: https://logical-code.tistory.com/118 'Spring AOP와 AspectJ 비교하기'

<br>

---

<br>

## Weaving

- Post-Compile / Runtime Weaving (RTW)<br>
스프링 AOP의 기본 설정.<br>
Proxy 사용 방법으로 Runtime시에 핵심코드와 Advice를 호출.<br><br>



- Compile-Time Weaving (CTW)<br>
AspectJ모듈에서 지원되는 방식.<br>
핵심코드와 Advice를 compile시에 병합(merge).<br><br>


- Load-Time Weaving (LTW)<br>
AspectJ모듈에서 지원되는 방식.<br>
프로세스가 시작될 때 핵심코드와 Advice를 조합.<br><br>
