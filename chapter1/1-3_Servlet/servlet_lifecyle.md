# 서블릿 생명주기

클라이언트로부터 요청을 받으면 Servlet은 객체를 생성하고 초기화 작업을 거친 후, 요청을 처리하게 된다.

<img src="/assets/images/servlet/servlet_lifecycle_overview.png" width="450" height="500">

1. 요청이 오면 Servlet 클래스가 로딩되어 요청에 대한 Servlet 객체 생성
2. `init()`메소드를 통해 Servlet 객체 초기화
3. `service()`메소드를 호출하여 servlet이 클라이언트의 요청을 처리
4. service()메소드는 요청의 HTTP 메소드에 따라 doGet() doPost()를 호출
5. `destroy()`메소드를 호출하여 servlet 객체 제거

이 과정에서 리소스 낭비를 위해 톰캣은 다음과 같은 기능을 제공한다.

- Servlet 객체를 생성/초기화 이후 다음 요청을 위해 이미 생성된 Servlet객체를 메모리에 남겨둔다.
- 톰캣이 종료/reload 전에 모든 Servlet을 제거

따라서 특정 url에 대해 최초로 Servlet객체를 생성할 때에는 init()메소드를 호출하지만, 이후 같은 url에 대해 요청이 발생하면 service()메소드가 즉시 호출되게 된다.

