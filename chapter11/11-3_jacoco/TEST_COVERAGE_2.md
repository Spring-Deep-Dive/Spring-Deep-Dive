### **JaCoCo**

JaCoCo는 Java 코드의 커버리지를 체크하는 라이브러리이다. 

테스트코드를 돌리고 그 커버리지 결과를 눈으로 보기 좋도록 html이나 xml, csv 같은 리포트로 생성한다.

그리고 테스트 결과가 내가 설정한 커버리지 기준을 만족하는지 확인하는 기능도 있다.

[https://www.jacoco.org/jacoco/trunk/coverage/](https://www.jacoco.org/jacoco/trunk/coverage/)

**build.gradle**

보다 더 자세한 세팅 방법은 [이곳](https://techblog.woowahan.com/2661/)을 참고 바란다.

```
plugins {
	...
    id 'jacoco' // 플러그인 추가
}

jacoco {
    // JaCoCo 버전
    toolVersion = '0.8.9'

    //  테스트 결과 리포트를 저장할 경로 변경
    //  default는 "${project.reporting.baseDir}/jacoco"
    reportsDir = file("$buildDir/customJacocoReportDir")
}

task testCoverage(type: Test) {
    group 'verification'
    description 'Runs the unit tests with coverage'

    dependsOn(':test',
            ':jacocoTestReport',
            ':jacocoTestCoverageVerification')

    tasks['jacocoTestReport'].mustRunAfter(tasks['test'])
    tasks['jacocoTestCoverageVerification'].mustRunAfter(tasks['jacocoTestReport'])
}

jacocoTestReport {
    reports {
        // 원하는 리포트를 켜고 끌 수 있습니다.
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)

        //  각 리포트 타입 마다 리포트 저장 경로를 설정할 수 있습니다.
//        html.destination file("$buildDir/jacocoHtml")
//        xml.destination file("$buildDir/jacoco.xml")
    }
    finalizedBy 'jacocoTestCoverageVerification'
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true // 활성화
            element = 'CLASS' // 클래스 단위로 커버리지 체크
            // includes = []

            // 라인 커버리지 제한을 80%로 설정
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }

            // 브랜치 커버리지 제한을 80%로 설정
            limit {
                counter = 'BRANCH'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }

            excludes = []
        }
    }
}
```

**Foo.java**

```
public class Foo {

    public String run(String s) {
        return switch (s) {
            case "Ping" -> "Pong";
            case "Hello" -> "World";
            default -> "default";
        };
    }

    public void callMe() {
        System.out.println("Please, call me.");
    }
}
```

**FooTest.java**

```
class FooTest {

    private final Foo foo = new Foo();

    @Test
    public void test1() {
        String actual = foo.run("Ping");
        assertEquals(actual, "Pong");
    }
}
```

테스트를 실행하면 **build/customJacocoReportDir/test/html/index.html** 이 생성된다.

해당 파일에선 다음과 같이 각 커버리지 항목마다 총개수와 놓친 개수를 표시해 준다.

<img src="/assets/images/JACOCO/1.png">

코드 파일에서는 커버가 된 라인은 초록색, 놓친 부분은 빨간색으로 표시해 준다.

노란색은 모든 조건이 아닌 일부만 테스트된 라인이다.

<img src="/assets/images/JACOCO/2.png">
