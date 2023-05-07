# 도메인 클래스 컨버터(Domain Class Converter)

## 도메인 클래스 컨버터

- Spring Data JPA의 확장된 기능으로 의존성을 추가하면 자동으로 사용할 수 있다.

사용 전/후 예시

```java
// @Transactional  -> 동작과정
public class TestContorller {
	
		private final MemberRepository repository;

		// 사용 전
		@GetMapping("/{id}")
		public User getUserById(@PathVaribale("id") Long id){
			Member member = repository.getMemberById(id);
			return member;
		}

		// 사용 후
		@GetMapping("/v2/{id}")
		public User getUserByConverter(@PathVaribale("id") Member member){
			return member;
		}

}

```

- Spring MVC에서 메시지 컨버터에서 제공하는 기능과 비슷하게 id로 바로 멤버를 조회해서 매칭시켜주는 기능

## 주의 사항

- 내부적으로 조회 기능을 이용하여 해당 id에 대한 엔티티를 조회하지만 트랜잭션이 관리되는 범위가 아니기 때문에 영속성 컨텍스트에 의해 관리되지 않는다.
- 값을 수정하거나 하지 않고 readonly로서 사용하는게 가장 좋다.