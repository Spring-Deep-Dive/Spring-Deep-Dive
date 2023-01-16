<br>

# Servlet

`서블릿`은 자바를 사용하여 웹 어플리케이션을 개발하는데에 사용되는 기술이며, 이와 관련된 인터페이스와 클래스를 제공한다. 웹과 관련된 기술(request와 response를 처리 및 생성, JSP, Filter 등)을 사용하기 위해 서블릿의 인터페이스를 객체로 구현하여 기능을 사용한다.

>그래서 서블릿이 뭔데?

서블릿(클래스, 인터페이스)에서 명시하고 있는 규칙을 따라 구현되는 자바 웹 기술.


## Common Gateway Interface(CGI)
<img src="/assets/images/servlet/servlet_cgi_process.png">
서블릿 이전에 사용하던 기술로, 서버사이드 스크립팅 언어이다. 클라이언트로부터 요청을 받고 응답을 처리해주는 과정과 같은 상호작용에 대한 기능을 제공한다.

프로세스를 기반으로 동작하며, 요청을 보내는 클라이언트의 수가 증가하면 서버측에서 응답을 전송하는데에 지연이 발생하게 된다.
또한, C,C++,Perl과 같은 플랫폼에 종속적인 언어를 사용한다.


<br>

## Servlet의 개념과 특징
<img src="/assets/images/servlet/servlet_overview.png">

- 요청에 대해 쓰레드를 기반으로 작동한다
- 자바를 기반으로 하여 플랫폼 독립적이며, JVM을 통해 관리된다.
- 자바코드 내부에 HTML을 포함한다
- javax.servlet.http.HttpServlet 클래스를 상속받는다
