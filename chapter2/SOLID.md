## SOLID 원칙

로버트 마틴이 명명한 객체지향 프로그래밍 및 설계의 5가지 원칙이다. 소프트웨어 작업에서 프로그래머가 소스 코드가 읽기 쉽고 확장하기 쉽게 될 때까지 소프트웨어 소스 코드를 리팩터링하여 코드 냄새를 제거하기 위해 적용할 수 있는 지침이다.

### 1\. SRP (단일 책임 원칙: Single Responsibility Principle)

한 클래스는 하나의 책임만 가져야 한다. 여기서 책임이란 **변경하려는 이유**이고, 어떤 클래스나 모듈을 변경하려는 **단 하나의 이유**만을 가져야 한다. 아래는 단일 책임 원칙을 위배하는 예시이다.

```
public class TextManipulator {  

    private String text;  

    public TextManipulator(String text) {  
        this.text = text;  
    }  

    public String getText() {  
        return text;  
    }  

    public void appendText(String newText) {  
        text = text.concat(newText);  
    }  

    public String findWordAndReplace(String word, String replacementWord) {  
        if (text.contains(word)) {  
            text = text.replace(word, replacementWord);  
        }  
        return text;  
    }  

    public String findWordAndDelete(String word) {  
        if (text.contains(word)) {  
            text = text.replace(word, "");  
        }  
        return text;  
    }  

    public void printText() {  
        System.out.println(this.getText());  
    }  
}
```

TextManipulator 클래스는 텍스트를 처리하고 출력하는 2가지 책임을 갖고 있다. 이는 단일 책임 원칙에 위배되므로 텍스트를 출력하는 클래스를 새로 만들어야 한다.

```
public class TextPrinter {  

    TextManipulator textManipulator;  

    public TextPrinter(TextManipulator textManipulator) {  
        this.textManipulator = textManipulator;  
    }  

    public void printText() {  
        System.out.println(textManipulator.getText());  
    }  

    public void printOutEachWordOfText() {  
        System.out.println(Arrays.toString(textManipulator.getText().split("  ")));  
    }  

    public void printRangeOfCharacters(int startingIndex, int endIndex) {  
        System.out.println(textManipulator.getText().substring(startingIndex, endIndex));  
    }  
}
```

클래스를 분리함으로써 각 객체에 대한 책임이 분명해지고 결과적으로 응집도가 높아졌다.

### 2\. OCP (개방 폐쇄 원칙: Open-Closed Principle)

소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다. 간단히 말해서 그냥 추상화하고 상속하라는 얘기다. 아래는 개방 폐쇄 원칙을 위배하는 예시이다.

```
public class Main {  

    public static void main(String[] args) {  
        Hello hello = new Hello();  

        Member honggildong = new Member("honggildong");  
        Member johndoe = new Member("johndoe");  
        Member mouren = new Member("mouren");  

        hello.run(honggildong);
        hello.run(johndoe);   
        hello.run(mouren);  
    }  
}
```

```
public class Hello {  

    public void run(Member member) {  
        if ("honggildong".equals(member.getName())) {  
            System.out.println("안녕하세요.");  
        } else if ("johndoe".equals(member.getName())) {  
            System.out.println("Hello.");  
        } else if ("mouren".equals(member.getName())) {  
            System.out.println("你好。");  
        }  
    }  
}
```

```
public class Member {  

    private String name;  

    public Member(String name) {  
        this.name = name;  
    }  

    public String getName() {  
        return name;  
    }  
}
```

이 상태에선 새로운 Member가 추가 될 때마다 클래스를 일일이 바꿔줘야 한다. 여기서 Member 클래스를 추상화하고 이를 상속하여 확장시키는 형태로 바꾼다면, 확장에는 열려있고 변경에는 닫힌 코드를 만들 수 있다.

```
abstract class Member {  
    abstract void hello();  
}  

class HongGilDong extends Member {  
    void hello() {  
        System.out.println("안녕하세요.");  
    }  
}  

class JohnDoe extends Member {  
    void hello() {  
        System.out.println("hello.");  
    }  
}  

class Mouren extends Member {  
    void hello() {  
        System.out.println("你好。");  
    }  
}
```

```
public class Hello {  

    public void run(Member member) {  
        member.hello();  
    }  
}
```

```
public class Main {  
    public static void main(String[] args) {  
        Hello hello = new Hello();  

        Member honggildong = new HongGilDong();  
        Member johndoe = new JohnDoe();  
        Member mouren = new Mouren();  

        hello.run(honggildong);  
        hello.run(johndoe);  
        hello.run(mouren);  
  }  
}
```

추상화를 함으로써 추후 다른 Member가 추가되더라도 Hello 클래스의 수정이 불필요하게 되었다.

### 3\. LSP (리스코프 치환 원칙: Liskov Substitution Principle)

프로그램의 객체는 프로그램의 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀 수 있어야 한다. 아래는 리스코프 치환 원칙을 위배하는 예시이다.

