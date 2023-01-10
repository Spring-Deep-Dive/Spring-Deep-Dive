# HTTP

`HTTP`는 HTML 문서와 같은 자원을 가져오기 위해 사용되는 통신규약이다. 웹 브라우저(클라이언트)와 서버 사이의 HTTP 통신을 통해 사용자는 웹 문서에 접근/이용할 수 있다. 서버 간에 데이터를 주고 받을 때도 대부분 HTTP를 사용한다.

클라이언트와 서버는 개별적인 메세지(데이터 스트림)를 주고받으며 통신한다. 웹 브라우저(클라이언트)가 보내는 메세지를 `요청(request)`라고 하며, 이에 반응하여 서버가 전송하는 메세지를 `응답(response)`라고 한다.

HTTP는 확장가능한 프로토콜이다. 이 확장가능성(extensibility)을 통해서 응용 계층 프로토콜(TCP 또는 TLS-encryped TCP연결)과 같은 다른 전송 프로토콜 또한 이용이 가능하다. 또한, 하이퍼텍스트 문서 뿐 만 아니라 이미지와 비디오 같은 컨텐츠도 서버와의 통신에서 송수신이 가능하다.

> `Tranport Layer Security` : Secure Socket Layer(SSL)으로도 알려진 이 프로토콜은 이메일, 웹 브라우징, 메세징 등 다른 프로토콜에서의 데이터 도청 및 변경으로 부터 보안을 약속한다.

> `Transmission Control Protocol` : TCP는 전송 제어 프로토콜로써, 두 host가 데이터 전송함에 있어 무결성을 보장한다.


# HTTP Request & Response

## HTTP Request(요청)

```
GET /search?q=hello&hl=ko HTTP/1.1
User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)
Host: www.google.com
Accept-Language: en-us
```

`HTTP request`는 클라이언트에서 서버로 전송되는 메세지로, 어떠한 action을 요청하는지 명시한다.
첫줄에는 HTTP method, 요청 타깃, HTTP 버전으로 구성된다.

- HTTP method - GET,PUT과 같은 서버를 통해 수행할 동작이 명시된다.
- Request target - HTTP method에 따라 URI 또는 '?'으로 시작하는 QueryString이 명시된다.
- HTTP version - HTTP 버전에 따라 나머지 부분의 구조가 달라진다.


## HTTP Response

```
HTTP/1.1 200 OK
Content-Type: text/html;charset=UTF-8
Content-Length: 3423

<html>
    <body>...</body>
</html>
```

`HTTP Response`메세지는 첫줄에 프로토콜의 버전, 상태코드, 상태 메세지로 구성된다. Header는 Request와 동일한 구조를 지니며, Body는 상태코드에 따라 달라진다.

<br>

# HTTP Method, Status

## HTTP Method

`HTTP Method`는 클라이언트가 웹 서버에게 사용자 요청의 목적이나 종류를 알리는 수단을 말한다.
최초의 HTTP 0.9는 GET메소드만 존재하였지만 버전이 증가됨에 따라 다양한 메소드들이 추가되었다.

| Method  |                       Description                        |
| :-----: | :------------------------------------------------------: |
|   GET   |                       리소스 조회                        |
|  POST   |      요청데이터 처리, 주로 데이터를 등록할 때 사용       |
|   PUT   |       리소스를 변경하며 해당 리소스가 없다면 생성        |
|  PATCH  |              존재하는 리소스의 일부만 변경               |
| DELETE  |                       리소스 삭제                        |
|  HEAD   | GET과 동일하나 메세지 부분을 제외한 상태줄, 헤더만 반환  |
| CONNECT |      대상 자원으로 식별되는 서버에 대한 터널을 설정      |
|  TRACE  | 대상 리소스에 대한 경로를 따라 메시지 루프백 테스트 수행 |
| OPTIONS | 대상 리소스에 대한 통신 가능 옵션을 설명(CORS에서 사용)  |


- `안전(Safe)` : 반복해서 메소드를 호출하더라도 리소스가 변경되지 않음
  - GET메소드는 데이터를 변경시키지 않는 메소드임으로 안전함
- `멱등(Indempotnet)`: 메소드를 계속 호출해도 결과가 동일함 ➡️ f(f(x)) = f(x)
  - GET: 한 번 조회하든, 여러번 조회하든 같은 결과가 조회됨
  - PUT: 결과를 대체함. 따라서 같은 요청을 반복해도 최종결과는 같음
  - DELETE: 결과를 삭제함. 같은 요청을 반복해도 삭제된 결과는 같음
  - POST: 멱등이 아님. 반복 호출 시 중복해서 비즈니스 로직이 수행될 수 있음.
- `캐시가능(Cacheable)`: 캐싱을 통해 데이터를 효율적으로 가져올 수 있다.
  - GET,HEAD,POST,PATCH가 캐시가능하나 GET/HEAD가 주로 캐싱에 쓰인다.

> Reference
> https://kyun2da.dev/CS/http-메소드와-상태코드/



## HTTP status

`HTTP 상태코드`는 클라이언트가 보낸 요청의 처리 상태를 응답에서 알려주는 기능이다.

| Status Code |     Name      |                          Description                      |
| :---------: | :-----------: | :-------------------------------------------------------: |
|     1xx     | Informational |                         요청이 수신되어 처리중                  |
|     2xx     |  Successful   |                             요청 정상 처리                    |
|     3xx     |  Redirection  |                   요청을 완료하려면 추가 행동이 필요               |
|     4xx     | Client Error  | 클라이언트 오류 - 잘못된 문법 등으로 인해 서버가 요청을 수행할 수 없음     |
|     5xx     | Server Error  |              서버 오류 - 서버가 정상 요청을 처리하지 못함            |

> reference :
>
> > https://mangchhe.github.io/web/2021/02/19/HttpActionProcess/
> > 인프런 - 모든 개발자를 위한 HTTP 웹 기본 지식(김영한)

