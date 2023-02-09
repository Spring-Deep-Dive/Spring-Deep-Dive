# Dispatcher Servlet

---

# Dispatcher Servlet이란?

## 정의

- HTTP 프로토콜의 요청을 가장 먼저 받아 적합한 컨트롤러에 위임하는 Front Controller

## 장점과 단점

### 장점

- Dispatcher Servlet가 등장하면서 web.xml에 대한 종속성이 줄어들었다.
- 모든 요청을 처리하기 때문에 공통적인 작업 처리에 유리하다.
- 개발자는 컨트롤러를 구현하기만 하면 되기 때문에 개발적으로 상당한 이점이 있다.

### 단점

- 모든 요청을 가로채기 때문에 정적인 파일을 불러오지 못하는 문제가 있었다.

### 해결방안

- 정적인 자원에 대한 요청과 애플리케이션에 대한 요청 분리
    - URL에 특정 path가 중복되면서 정적인 자원에 대한 요청과 애플리케이션에 대한 요청에 대한 구분이 필요하고 유연한 설계가 어렵다.
- 우선순위 부여
    - Dispatcher Servlet은 요청을 받으면 애플리케이션의 해당하는 요청인지 먼저 확인한 후, 만약 해당 요청에 대한 컨트롤러가 없다면 정적인 자원을 2순위로 찾는다. 만약 이도 없다면 `NoHandlerFoundException` 이 발생한다.

---

# 동작 과정

![이미지 001.png](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/a7f2a183-2f05-4154-8214-27616170ace6/%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5_001.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20230209%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20230209T132318Z&X-Amz-Expires=86400&X-Amz-Signature=067ba1112b64dfbc7516c89586dab23a9495d8a26e1434fe64a2b28eb58ddb4c&X-Amz-SignedHeaders=host&response-content-disposition=filename%3D%22%25E1%2584%258B%25E1%2585%25B5%25E1%2584%2586%25E1%2585%25B5%25E1%2584%258C%25E1%2585%25B5%2520001.png%22&x-id=GetObject)

## 1.클라이언트의 요청을 받는 Dispatcher Servlet

- Front Controller인 Dispatcher Servlet은 Web Context에 있는 필터들을 지나 Spring Context에서 처음으로 요청을 전달받는다.
- HttpServlet을 상속받아 사용하고 서블릿으로 동작.
- HttpServlet의 service를 재정의하여 DispatcherServlet.dpDispatch()가 호출된다.

## 2.전달 받은 요청 정보를 기반으로 위임할 컨트롤러 탐색

- dpDispatch에서 요청 정보와 일치하는 핸들러를 찾는다.
- 핸들러에 연길된 어댑터를 조회.

## 3.조회된 핸들러에게 요청을 위임.

- 2에서 언급한 핸들러 어댑터를 찾았다면 요청 정보에 해당하는 핸들러 어댑터(개발자가 구현한 Controller부)에게 요청을 위임하고 개발자가 작성한 비즈니스 로직이 실행

## 4.핸들러 어댑터의 실행 값 반환

- 반환 값에 따라 viewResolver를 호출하여 Model을 반환하는 HTML을 리턴하거나, [Message Converter](https://joont92.github.io/spring/MessageConverter/)를 통해 반환 값을 직렬화하여 클라이언트에게 반환한다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/d20ddbf7-f2ed-43c0-9fba-64e6ff41e032/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20230209%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20230209T132346Z&X-Amz-Expires=86400&X-Amz-Signature=2ab72c74420c706df5623eb01bbf8f209c0a449462f709e38f893ee48746a98d&X-Amz-SignedHeaders=host&response-content-disposition=filename%3D%22Untitled.png%22&x-id=GetObject)

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/697e029c-2c7b-4fc8-b687-b68ff2e663eb/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20230209%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20230209T132358Z&X-Amz-Expires=86400&X-Amz-Signature=d58d3468dc6171c871df25c19c5c7c809a60ea12e1f1ea7b79de2a69270bfbd5&X-Amz-SignedHeaders=host&response-content-disposition=filename%3D%22Untitled.png%22&x-id=GetObject)

---

# 참고 자료:

[[Spring] SpringBoot 소스 코드 분석하기, DispatcherServlet(디스패처 서블릿) 동작 과정 - (7)](https://mangkyu.tistory.com/216)

[[spring] MessageConverter](https://joont92.github.io/spring/MessageConverter/)

[[Spring] Dispatcher-Servlet(디스패처 서블릿)이란? 디스패처 서블릿의 개념과 동작 과정](https://mangkyu.tistory.com/18)