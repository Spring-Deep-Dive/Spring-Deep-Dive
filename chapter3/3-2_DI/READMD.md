# 스프링 DI

---

---

## 의존성 주입이란

- 한 객체가 의존하고 있는 다른 객체를 외부에서 대신 주입하는 것,
- 물론 DI가 없어도 직접 구현클래스를 사용할 수 있다.

```java
public class MemberServiceImpl implements OrderService {

	private MemberRepository repository = new MemberRepositoryImpl();
	//... do something

}
```

물론 코드는 정상동작을 하겠지만 MemberRepository의 구현체가 바뀌게 된다면 코드의 수정이 일어날 여지가 있고 유연한 설계가 어렵다.

더군다나 이는 객체지향의 설계 원칙 SOLID 중 DIP와 OCP를 위반한다.

- MemberRepository(Interface)가 직접 구현체를 의존 (MemberRepositoryImpl)
    - 추상화에 의존이 아니라 직접 구현체를 의존하므로 DIP의 위반
- MemberRepositoryImpl를 테스트용으로 외부 DB를 사용하는 것이 아니라 인메모리 DB를 사용하는 등의 변경사항이 생길 때, 실제 서비스 클래스인 MemberServiceImpl에서 코드 변경이 일어나야한다.
    - OCP 위반


이런 문제들을 해결하고 더욱더 유연한 설계와 객체 지향의 장점을 적극 활용하기 위해 Dependency Injection의 종류에 대해 알아보자.

---

## 생성자 주입

생성자 주입은 객체의 생성자를 통해 필드에 선언된 객체의 구현체를 주입하는 방식이다.

```java
// 변경 전
public class MemberServiceImpl implements OrderService {

	private final MemberRepository repository = new MemberRepositoryImpl();
	//... do something

}

// 변경 후
public class MemberServiceImpl implements OrderService {

		private final MemberRepository repository;
		//... do something

	// 객체가 생성되는 시점에 파라미터로 전달받은 객체(MemberRepository)를 repository로 할당.
	public MemberServiceImpl(MemberRepository repository){
		this.repository = repository;
	}
}

```

---

## 필드 주입

Autowired 어노테이션을 통해 필드의 의존성을 주입하는 방식으로 사용방벙이 매우 간단하다는 장점이 있으나

Intelli J에서는 Field injection is not recommended라는 경고 문구를 출력한다.

```java
public class MemberServiceImpl implements OrderService {

		@Autowired
		private MemberRepository repository;
		//... do something
}
```

---

## 수정자 주입

자바의 Getter / Setter를 사용해 필드 변수 repository에 구현체를 할당하는 방법이다.

```java
public class MemberServiceImpl implements OrderService {

		private MemberRepository repository;

		public void setRepository(MemberRepository repository){
			this.repository = repository;
		}

		//... do something
}
```

---

## 어떤 주입 방법을 선택해야할까?

### 순환 참조

- 필드 주입의 방법은 어플리케이션 로딩 이후에 구현체를 할당하기 때문에 순환 참조를 일으킬 여지가 있다.
    - 순환 참조란?
        - A가 B를 의존하고, 다시 B가 A를 의존하면서 A에서 B를 호출, B에서 A를 호출하면 메서드 콜 스택이 무한히 늘어나면서 StackOverflow Exception이 발생한다.

### 예상하지못한 다른 객체의 주입.

- 수정자,필드 주입은 앞서 언급했듯이 구현체의 할당 시기 때문에 final 키워드를 붙일 수 없다.
- 이말인 즉,객체가 의도하지 않게 변경될 수 있다는 의미고 이는 예상치 못한 사이드 이펙트를 불러올 수 있다.

### Null….

- 수정자,필드 주입은 많이 일어나는 실수가 아니지만 주입을 해주지 않거나 다른 실수가 일어나면 NPE가 일어날 여지가 있다.

### 정답은요..

- 생성자 주입이 권장된다.
- 어플리케이션 로딩시점에 빈을 등록하고 주입을 받기 때문에 순환 참조를 방지할 수 있다.
- 또한 생성자를 통해 주입을 하기 때문에 final 키워드를 사용하여 필드 객체의 불변성을 보장하고 NPE또한 방지할 수 있다.%