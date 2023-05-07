# Auditing

---

## Auditing이란?

- DB를 구성할 때, 해당 Row가 언제 생성, 수정됐는지를 기록하는 일이 굉장히 많다.
- 이런 일이 많다는 것과 테이블의 갯수가 많아 진다는 것은 도메인을 모델링할 때 중복되는 컬럼의 수와 코드가 많아진다는 의미다.
- 이러한 불편함을 덜기위해 JPA에서는 Audit이란 기능을 제공한다.
- Audit은 감사 , 감시의 의미로 Spring Data JPA에서 시간 값을 자동으로 넣어주는 기능.

예시

```java
@EnableJpaAuditing 
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
@Getter
@MappedSuperclass 
@EntityListeners(AuditingEntityListener.class) 
public abstract class TimeEntity{

    // Entity가 생성되어 저장될 때 시간이 자동 저장됩니다.
    @CreatedDate
    private LocalDateTime createdDate;

    // 조회한 Entity 값을 변경할 때 시간이 자동 저장됩니다.
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```

메인 클래스에서 @EnableJpaAuditing 어노테이션을 통해 Audit기능을 활성화하고,

추상클래스로 만들어서 두 컬럼을 정의한다.

@CreatedDate : 해당 엔티티가 persist될 때 하이버네이트에서 자동으로 채운 값.

@LastModifiedDate : 해당 엔티티의 dirty checking으로 인해 영속성 컨텍스트에 변화가 생기면 자동으로 채워줌.