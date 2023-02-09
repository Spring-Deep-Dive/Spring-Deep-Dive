# 4-1.스프링의 내장 톰캣

---

# 스프링의 내장톰캣

- 스프링의 내장 톰캣은 현재의 스프링이 입지를 다지는데 굉장히 큰 요소 중 하나이다.
- 이전에는 별도의 외부의 톰캣을 설치해서 빌드한 파일을 톰캣에 옮겨서 별도의 설정 파일을 다 직접 손봐야했다.
- 스프링은 스프링 부트 프로젝트에서 위와 같은 일련의 고비용 작업들을 개선하기 위해 톰캣을 기본 WAS로 내장시켜버렸다.

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.3'
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

스프링 프로젝트 생성시 build.gradle파일에서 Spring-web을 추가하게 되면     implementation 'org.springframework.boot:spring-boot-starter-web' 라이브러리가 추가되는데

이 라이브러리가 톰캣과 관련된 라이브러리를 가져온다.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/c8a557c8-6fc2-41ee-839a-496a8b185cb4/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20230209%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20230209T132104Z&X-Amz-Expires=86400&X-Amz-Signature=b6b8f03e133fbf516d2ca8bc04b9066ba90dfda9ba3b99b21a8862af82172577&X-Amz-SignedHeaders=host&response-content-disposition=filename%3D%22Untitled.png%22&x-id=GetObject)

이러한 점이 스프링이 현재 입지를 다지는데 굉장히 큰 요소로 볼 수 있다.

이제는 프로그램을 실행만 하게되면 톰캣이 자동으로 실행이 되니 굉장히 편한 점이다.

스프링의 본래 취지와 맞게 개발에 불필요한 설정에 대한 리소스를 최소화 할 수 있다.

---

# 차이점

- 개발을 하면서 항상 A에서도 지원되는 기능, B에서도 지원되는 기능이라면 어떤 차이점이 있느냐가 기술적 부채를 줄이는 방법인 것 같다.
- 그렇다면 기존에 외장 톰캣과 내장 톰캣은 어떤 차이가 있을까?

## 실행 방법의 차이

### 내장

- 빌드된 파일을 스프링 부트에서 java명령어로 실행

### 외장

- Tomcat 설치
- 설정파일 구성
- 디렉토리에 실행 파일 구성
- 실행

### 이외에 차이점은 큰 차이점은 딱히 없다.

외장 톰캣에서 virtual host의 기능을 지원하기는 하지만 내장 톰캣도 설정에 따라 가능한 부분이고

이는 Nginx와 같은 웹 서버에서도 충분히 커버할 수 있고, WAS와 역할과는 거리가 먼 부분이기 때문에 사실상의 큰 차이는 없다고 봐도 무방하다.

- 내장 톰캣을 실행하는게 스프링 부트 앱 하나의 프로그램으로써 하나의 포트를 독자적으로 점유하기 때문에 리버스 프록시 등의 기능을 사용하기 복잡하다는 단점이 있지만 그 책임은 웹 서버에게 위임하고 개발자는 개발에만 집중할 수 있는 것이 좋을 듯 하다.
- 내,외장 톰캣은 설정 부분도 차이가 있기 때문에 application.properties에 좀 더 간편하게 설정 및 테스트를 할 수 있다.