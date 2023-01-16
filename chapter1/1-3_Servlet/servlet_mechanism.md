# 클라이언트-서버 동작 과정

1. 서버측에서 TCP/IP연결을 위해 소켓을 오픈하고 대기
2. 요청을 수신하고 HTTP 요청 메시지 파싱<br>
    HTTP method 및 URL, Content-Type 확인<br>
    HTTP 메시지 바디 내용 파싱
3. 저장 프로세스 실행
4. 비즈니스 로직 실행
5. HTTP 응답 메시지 생성
    HTTP 메시지 시작라인 생성
    헤더 생성
    응답 메시지 바디 생성 및 입력
6. TCP/IP 응답 전달 후 소켓 종료

서블릿은 `4. 비즈니스 로직 실행` 외의 업무를 처리해주며, 개발자가 비즈니스 로직에만 집중할 수 있도록 해준다.

이를 기반으로 `서블릿`을 설명하자면 다음과 같이 할 수 있다.
- 클라이언트의 요청을 처리하고, 그 결과를 다시 클라이언트에게 전송하는 Servlet클래스의 구현 규칙을 따르는 자바 프로그램
- 클라이언트의 요청에 대해 특정 기능을 수행(HTML 문서 생성)을 통해 응답하는 인터넷 서버 프로그램

참조: 'Servlet이란? 서블릿이란?' https://jusungpark.tistory.com/15

<img src="/assets/images/servlet/servlet_process_overview.png">


# 서블릿 컨테이너
`서블릿 컨테이너` 또는 `웹 컨테이너`는 서블릿의 생명주기를 관리하고 요청에 따른 스레드를 생성하며 클라이언트의 요청을 받고 응답을 보낼 수 있도록 웹 서버와 소켓을 만들어서 통신하도록 해준다.<br>
이러한 서블릿 컨테이너는 `톰캣`이라는 웹 서버를 통해 지원받는데, 톰캣은 웹 서버와 연동하여 실행할 수 있는 자바 환경을 제공하며 자바 서버 페이지(JSP)와 자바 서블릿이 실행될 수 있는 환경을 제공한다.<br>

## 서블릿 컨테이너의 역활
- 통신 지원
    소켓을 만들고 특정 포트를 리스닝하며 연결 요청이 들어오면 스트림을 생성하여 요청을 최종적으로 수신한다. 서블릿 컨테이너는 이러한 통신 과정을 API로 제공한다.

- 생명주기 관리
    서블릿 컨테이너는 서블릿 클래스를 로딩하여 인스턴스화하고, 초기화 메소드를 호출한다. 요청이 들어오면 적절한 서블릿 메소드를 찾아서 호출한다. 모든 사이클이 끝나면 GC에 의해서 인스턴스화된 객체를 소멸시킨다.

- 멀티스레딩
    서블릿 컨테이너는 서블릿 요청에 대해 스레드를 기반으로 작업을 수행한다.

- 선언적인 보안 관리
    서블릿 컨테이너 차원에서 보안 기능을 지원하여 서블릿 코드 내부에서 보안 관련 메소드를 구현하지 않아도 된다.

- JSP 지원


# 서블릿 동작 과정

<img src="/assets/images/servlet/servlet_process.png">

1. 클라이언트가 HTTP 요청을 서블릿 컨테이너로 전송
2. 요청을 받은 서블릿 컨테이너는 HttpServletRequest, HttpServletResponse 객체 생성
3. web.xml을 기반으로 요청 URL이 어느 서블릿에 대한 요청인지 조회
4. 해당 서블릿에서 service메소드를 호출하여 클라이언트의 요청이 GET/POST여부에 따라 doGet(), doPost()를 호출
5. doGet() 또는 doPost()는 동적 페이지를 생성한 후 HttpServletResponse객체에 응답을 전송
6. 응답이 끝나면 HttpServletRequest, HttpServletResponse 객체를 소멸


## 서블릿 호출에 대한 서버의 동작 플로우

최초로 서블릿 요청이 발생한 경우
- 서블릿 클래스 로드
- 서블릿 클래스 인스턴스화
- ServletConfig 객체를 전달하여 init() 호출

이전에 받은 요청이라면
- request & response 객체를 전달하고 service() 호출


## 컨테이너가 서블릿 요청을 처리하는 과정
- web.xml파일을 통해 요청에 대한 servlet을 조회
- 요청에 대한 request & response 객체 생성
- 쓰레드를 통해 service() 호출
- public service()가 내부적으로 protected service()호출
- protected service()가 doGet()과 같은 요청 타입에 따른 메소드 호출
- doGet()과 doPost()같은 메소드가 response 객체를 생성하여 클라이언트로 전송



## public service()의 내부
`public service()`는 ServletRequest 객체를 `HttpServletRequest` & `HttpServletResponse`타입으로 변환시킨다. 변환된 객체를 `service()`를 통해 전달한다.

```JAVA
public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    HttpServletRequest request;
    HttpServletResponse response;
    try {
        request = (HttpServletRequest) req;
        response = (HttpServletResponse) res;
    } catch (ClassCastException e) {
        throw new ServletException("non-HTTP request or response");
    }
    service(request, response);
}
```

## protected service()의 내부
`protected service()`는 요청의 타입을 확인하여 해당 do메소드를 호출한다.
```JAVA
protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String method = req.getMethod();
    if(method.equals("GET")) {
        long lastModified = getLastModified(req);
        if(lastModified == -1L) {
            doGet(req, res);
        } else {
            long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                if (ifModifiedSince < lastModified) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                } else {
                    resp.setStatus(304);
                }
        }
    }
}
```

*last-modified & if-modified-since in service()<br>
service()내부에서 doGet()과 같은 메소드를 호출하지만, 매 요청마다 해당 메소드를 실행시키는것은 아니다.<br>
웹 서버는 문서를 전송하면서 last-modified 헤더를 함께 전송하는데, 문서에 대한 변경여부를 판별하도록 한다.<br>
이전에 발생했던 GET요청에 대해 데이터 변경이 없었다면 doGet()를 실행시키지 않는다.


참조: 'Servlet이란? 서블릿의 특징' https://healthdevelop.tistory.com/entry/tomcat2<br>
참조: 서블릿이란?(Servlet, Servlet Container, JSP) https://u0hun.tistory.com/11<br>
참조: Last Modified Times https://www.oreilly.com/library/view/java-servlet-programming/156592391X/ch03s06.html<br>
