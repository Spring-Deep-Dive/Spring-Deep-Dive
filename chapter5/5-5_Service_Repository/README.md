Service, Repository 에 대해 Spring boot 코드를 중심으로 정리한다.

### Service

Controller에게 요청을 받아서 Repository를 통해 자원을 얻어온 후 비즈니스 로직을 수행하고, Controller에게 정보를 보내주는 역할이다. 보통 Service가 트랜잭션 경계가 된다.

### Repository

Entity를 통해 테이블이 생성되면 데이터베이스를 통해 CRUD 작업을 수행한다.

위의 내용을 바탕으로 Spring Data JPA를 사용하여 코드를 작성해보았다.

```
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @EmbeddedId
    private MemberId id;

    @Column(nullable = false)
    private String name;

    private Integer age;
}
```

Member는 회원 이름과 나이, 식별자를 갖고있고, 데이터베이스의 테이블과 1:1 매칭될 엔티티 클래스다.

테이블은 id(MemberId의 id), name, age 3개의 컬럼으로 구성된다.

Builder 패턴을 쓰고 싶어서 @Builder, @AllArgsConstructor 을 선언했다.

@Builder는 build() 메서드에서 모든 필드를 파라미터로 갖는 생성자를 사용하기 때문에 @AllArgsConstructor를 붙여줬다.

전체 생성자 자체는 다른 곳에서 사용할 필요가 없기 때문에 접근 제어자를 Private으로 설정했다.

기본 생성자 또한 접근 제어자를 Protected로 설정하여 다른 패키지에서 사용하게 하지 않게 만듦으로써 무분별한 객체 생성을 막을 수 있다.

도메인 영역에 해당하는 이 엔티티에 핵심 비즈니스 로직을 작성하면 된다. 엔티티만으로 비즈니스 로직을 처리할 수 없다면 서비스와 협업할 수 있다.

```
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberId implements Serializable {

    @Column
    private String id;
}
```

MemberId는 Member 엔티티의 식별자로 쓰이는 VO(Value Object) 객체다.

VO는 이름 그대로 주소값 참조(Reference)가 아닌 필드값 상태(Value)가 같다면 같은 객체로 처리해야 한다.

나는 id 값이 같다면 같은 객체로 처리하고 싶어서 lombok으로 @EqualsAndHashCode를 붙였다.

여기서 id는 데이터베이스에서 member의 PK로 쓰이는 필드다.

```
public interface MemberRepository extends JpaRepository<Member, MemberId> {
}
```

MemberRepository는 데이터베이스와 통신하기 위한 인터페이스다. 구현체는 SimpleJpaRepository.

Spring Data JPA에선 이렇게 인터페이스만 만들어주고 SimpleRepository의 기능들을 쓰면 된다.

쿼리는 기본적으로 하이버네이트가 만들어주고, 이를 통해 데이터베이스 CRUD 작업을 하게 된다.

```
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(MemberDto.JoinRequest request) {
        MemberId newMemberId = new MemberId("A".concat(UUID.randomUUID().toString()));

        Member newMember = Member.builder()
                .id(newMemberId)
                .name(request.getName())
                .age(request.getAge())
                .build();
        memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MemberDto.Response findById(String id) {
        MemberId memberId = new MemberId(id);
        Member found = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        return MemberDto.Response.of(found);
    }
}
```

MemberService는 도메인 간의 순서를 보장하고, 트랜잭션 경계가 되는 역할이다.

@Transactional만 붙여주면 스프링 트랜잭션이 적용된다.

@Service는 이 클래스를 서비스로 쓰겠다고 선언하는 어노테이션이다. 기능 상으론 @Component와 똑같다.

findById 메서드를 보자.

repository에서 member를 찾아달라고 요청한다.

만약 찾을 수 없을 시 EntityNotFoundException을 던지고 트랜잭션이 롤백된다.

member를 찾았을 경우 DTO로 가공하여 리턴한다. 

@Transactional의 readOnly 옵션을 true로 주면 영속성 컨텍스트가 더티체킹을 위한 스냅샷을 보관하지 않으므로 성능이 향상된다.

```
public class MemberDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinRequest {

        @NotBlank(message = "F001")
        private String name;

        private Integer age;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {

        private String name;

        private Integer age;

        public static Response of(Member member) {
            return new Response(member.getName(), member.getAge());
        }
    }
}
```

MemberDto는 레이어 간 Member 데이터 교환을 하기 위해 정보를 담을 수 있는 DTO이다.

개발 도중 여러 DTO 클래스를 생성하면 향후 관리하기 귀찮아져서 나는 그냥 이너 클래스로 만들어버린다.

이렇게 만든 서비스를 컨트롤러에서 아래의 예시와 같이 갖다쓰면 된다.

```
@RequiredArgsConstructor
@RequestMapping("/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid @RequestBody MemberDto.JoinRequest request) {
        memberService.join(request);

        return ResponseEntity.created(URI.create("/"))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDto.Response> findById(@PathVariable String id) {
        return ResponseEntity.ok(memberService.findById(id));
    }
}
```

### 정리

**Service**: 컨트롤러에게 비즈니스 로직 수행 요청을 받는다.

**Repository**: 서비스에게 데이터베이스 작업 수행 요청을 받는다.
