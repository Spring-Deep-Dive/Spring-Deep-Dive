# 서블릿 API 계층 구조와 기능

<img src="/assets/images/servlet/servlet_class_interface.png" width="450" height="500">

서블릿 API는 추상화 클래스와 인터페이스를 기반으로 구현된다.

## Servlet 인터페이스: 라이프사이클 관련 메소드 제공
모든 서블릿이 구현해야 할 메소드를 정의.
서블릿 패키지의 중심으로, 서버가 서블릿을 관리할 수 있도록 생명주기 관련 메소드로 구성.
- init()
- service()
- destroy()
- getServletConfig()
- getServletInfo()

## ServletConfig 인터페이스: 서블릿 초기화 관련 메소드 제공
서블릿 컨테이너가 서블릿을 초기화할 때 필요한 정보를 전달해주기 위한 인터페이스.
web.xml을 참고하여 서블릿 초기화를 위한 정보를 저장

## GenericServlet 추상클래스: 프로토콜 별 대응
상위 두 인터페이스를 구현하여 일반적인 서블릿 기능을 구현.


## HttpServlet 추상클래스: HTTP 프로토콜 대응
GenericServlet을 상속받아 HTTP 프로토콜에 대한 기능을 수행하는 클래스.
service()를 호출하여 요청 메소드에 따른 do()메소드 호출.

참조: https://velog.io/@7lo9ve3/ㅇ