```
public class Rectangle {  

    private int width;  
    private int height;  

    // getter & setter...
}  

class Square extends Rectangle {  

  @Override  
  public void setWidth(final int width) {  
        super.setWidth(width);  
        super.setHeight(width);  
  }  

  @Override  
  public void setHeight(final int height) {  
        super.setWidth(height);  
        super.setHeight(height);  
  }  
}
```

정사각형은 직사각형이지만, 직사각형은 정사각형이 아니므로 Square 클래스를 따로 정의하였다. 여기서 높이가 너비보다 같거나 작을 경우 높이을 늘이는 메서드를 추가한다고 가정해보자.

```
public void increaseHeight(final Rectangle rectangle) {  
    if(rectangle.getHeight() <= rectangle.getWidth()) {  
        rectangle.setHeight(rectangle.getWidth() + 1);  
    }  
}
```

직사각형의 경우 위 메서드는 의도한 대로 문제없이 잘 작동한다. 높이가 2, 너비가 3인 직사각형의 경우엔 높이가 너비보다 작으므로 높이는 4가 될 것이다. 그러나 정사각형의 경우엔 높이와 너비가 같으므로 둘 다 1씩 증가하게 된다. 조건문을 넣어서 이 문제를 회피할 수 있겠지만, 그러한 임시방편 사용 자체가 근본적으로 리스코프 치환 원칙을 위배하는 것이고 increaseHeight가 확장에 열려있지 않다는 뜻이다. 따라서 상위 타입에서 정한 명세를 하위 타입에서도 그대로 지킬 수 있을 때 상속을 해야 한다.

### 4\. ISP (인터페이스 분리 원칙: Interface Segregation Principle)

클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야 한다. 아래는 인터페이스 분리 원칙을 위배하는 예시이다.

```
abstract class Vehicle {  

    abstract void go();  

    abstract void fly();  
}  

class Car extends Vehicle {  

    @Override  
    public void go() {  
        System.out.println("go");  
    }  

    @Override  
    public void fly() {  

    }  
}  

class Airplane extends Vehicle {  

    @Override  
    public void go() {  

    }  

    @Override  
    public void fly() {  
        System.out.println("fly");  
    }  
}
```

Car 클래스는 fly() 메서드를 사용하지 않을 것이지만, Vehicle 클래스를 상속받고 있기 때문에 반드시 구현해야 한다. 이 코드를 인터페이스 분리 원칙을 지키면서 다음과 같이 변경할 수 있다.

```
abstract class Movable {  

    abstract void go();  
}  

class Car extends Movable {  

    @Override  
    public void go() {  
        System.out.println("go");  
    }  
}  

abstract class Flyable extends Movable {  

    abstract void fly();  
}  

class Airplane extends Flyable {  

    @Override  
    void go() {  

    }  

    @Override  
    void fly() {  
        System.out.println("fly");  
    }  
}
```

이제 Car 클래스는 go() 메서드만 구현하고 필요 없던 fly() 메서드를 구현하지 않아도 되므로, 인터페이스 분리 원칙을 만족하게 된다.

### 5\. DIP (의존관계 역전 원칙: Dependency Inversion Principle)

추상화에 의존해야지, 구체화에 의존하면 안된다. 아래는 의존관계 역전 원칙을 위배하는 예시이다.

```
public class PayService {  

    private SamsugPay samsungPay;  

    public void setSamsungPay(final SamsungPay samsungPay) {  
        this.samsungPay = samsungPay;  
    }  

    public void payment() {  
        samsungPay.payment();  
    }  
}
```

```
public class SamsungPay {  

    public void payment() {  
        System.out.println("Samsung Pay");  
    }  
}
```

삼성페이를 지원하는 서비스이다. 여기서 요구사항이 변경되어 애플페이도 지원한다고 했을 때, Pay 클래스 뿐만 아니라 관련된 클래스(예시의 경우 PayService)들 모두가 변경되어야 한다. 이러한 상황을 피하기 위해 고수준 모듈(PayService)가 저수준 모듈(SamsungPay)를 직접 참조하는 것이 아니라 저수준 모듈이 인터페이스 등 추상을 매개체로 고수준 모듈을 참조하도록 의존관계를 바꾸어야 한다.

```
public class PayService {  

    private Pay pay;  

    public void setSamsungPay(final Pay pay) {  
        this.pay = pay;  
    }  

    public void payment() {  
        pay.payment();  
    }  
}
```

```
public class SamsungPay implements Pay {  

    @Override  
    public void payment() {  
        System.out.println("Samsung Pay");  
    }  
}
```

```
public class ApplePay implements Pay {  

    @Override  
    public void payment() {  
        System.out.println("Apple Pay");  
    }  
}
```

이제 요구사항이 변경되어 다른 결제 수단이 추가되더라도, 고수준 모듈에서의 변경을 최소화 할 수 있게 되었다.

### 출처

[https://en.wikipedia.org/wiki/SOLID](https://en.wikipedia.org/wiki/SOLID)  
[https://www.baeldung.com/java-single-responsibility-principle](https://www.baeldung.com/java-single-responsibility-principle)
