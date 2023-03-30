# QueryDSL 세팅 방법

> Querydsl defines a general statically typed syntax for querying on top of persisted domain model data. JDO and JPA are the primary integration technologies for Querydsl. This guide describes how to use Querydsl in combination with JPA.

**JDO(Java Data Objects)**: 데이터 영속을 위한 POJO, standard way to access persistent data in DB, using POJO to represent persistent data.

QueryDSL은 영속된 최상위 도메인 모델의 데이터에 따라서 정적으로 쿼리를 정의한다. 


- 출처
    - Tutorials, QueryDSL,http://querydsl.com/static/querydsl/latest/reference/html/ch02.html
    - Java Data Objects, https://db.apache.org/jdo/


## dependency

QueryDSL을 사용하기 위해 필요한 의존성 라이브러리는 다음과 같다.

- QueryDSL core
    - DB쿼리 생성을 위한 코어.
- QueryDSL JPA Library(optional)
    - DB에 접근하기 위해 사용되며, 이를 위해 QueryDSL JPA 라이브러리가 포함되어야 한다. JPA-specific 쿼리 타입과 표현식이 지원된다.
- QueryDSL SQL Library(optional)
    - SQL DB와 함께 QueryDSL 사용 시 포함된다. SQL-specific 쿼리 타입과 표현식이 지원된다.
- DB driver
- Java 8 or higher




- Maven

```Java
<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-apt</artifactId>
  <version>${querydsl.version}</version>
  <scope>provided</scope>
</dependency>

<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
  <version>${querydsl.version}</version>
</dependency>
```

- Gradle
```Java
buildscript {
    ext {
        queryDslVersion = "5.0.0"
    }
}

plugins {
    id 'java'
    id 'org.springframework.boot' version '2.6.3'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    // querydsl 추가
    id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"

}

group = 'springdeepdive'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'



configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.0'

    /*
    QueryDSL은 프로젝트 내의 @Entity 클래스를 탐색하고, JPAAnnotationProcessor를 통해 Q클래스를 생성한다.
    querydsl-apt: @Entity, @Id 등의 annotation을 알수있도록 함
    javax.persistence, javax.annotation을 annotation processor에 추가
    */

    // QueryDSL
    def queryDSL = '5.0.0'
    compile("com.querydsl:querydsl-jpa:${queryDSL}")
    compile("com.querydsl:querydsl-apt:${queryDSL}:jpa")
    implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
    implementation "com.querydsl:querydsl-apt:${queryDslVersion}"
    annotationProcessor "org.springframework.boot:spring-boot-configuration-processor"

    annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa"
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")


    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}


//querydsl 설정부 시작
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
//    library = "com.querydsl:querydsl-apt"
    jpa = true
    querydslSourcesDir = querydslDir
}

// 개발 환경에서 생성된 Q클래스를 사용할 수 있도록 generated 디렉토리를 sourceSet에 추가
sourceSets {
    main.java.srcDir querydslDir
}

compileQuerydsl{
    options.annotationProcessorPath = configurations.querydsl
}

// annotation processor 경로 설정
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
    querydsl.extendsFrom compileClasspath
}

//querydsl 설정부 끝



tasks.named('test') {
    useJUnitPlatform()
}

```

### Gradle을 통한 설정 흐름

1. plugin 추가
2. dependencies 추가
3. def 정의
4. querydsl 추가
5. sourceSets 추가
6. compileQuerydsl 추가
7. configurations 추가
8. build/compiler/annotation process의 enable annotation processing 설정

위의 흐름으로 진행된다.<br>
gradle을 사용함에 있어 가장 치명적인 것은 `queryDSL 공식 문서에서 Gradle 기반 환경설정에 대한 언급`이 없다는 점이다. 기본적으로 Maven에 대한 소개만을 하고 있다.<br>

환경설정과 Q클래스가 정상적으로 생성(gradle-other-compileQuerydsl)되었다면, `build/generated`디렉토리에서 이를 확인할 수 있다. 단, 사용하는 IDE와 설정에 따라 위치가 변경될 수 있다.<br>

왜 Gradle만 빠져있나? 라는 의문과 환경설정 과정에서 도움을 준 블로그는 아래 참조에 최상단에 첨부했다.<br><br>

### Gradle의 문제
gradle은 프로젝트 의존성 관리 기능을 제공하는데, 버전이 빠르게 업데이트 된다. 이 과정에서 기존의 스크립트가 동작하지 않는 등의 문제가 발생한다.<br> 현 케이스에서는 Q클래스를 생성하는 QueryDSL JPA 플러그인이 문제가 많다. QueryDSL에서는 @Entity 어노테이션을 탐색하고 이를 AnnotationProcessor를 통해 Q클래스를 생성하는데, 이는 자바 언어가 지닌 정적 코드의 장점을 활용하기에 안전하다고 한다.<br>
AnnotationProcessor는 gradle 4.6이후로 소개되었는데, 2018년에 소개된 `com.ewerk.gradle.plugins.querydsl`플러그인이 많이 사용되었다. 문제는 gradle 5.* 이상부터는 해당 플러그인이 정상적으로 동작하지 않게 되었고, 1.* 이후로 버전업이 없다. 이러한 버전 문제때문에 `configurations`에서 annotation processor의 경로를 설정해줘야 한다.

### IntelliJ IDEA의 문제
queryDSL은 gradle만의 문제로 끝이 나지 않음을 확인했다. 사용하는 IDE, 대표적으로 IntelliJ의 버전에 따라서도 달라질 수 있다. 정확한 IntelliJ의 버전별 차이는 확인하지 못하였으나, 결정적으로 build 설정에 있어 Q클래스의 생성 위치와 참조가 달라지게 된다.<br>

- build/Gradle 설정
    build and run / Run tests using : IntelliJ IDEA

- build/Compiler/Annotation Processors
    - Gradle Imported : store generated sources relative to `Module content root`


> 라이브러리 버전 리스트
    Java - 17
    IntelliJ - 2021.3
    Spring boot - 2.6
    Gradle - 7.6.1
    Spring Data JPA - 2.6
    QueryDSL - 5.0


- 참조
    - http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html
    - https://velog.io/@jkijki12/Spring-QueryDSL-%EC%99%84%EB%B2%BD-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0
    - https://devfoxstar.github.io/java/intellij-querydsl-error/#gradle-%EC%84%A4%EC%A0%95-%ED%99%95%EC%9D%B8
