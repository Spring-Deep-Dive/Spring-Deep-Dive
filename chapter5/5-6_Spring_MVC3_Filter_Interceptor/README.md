# Filter와 Interceptor

---

## 필터와 인터셉터

### 공통점

- 개발자가 직접 작성한 컨트롤러에 요청이 전달되기 전에 전,후처리가 가능하다.

### 차이점

- 실행 시점
    - 필터
        - 필터는 J2EE 표준 스펙으로 Dispatcher Servlet에게 요청을 전달하기 전에 URL 패턴에 맞는 요청에 대한 부가작업 처리 기능 제공
        - Request , Response 객체 조작가능
    - 인터셉터
        - Spring이 제공하는 기술로 Dispatcher Servlet이 요청을 받은 후 실행.
        - 즉, 개발자가 직접 작성한 컨트롤러 전,후 처리
        - Request, Response 객체 조작이 불가능

---

## Request/Response 조작 가능 여부

### 필터

- 필터는 다음 필터에 대해 체이닝하여 호출을 하는데 이 때, 다음 필터에 값을 넘기기 전에 객체를 직접 바꿔줄 수 있다.

```java
public MyFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // 개발자가 다른 request와 response를 넣어줄 수 있음
        chain.doFilter(request, response);       
    }
    
}
```

### 인터셉터

- 인터셉터는 디스패쳐 서블릿이 인터셉터 목록을 가지고 있고, 반복문을 통해 호출하여 호출을 통해 false가 리턴되면 호출을 중단한다.

```java
public class MyInterceptor implements HandlerInterceptor {

    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Request/Response를 교체할 수 없고 boolean 값만 반환할 수 있다.
        return true;
    }

}
```

---

## 용도

필터는 스프링과 무관하게 어플리케이션에서 처리되어야 하는 작업들에대한 전,후처리가 가능하고 웹 어플리케이션에 전반적으로 사용되어야 하는 기능에 대한 구현과 스프링 컨텍스트 전에 요청이 처리 되기 때문에 공통 보안 작업에 많이 사용된다고 한다.

인터셉터는 클라이언트의 요청과 관련되어 전역으로 사용되는 작업을 처리한다.

즉, 요청이 스프링 컨텍스트 내부에서 처리되는 전역작업에 대한 전,후처리가 용이하다.

그리고, 개발자가 직접 작성한 컨트롤러부에 공통적으로 데이터를 가공하는 등의 이점을 가질 수 있다.