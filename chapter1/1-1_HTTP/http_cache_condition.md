# HTTP Cache

`캐시` 자주 사용되는 데이터를 임시로 복사해두는 임의의 장소

HTTP에서의 캐시?

웹 사이트의 로딩 시간을 개선하기 위해 사용!
JS, CSS와 같은 파일은 자주 변하지 않기때문에 캐싱을 통해 불필요한 네트워크 비용 발생 방지

## HTTP 캐시의 종류

- Private Cache <br>
    브라우저에 저장되는 캐시<br>
    외부에서 접근이 불가하며, `Authorization 헤더`가 포함되면 private cache에 저장되지 않는다.

- Shared Cache <br>
    브라우저와 서버 사이에서 동작하는 캐시
    - Proxy Cache <br>
        (포워드) 프록시에서 동작하는 캐시
    - Managed Cache <br>
        CDN서비스, 리버스 프록시에서 동작하는 캐시<br>
        서비스 관리자가 직접 캐시에 대한 설정을 관리하거나 리버스 프록시 설정으로 관리할 수 있다.

*포워드 프록시<br>
클라이언트와 인터넷 사이에 위치하며, 클라이언트의 정보가 서버측에 노출되지 않는다.
*리버스 프록시<br>
서버와 인터넷 사이에 위치하며, 클라이언트의 요청을 대신 받고 로드 밸런싱, 무중단 배포, DDoS등의 공격으로부터 보호


## 캐시의 유효기간
`Cache-Control`은 HTTP에서 캐시 메커니즘을 지정하기 위해 사용되는 헤더.
여러 속성이 존재하며, 대표적으로 max-age는 캐시의 최대 수명을 설정.

```HTTP
HTTP/1.1 200 OK
Content-Type: text/html
Cache-Control: max-age=3600
Content-Length: 157

<!DOCTYPE HTML>
<html lang="ko">
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
</head>
<body>
Hello, World!
</body>
</html>
```

클라이언트에서 특정 리소스에 대한 GET요청을 할 때, 서버는 Cache-Control 헤더가 포함된 응답을 보낸다. 위의 예시대로라면, 브라우저는 해당 응답을 1시간 동안 캐시에 저장한다.

이후 같은 GET 요청에 대해서 브라우저는 캐시에 저장된 데이터를 이용하며 max-age로 명시한 유효기간이 지나면 서버에 GET요청을 다시 하게 된다.

## 캐시의 유효성 검증
캐시 유효기간이 만료되어 서버로 재요청을 보내게 되었을 때, 똑같은 데이터를 받는다면 자원을 소모하게 된다. 

`캐시 유효성 검증`과 `조건부 요청`을 통해 이런 트래픽 낭비를 막기 위해, 실제 원본 데이터가 수정 되었을때만 리소스를 내려받도록 할 수 있다.


- Last-Modified / If-Modified-Since <br>
    서버측에서 GET요청에 대한 응답을 보낼 때, Last-Modified 헤더를 추가해서 전송한다.

    해당 요청을 받은 브라우저는 Last-Modified 값을 저장한다.

    캐시가 만료되어 다시 GET요청을 보낼 때, 서버측에서 Last-Modified 이후 변경이 없다면 `304 Not Modified` 응답을 보내게 된다.

- ETag / If-None-Match <br>
    서버는 GET 요청에 대해서 ETag헤더를 포함하여 전송하며, 이는 해당 리소스에 대한 버전으로 작용한다. 브라우저는 해당 값을 저장한다.

    캐시 만료 이후 브라우저에서 If-None-Match 헤더에 ETag값을 함께 전송한다.

    서버는 리소스가 변경되면 ETag값을 통해 버전을 수정하게 된다. 브라우저에서 전송한 ETag값에 해당하는게 없다면 리소스가 변경되었다는 의미로 재전송을 하게 되며, 같은 값이 존재한다면 304 Not Modified 응답을 전송한다.

- no-cache & no-store<br>
    통신 리소스가 추가적으로 발생하더라도 브라우저의 데이터가 최신상태를 유지하기 위해 `no-cache`, `no-store` 헤더를 사용할 수 있다.

    ```HTTP
    HTTP/1.1 200 OK
    Content-Type: text/html
    Cache-Control: no-cache
    ```

    - no-cache: 리소스에 대한 캐시를 생성하지만 요청 시 항상 서버에서 캐시 유효성 검증 과정을 거침

    - no-store: 리소스에 대한 캐시를 생성하지 않도록 한다.



참조: https://hudi.blog/http-cache/

