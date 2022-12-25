## HTTP의 구조(spec)

- HTTP request: 클라이언트가 서버로 전달해서 서버의 동작을 야기하는 메세지
- HTTP response: 요청에 대한 서버의 답변

HTTP message는 ASCII로 인코딩된 텍스트 정보이며, 응답과 요청은 사전에 약속된 형태로 구조를 띄고 있습니다.

> 시작 줄(start-line) : 실행되어야할 요청, 요청 수행에 대한 결과
> 헤더: 요청에 대한 설명 및 message body에 대한 설명
> 빈 줄: 줄바꿈을 통해 요청에 대한 모든 메타 정보의 끝을 뜻함
> 본문: 시작줄과 헤더에서 명시된 내용이 옵션으로 들어감

시작 줄과 헤더를 묶어서 request header라고 하며, message body는 payload 또는 본문(body)이라고 합니다.

### HTTP Request

#### 시작줄(stat-line)
- HTTP 메소드
    - GET, PUT, POST, HEAD, OPTIONS등과 같은 메소드를 명시하여 서버에 수행을 요청할 동작을 나타냅니다. 
- Request target
    - URL, 프로토콜, 포트, 도메인 절대 경로 등으로 구성되며, HTTP 메소드를 수행할 자원에 대한 정보입니다.
- HTTP 버전
    - Response message에서 사용할 HTTP 버전을 명시합니다.

#### HTTP 헤더




### HTTP Response
