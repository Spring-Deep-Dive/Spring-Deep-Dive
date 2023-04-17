> _**얼마만큼의 코드를 자동화한 단위 텍스트로 계산해야 할까? 대답할 필요조차 없다.**_   
> _**모조리 다 해야 한다. 모.조.리!**_  
> _**100% 테스트 커버리지를 권장하냐고? 권장이 아니라 강력히 요구한다.**_   
> _**작성한 코드는 한 줄도 빠짐없이 전부 테스트해야 한다. 군말은 필요 없다.**_  
> \-로버트 마틴 저, 정희종 역. 클린 코더(2016) 중에서.

[https://youtu.be/jdlBu2vFv58](https://youtu.be/jdlBu2vFv58)

지금으로부터 2년 전, 토스 개발자 컨퍼런스에서 **테스트 커버리지 100%**에 도달했다는 발표가 있었다.**

커버리지를 모두 만족해버려서 배포하는 것에 대한 두려움이 사라진다니!

테스트 커버리지가 무엇이고 어떤 장점이 있는지 알아보자.

### **코드 커버리지(Code Coverage)란?**

-   소프트웨어의 테스트를 논할 때 얼마나 테스트가 충분한가를 나타내는 지표 중 하나.
-   소프트웨어 테스트를 진행했을 때 코드 자체가 얼마나 실행되었는지 숫자로 볼 수 있다.

### **코드 커버리지는 어떤 기준으로 측정할까?**

코드의 구조를 살펴보면 크게 구문(Statement), 조건(Condition), 결정(Decision)의 구조로 이루어져 있다.

코드 커버리지는 이러한 코드의 구조를 얼마나 커버했느냐에 따라 측정기준이 나뉘게 된다.

**구문(Statement)**

Statement Coverage 혹은 Line Coverage라고 부른다.

코드 한 줄이 한 번 이상 실행된다면 충족된다.

```
void foo (int x) {
    sout("start line"); // 1번
    if (x > 0) { // 2번
        sout("middle line"); // 3번
    }
    sout("last line"); // 4번
}
```

위의 코드를 테스트한다고 가정해보자.

**x = -1**을 테스트 데이터로 사용할 경우, if문을 통과하지 못하기 때문에 3번 라인은 실행되지 않는다.

총 4개의 라인 중 1, 2, 4번 라인만 실행되므로 **라인 커버리지**는 **3 / 4 \* 100 = 75(%)**가 된다.

**결정(Decision)**

Decision Coverage 혹은 Branch Coverage라고 부른다.

모든 조건식이 true/false를 가지게 되면 충족된다.

```
void foo (int x, int y) {
    sout("start line"); // 1번
    if (x > 0 && y < 0) { // 2번
        sout("middle line"); // 3번
    }
    sout("last line"); // 4번
}
```

위의 코드에서, **브랜치 커버리지**를 만족하는 테스트 케이스는 **x = 1, y = -1**, **x = -1, y = 1**이 있다.

전자의 경우 true를 반환하고, 후자의 경우 false를 반환하므로 모든 **브랜치 커버리지**를 충족시킨다.

**조건(Condition)**

각 내부 조건식이 true/false을 가지게 되면 충족된다.

```
void foo (int x, int y) {
    sout("start line"); // 1번
    if (x > 0 && y < 0) { // 2번
        sout("middle line"); // 3번
    }
    sout("last line"); // 4번
}
```

위의 코드에서, **조건 커버리지**를 만족하는 테스트 케이스는 **x = 1, y = 1, x = -1,y = -1** 이 있다.

**x > 0** 내부 조건에 대해 **true/false**를  만족하고, **y < 0** 내부 조건에 대해선 **false/true**를 만족한다.

그러나 테스트 케이스는 if문에서 false만 반환한다.

내부 조건 x > 0, y < 0에 대해서는 각각 true와 false 모두 나왔지만 if 조건문의 관점에서 보면 false에 해당하는 결과만 발생하는데, 이로 인해 3번 라인이 실행되지 않는다.

따라서 **컨디션 커버리지**를 만족하도록 테스트를 작성했을 경우, **라인 커버리지**와 **브랜치 커버리지**를 만족하지 못하게 될 수도 있다.

### **라인 커버리지보다 패스 커버리지**

많은 개발자들이 라인 커버리지를 중요시하지만 맹목적인 라인 커버리지 수치만을 쫓을 경우 코드 품질의 저하를 초래할 수 있다. 또한 위에서 살펴보았듯, 라인 커버리지와 브랜치 커버리지를 모두 만족함에도 컨디션 커버리지를 만족하지 못하는 경우가 생긴다. 이에 관해 다음 글을 참고하길 바란다.

[http://aeternum.egloos.com/v/1209470](http://aeternum.egloos.com/v/1209470)

### **참고**

[https://en.wikipedia.org/wiki/Code\_coverage](https://en.wikipedia.org/wiki/Code_coverage)

[https://tecoble.techcourse.co.kr/post/2020-10-24-code-coverage/](https://tecoble.techcourse.co.kr/post/2020-10-24-code-coverage/)

[http://aeternum.egloos.com/v/1209470](http://aeternum.egloos.com/v/1209470)
