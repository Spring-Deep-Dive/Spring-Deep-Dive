# MVC Design Pattern

MVC는 어플리케이션을 세가지의 역할과 책임으로 구분한 소프트웨어 설계 패턴을 말한다.

- `Model`: 어플리케이션에서 사용되는 데이터를 말하며, 해당 객체의 상태를 관리하는 메소드가 포함될 수 있다.

- `View`: Model의 데이터를 유저에게 보여주는 역할을 맡는다. 

- `Controller`: Model과 View 사이에서 유저의 행동에 따른 이벤트를 핸들링하고, 그에 따른 Model 데이터의 변경과 View 업데이트를 수행한다.

# MVC in Spring

<img src="/assets/images/MVC/mvc_work_flow.png">

Spring에서 MVC 아키텍처는 `프론트 컨트롤러(Front Controller)` 패턴과 함께 사용된다. 중앙집중형 컨트롤러(`DispatcherServlet`)를 프레젠테이션 계층의 가장 앞 단에서 모든 요청을 처리하는데, 클라이언트의 요청을 받아서 적절한 컨트롤러로 작업을 위임하고 예외를 처리하며 응답으로 전송할 뷰를 선택하고 생성한다. 

1. DispatcherServlet의 HTTP 요청 수신
    서블릿 컨테이너는 수신한 요청 정보를 컨테이너의 DispatcherServlet에게 전달한다. 이 때, DispatcherServlet이 전달받을 URL 정보는 web.xml에 정의된다. 

2. DispatcherServlet에서 컨트롤러로 HTTP 요청 위임
    `핸들러 매핑 전략`에 따라서, DispatcherServlet은 URL, 파라미터 정보, HTTP 메소드 등을 참고하여 기반으로 특정 컨트롤러에게 작업을 위임하게 된다. 이 때, DispatcherServlet은 서블릿 컨테이너에 등록되어있는 서블릿을 이용하여 DI가 적용되는 것처럼 ServletApplicationContext의 빈을 가져와서 사용한다.

    > 스프링에서 컨트롤러를 핸들러라고 부르며, 웹의 요청을 다루는 객체라는 의미로 사용된다. 

    어떤 컨트롤러(핸들러)가 요청을 처리할지 정해졌다면, 해당 컨트롤러 객체의 메소드를 호출하여 실제 웹 요청 처리를 위한 위임이 일어난다 DispatcherServlet은 어떠한 종류의 객체도 컨트롤러로 사용할 수 있다. 그렇다면, 제각기 다른 메소드와 포맷을 지닌 컨트롤러 객체를 어떻게 요청 처리를 위해 사용할 수 있나? 이를 위해 `어댑터 패턴`을 사용하여 특정 컨트롤러의 기능을 호출한다. 따라서 DispatcherServlet은 핸들러 어댑터에 웹 요청(HttpServletRequest/Response)을 전달하며, 어댑터는 컨트롤러의 메소드 파라미터 형식에 맞춰 전달하고 처리결과를 받는다.

3. 컨트롤러의 모델 생성과 정보 등록
    컨트롤러(핸들러)는 전달받은 요청을 해석하고, 비즈니스 로직을 수행하여 서비스 계층의 객체에게 작업을 위임한다. 작업의 결과를 받아서 모델을 생성하여 DispatcherServlet에게 모델을 전달한다.

4. 컨트롤러의 결과 리턴: 모델과 뷰
    컨틀로러는 뷰의 논리적인 이름을 DispatcherServlet에게 전달하면, 이를 뷰 리졸버를 통해 뷰 오브젝트를 생성한다. 뷰 오브젝트를 리턴하는 방법은 컨트롤러가 직접 리턴하거나 사용되는 뷰 템플릿에 따라 달라지지만, 핵심은 컨트롤러가 뷰에 대한 정보를 전달한다는 것이다.

    결과적으로 컨트롤러는 모델과 뷰에 대한 정보를 어댑터를 통해 전달하며, 핸들러는 `ModelAndView` 객체를 통해 이 과정을 수행한다. 이후 컨트롤러의 책임이 끝나며 작업 흐름은 DispatcherServlet에게 위임된다.

5. DispatcherServlet의 뷰 호출 & 모델 참조
    DispatcherServlet은 뷰 오브젝트에게 모델을 전달하고 클라이언트에게 전송할 최종 결과물 생성을 요청한다. JSP라면, 뷰 템플릿의 이름을 통해 HTML을 생성하고 동적으로 모델의 데이터를 채운다.

    ```HTML
    <div> name: ${name} </div>

    <div> name: John Doe </div>
    ```

    동일한 모델이지만 어떤 뷰 템플릿을 사용하느냐에 따라 최종 결과물은 다르게 된다. 이렇게 생성된 결과물은 HttpServletResponse 객체에 담기게 된다.

6. HTTP 응답 리턴
    DispatcherServlet은 뷰를 통해 전달받은 HttpServletResponse를 서블릿 컨테이너에게 전달한다. 서블릿 컨테이너는 이를 HTTP 응답으로 만들어 클라이언트에게 전송한다.



참조: 이일민 저. 토비의 스프링3.1, <i>DispatcherServlet과 MVC 아키텍처</i>