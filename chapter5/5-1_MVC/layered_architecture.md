# Spring MVC and Layered Architecture

어플리케이션의 MVC 설계에서 명확한 책임을 구분하기 위해 3 계층으로 구성된 아키텍처를 따른다.

MVC와 레이어드 아키텍처의 차이점은 다음과 같다.

<img src="/assets/images/MVC/mvc_pattern_overview.png">


MVC패턴은 오직 presentation layer에서의 로직에 집중한다. 모델은 비즈니스 레이어를 정의하며, 컨트롤러는 어플리케이션의 전반적인 흐름을 관리하며, 뷰는 프레젠테이션 레이어에 대해 정의한다.

<img src="/assets/images/MVC/layered_architecture_overview.png">


레이어드 아키텍처는 어플리케이션의 전체적인 설계를 다룬다.

- Presentation Layer: 사용자와 상호작용을 담당하며 어플리케이션의 기능을 제공하고 데이터를 유저에게 보여준다.

    - 사용자로부터 요청을 수신/검증하며, 모델 객체를 조작하며, 적절한 ModelAndView 객체를 리턴한다.

- Business Logic(Application/Domain) Layer: 실제 어플리케이션의 비즈니스 로직을 수행하며 이와 관련된 데이터를 처리한다.

    - 실제 비즈니스 로직(연산, 데이터 변환 및 처리, 비즈니스 룰 적용)을 수행하며, 컨틀로러로부터 호출받아 실행된다. 다른 service나 repository클래스의 메소드를 호출한다.

- Data Access(Persistence) Layer: 상위 계층에서 발생한 이벤트로 인한 데이터 업데이트를 데이터베이스에 적용하며, 데이터를 필요에 따라 가져오는 책임을 지닌다.

    - 데이터베이스를 통한 CURD동작을 수행한다.



