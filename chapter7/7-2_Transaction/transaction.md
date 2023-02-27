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


- `Transactional` from org.springframework.transaction.annotation


> Describes a transaction attribute on an individual method or on a class.
When this annotation is declared at the class level, it applies as a default to all methods of the declaring class and its subclasses. Note that it does not apply to ancestor classes up the class hierarchy; inherited methods need to be locally redeclared in order to participate in a subclass-level annotation. For details on method visibility constraints, consult the Transaction Management  section of the reference manual.
This annotation is generally directly comparable to Spring's org.springframework.transaction.interceptor.RuleBasedTransactionAttribute class, and in fact AnnotationTransactionAttributeSource will directly convert this annotation's attributes to properties in RuleBasedTransactionAttribute, so that Spring's transaction support code does not have to know about annotations.
Attribute Semantics
If no custom rollback rules are configured in this annotation, the transaction will roll back on RuntimeException and Error but not on checked exceptions.
Rollback rules determine if a transaction should be rolled back when a given exception is thrown, and the rules are based on patterns. A pattern can be a fully qualified class name or a substring of a fully qualified class name for an exception type (which must be a subclass of Throwable), with no wildcard support at present. For example, a value of "javax.servlet.ServletException" or "ServletException" will match javax.servlet.ServletException and its subclasses.
Rollback rules may be configured via rollbackFor/noRollbackFor and rollbackForClassName/noRollbackForClassName, which allow patterns to be specified as Class references or strings, respectively. When an exception type is specified as a class reference its fully qualified name will be used as the pattern. Consequently, @Transactional(rollbackFor = example.CustomException.class) is equivalent to @Transactional(rollbackForClassName = "example.CustomException").


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

- `Transactional` from javax.transaction

> The javax.transaction.Transactional annotation provides the application the ability to declaratively control transaction boundaries on CDI managed beans, as well as classes defined as managed beans by the Jakarta EE specification, at both the class and method level where method level annotations override those at the class level.
See the Jakarta Enterprise Beans specification for restrictions on the use of @Transactional with Jakarta Enterprise Beans.
This support is provided via an implementation of CDI interceptors that conduct the necessary suspending, resuming, etc. The Transactional interceptor interposes on business method invocations only and not on lifecycle events. Lifecycle methods are invoked in an unspecified transaction context.

