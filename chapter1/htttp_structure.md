# HTTP의 구조(spec)

- HTTP request: 클라이언트가 서버로 전달해서 서버의 동작을 야기하는 메세지
- HTTP response: 요청에 대한 서버의 답변

<img src="/assets/images/http/http_message_structure.png" width="450" height="370">

HTTP message는 ASCII로 인코딩된 텍스트 정보이며, 응답과 요청은 사전에 약속된 형태로 구조를 띄고 있습니다.

> `시작줄(start-line)` : 실행되어야할 요청, 요청 수행에 대한 결과<br>
> `헤더`: 요청에 대한 설명 및 message body에 대한 설명<br>
> `빈줄`: 줄바꿈을 통해 요청에 대한 모든 메타 정보의 끝을 뜻함<br>
> `본문`: 시작줄과 헤더에서 명시된 내용이 옵션으로 들어감

<br>

## 시작줄(stat-line)

### Request

<img src="/assets/images/http/http_request_sl.png" width="350" height="270">


- HTTP 메소드
    - GET, PUT, POST, HEAD, OPTIONS등과 같은 메소드를 명시하여 서버에 수행을 요청할 동작을 나타냅니다. 
- 요청 타겟
    - URL, 프로토콜, 포트, 도메인 절대 경로 등으로 구성되며, HTTP 메소드를 수행할 자원에 대한 정보입니다.
- HTTP 버전
    - Response message에서 사용할 HTTP 버전을 명시합니다.

<br>

### Response
<img src="/assets/images/http/http_response_sl.png" width="350" height="270">

- HTTP 버전
    - GET, PUT, POST, HEAD, OPTIONS등과 같은 메소드를 명시하여 서버에 수행을 요청할 동작을 나타냅니다. 
- 상태 코드
    - 요청에 대한 수행 결과와 실패 시 그 이유를 나타내는 상태코드입니다.
- 상태 메시지
    - 상태코드에 대한 짧은 설명입니다.




<br>

## HTTP Header

<br>

헤더는 크게 4가지로 분류 됩니다.
- General Header(공통 헤더)
- Request Header(요청 헤더)
- Response Header(응답 헤더)
- Representation Header(표현 헤더)

<br>

### 1) General header
<br>

    요청과 응답 모두에 적용되지만, 바디에서 최종적으로 전송되는 데이터와는 관련이 없는 헤더

    - Date: 현재시간
    - Pragma: 캐시 제어(HTTP/1.0)
    - Cache-Control: 캐시 제어(HTTP/1.1)
    - Upgrade: 프로토콜 변경 시 사용
    - Via: 중계(프록시)서버의 이름, 버전, 호스트명
    - Connection: 네트워크 접속을 유지할지 말지 제어.(@HTTP/1.1, keep-alive 기본값)
    - Transfer-Encoding: 사용자에게 entity를 안전하게 전송하기 위해 사용하는 인코딩 방식을 지정

<br>

### 2) Request header
    
    HTTP 요청에서 사용되지만 메시지의 컨텐츠와 관련이 없는 패치될 리소스나 클라이언트 자체에 대한 자세한 정보를 포함하는 헤더
    - Host: 요청하려는 서버 호스트 이름과 포트번호
    - User-agent: 클라이언트 프로그램 정보
    - Referer: 현재 페이지로 연결되는 링크가 있던 이전 웹페이지 주소
    - Accept: 클라이언트가 처리 가능한 MIME Type 종류 나열
    - Accept-charset: 클라이언트가 지원가능한 문자열 인코딩 방식
    - Accept-language: 클라이언트가 지원 가능한 언어 나열
    - Accept-encoding: 클라이언트가 해석가능한 압축 방식 지정
    - If-Modified-Since: 지정된 시간 이후로 변경된 리소스 취득.캐시가 만료되었을 때만 데이터를 전송하는데 사용
    - Authorization: 인틍 토큰을 서버로 보낼때 쓰이는 헤더
    - Origin: 서버로 Post요청을 보낼때 요청이 어느 주소에서 시작되었는지 나타내는 값. 경로정보는 포함하지 않고 서버 이름만 포함
    - Cookie: key-value형태로 표현되는 쿠키 값. Set-Cookie 헤더와 함께 서버로부터 이전에 전송됐던 저장된 HTTP 쿠키를 포함

<br>

### 3) Response Header

    위치 또는 서버 자체에 대한 정보(이름, 버전)과 같이 응답에 대한 부가적인 정보를 포함하는 헤더

    - Server: 웹서버의 종류
    - Age: max-age 시간내에서 얼마나 흘렀는지 초 단위로 표기한 값
    - Referrer-policy: 서버 referrer 정책을 알려주는 값 ie. origin, no-referrer, unsage-url
    - WWW-Authenticate: 사용자 인증이 필요한 자원을 요구할 시 서버가 제공하는 인증 방식
    - Proxy-Authenticate: 요청한 서버가 프록시 서버인 경우 유저 인증을 위한 값
    - Set-Cookie: 서버측에서 클라이언트에게 세션 쿠키 정보를 설정

<br>

### 4) Representation Header

    HTTP 메세지의 본문에 대한 메타 정보를 담습니다.
    - Content-type: 리소스의 media type 명시
    - Content-Length: 바이트 단위를 가지는 개체 본문의 크기
    - Content-language: 본문을 이해하는데 가장 적절한 언어
    - Content-location: 반환된 데이터 개체의 실제 위치
    - Content-disposition: 응답 메시지를 브라우저가 어떻게 처리할지 명시 ie. 다운로드
    - Content-Security-Policy: 다른 위부 파일을 불러오는 경우, 차단할 리소스와 불러올 리소스 명시
    - Content-Encoding: 본문의 리소스 압축 방식
    - Location: 301/302 상태코드일때만 볼 수 있는 헤더로, 서버의 응답이 다른곳에 있다고 알려주며 해당 URI를 지정
    - Last-Modified: 리소스의 마지막 수정 날짜
    - Allow: 지원되는 HTTP 요청 메소드
    - Expires: 자원의 만료 일자
    - ETag: 리소스의 버전을 식별하는 고유한 문자열 검사기


<br>


### HTTP Body

#### HTTP Request

헤더에서 Entity Header가 존재한다면 request의 본문이 존재함을 의미합니다. GET, HEAD, DELETE, OPTIONS와 같은 리소스를 가져오는 요청엔 본문이 필요 없으며, POST와 같은 서버에 데이터를 전송하는 경우엔 본문을 포함합니다.

```
POST /test HTTP/1.1
Accept: application/json
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 83
Content-Type: application/json
Host: google.com
User-Agent: HTTPie/0.9.3

{
    "test_id": "tmp_12345567",
    "order_id": "8237362"
}
```
위의 예시는 HTTP Request 중 POST메소드를 이용해 HTML 폼 데이터를 전송하는 예시입니다.

<br>
<br>

#### HTTP Response

Request와 마찬가지로 모든 Response의 body가 존재하는것은 아닙니다.
데이터를 전송할 필요가 없는 경우엔 body가 비어있게 됩니다.

```
HTTP/1.1 200 OK
Date Sun, 98 Feb 2012 01:11:12 GMT
Server: Apache/1.3.29 (Win32)
Last-Modified: Sat, 07 Feb 2012
ETag: "0-23-4024c3a5"
Accept-Ranges: bytes
Content-Length: 35
Connection: close
Content-Type: text/html

<h1>My Home Page</h1>
```

---

<br>

