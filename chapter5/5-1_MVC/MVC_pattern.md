# MVC Design Pattern

MVC는 어플리케이션을 세가지의 역할과 책임으로 구분한 소프트웨어 설계 패턴을 말한다.

- `Model`: 어플리케이션에서 사용되는 데이터를 말하며, 해당 객체의 상태를 관리하는 메소드가 포함될 수 있다.

- `View`: Model의 데이터를 유저에게 보여주는 역할을 맡는다. 

- `Controller`: Model과 View 사이에서 유저의 행동에 따른 이벤트를 핸들링하고, 그에 따른 Model 데이터의 변경과 View 업데이트를 수행한다.

# MVC in Spring

<img src="/assets/images/MVC/mvc_work_flow.png">

Spring에서 MVC 아키텍처는 `프론트 컨트롤러(Front Controller)` 패턴과 함께 사용된다. 중앙집중형 컨트롤러(`DispatcherServlet`)를 프레젠테이션 계층의 가장 앞 단에서 모든 요청을 처리하는데, 클라이언트의 요청을 받아서 적절한 컨트롤러로 작업을 위임하고 예외를 처리하며 응답으로 전송할 뷰를 선택하고 생성한다. 

1. DispatcherServlet의 HTTP 요청 수신


2. DispatcherServlet에서 컨트롤러로 HTTP 요청 위임


3. 컨트롤러의 모델 생성과 정보 등록


4. 컨트롤러의 결과 리턴: 모델과 뷰


5. DispatcherServlet의 뷰 호출


6. 모델 참조


7. HTTP 응답 리턴





참조: 이일민 저. 토비의 스프링3.1