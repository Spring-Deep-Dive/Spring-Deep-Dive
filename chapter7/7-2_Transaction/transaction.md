# Transactional Annotaiton from javax & spring framework

Transactional: 수행하는 작업에 대해 트랜잭션 원칙이 지켜지도록 보장해주며, 예외 발생 시 rollback 처리를 자동으로 해주며, begin/commit을 자동으로 수행한다.

- @javax.transaction.Transactional
- @org.springframework.transaction.annotation

스프링에서 사용할 수 있는 Transactional 어노테이션은 위와 같다.


## From javax

패키지 이름에서 말하듯, 자바 표준 확장 패키지로 자바에서 기본 제공한다.

```Java
public @interface Transactional {
    TxType value() default TxType.REQURIED;

    public enum TxType {
        ...
    }

    @Nonbinding
    public class[] rollbackOn() default {};

    @Nonbinding
    public class[] dontRollbackOn() default {};


}
```

기본적인 트랜잭션 타입(TxType)과 Rollback(rollbackOn()) 정도만 구현되어있다.


## from springframework

```JAVA
public @interface Transactional {

    @AliasFor("transactionManager")
    String value() default "";

    @AliasFor("value")
    String transactionManager() default "";

    String[] label() default {};

    // 트랜잭션 동작 중 다른 트랜잭션을 호추할 때 처리 방식 지정
    //  REQURIED - 진행중인 트랜잭션이 있다면 해당 트랜잭션을 따르며, 아닌 경우 새로운 트랜잭션 생성
    Propagation propagation() default Propagation.REQUIRED;

    // 일관성 없는 데이터 허용 수준
    Isolation isolation() default Isolation.DEFAULT;

    // 지정된 시간 내에 메소드 수행이 완료되지 않을 시 Rollback
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    boolean readOnly() default false;

    // rollbackFor - 특정 예외 발생 시 rollback
    Class<? extends Throwable>[] rollbackFor() default {};

    String [] rollbackForClassName() default {};

    Class<? extends Throwable>[] noRollbackFor() default{};

    String [] noRollbackForClassName() default {};
    
}
```


