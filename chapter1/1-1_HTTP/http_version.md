# HTTP version

## HTTP 0.9 - 1991
HTTP 초기버전
```HTML
GET /mypage.html
```
- 요청은 단일 라인으로 구성되며, GET 메소드만 존재
```HTML
<HTML>
A very simple HTML page
</HTML>
```
- 응답도 단순히 파일 내용만으로 구성
- HTTP 헤더가 없고 HTML파일 전송만 가능

<br>

---

<br>

## HTTP 1.0 - 1996
```HTML
GET /mypage.html HTTP/1.0
User-Agent: NCSA_Mosaic/2.0 (Windows 3.1)
```
- HTTP 헤더가 추가되어 메타데이터를 포함할 수 있으며, 프로토콜을 유연하고 확장 가능하게 됨
- 버전 정보와 요청 method가 함께 전송
```
200 OK
Date: Tue, 15 Nov 1994 08:12:31 GMT
Server: CERN/3.0 libwww/2.17
Content-Type: text/html
<HTML>
A page with an image
  <IMG SRC="/myimage.gif">
</HTML>
```
- 상태 코드 라인이 응답 시작라인에 추가되어 요청의 성공여부 확인 가능
- GET 요청에 대한 응답 캐시 가능
- `Content-Type`으로 요청/전송의 데이터 포맷 지정 가능
- 커넥션 당 한개의 처리만 가능<br>
    요청 1개에 응답 1개만 허용


<br>

---

<br>

## HTTP/1.1 - 1997

```HTML
GET /en-US/docs/Glossary/Simple_header HTTP/1.1
Host: developer.mozilla.org
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:50.0) Gecko/20100101 Firefox/50.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate, br
Referer: https://developer.mozilla.org/en-US/docs/Glossary/Simple_header
```

- Persistent Connection 추가<br>
    지정된 timeout동안 커넥션을 닫지 않음으로 커넥션의 사용성 증가

- Pipelining 추가<br>
    이전 응답을 기다리지 않고 순차적으로 요청을 연속적으로 전송하고 순서에 따라 응답을 받는 방식<br>
    커넥션 당 한개의 처리만 가능하던 한계를 극복<br>
    여러 요청이 전송될 뿐, 해당 요청들을 한번에 처리해 응답을 전송해주는 것은 아니다.(multiplexing)

- Keey-Alive<br>
    TCP연결을 keep-alive를 통해 connection을 유지시켜 재사용한다.<br>
    요청-응답으로 이루어진 HTTP연결 사이클이 한번 끝난 이후, 다음 HTTP 통신이 가능한 식.<br>
    \[요청-응답\]\[요청-응답\]\[요청-응답\]식으로 처리되며,<br>
    \[요청 요청 요청 - 응답 응답 응답\]과 같은 병렬적 처리가 불가능<br>
    *병렬적 처리를 위해서는 여러 커넥션을 사용해야 한다.

```HTML
200 OK
Connection: Keep-Alive
Content-Encoding: gzip
Content-Type: text/html; charset=utf-8
Date: Wed, 20 Jul 2016 10:55:30 GMT
Etag: "547fa7e369ef56031dd3bff2ace9fc0832eb251a"
Keep-Alive: timeout=5, max=1000
Last-Modified: Tue, 19 Jul 2016 00:59:33 GMT
Server: Apache
Transfer-Encoding: chunked
Vary: Cookie, Accept-Encoding

```

- Head Of Line Blocking(HOL)<br>
    요청을 순차적으로 전송할 때, 앞 요청에 대한 응답이 느리게 되면 후순위의 요청은 block 되어버림

- HTTP 헤더의 중복 <br>
    연속 요청 시 헤더의 중복 발생

<br>

---

<br>

## HTTP 2.0 - 2015

<img src="/assets/images/http/http_1.1_2.0_comp1.png" width="450" height="500">


- 1.1과의 차이점<br>
    요청/응답 과정에서 대기하지 않고 하나의 connection에서 multiplexing 전송 가능

<img src="/assets/images/http/http_1.1_2.0_comp2.png" width="450" height="500">

- Binary Framing <br>
    기존 1.1에는 텍스트 기반으로 \r\n을 통해 헤더와 바디가가 구분되어 텍스트로 전송되었다. <br>
    2.0부터는 `binary frame`단위로 전송하며, 헤더와 바디를 layer로 구분한다.<br>
    전송에 필요한 메시지를 바이너리 단위로 구성하며 필요 정보를 더 작은 프레임으로 쪼개어 관리한다.

    *1.1버전의 클라이언트는 2.0버전의 서버와 통신이 불가하다<br>
    Binary Encoding 방식으로 데이터를 다루기때문에 Decoding과정이 필요

- Multiplexed Streams <br>
    브라우저가 하나의 커넥션 상에서 큐를 이용해 컨텐츠를 동시에 여러 요청을 보내는 기술<br>
    하나의 커넥션에서 stream으로 여러 요청을 병렬적으로 전송할 수 있다.


<img src="/assets/images/http/http_2.0_stream.png" width="450" height="500">













참조: https://freecontent.manning.com/animation-http-1-1-vs-http-2-vs-http-2-with-push/
참조: https://velog.io/@neity16/HTTP-HTTP-버전-별-특징