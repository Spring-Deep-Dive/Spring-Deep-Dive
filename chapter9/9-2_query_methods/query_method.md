# Spring Data JPA 쿼리 메소드

Spring Data JPA를 이용해서 쿼리를 생성하는 방법은 여럿 존재한다.<br>

## 메소드 이름 기반 쿼리 생성
Repository 인터페이스의 네이밍 룰을 이용하여 메소드를 작성하여 쿼리를 동작시킬 수 있다.<br>
가장 대표적으로 사용되는 조회 메소드들은 find 키워드로 시작되며, 경우에 따라 조건을 명시할 수 있다.<br>
<br>

```Java
public interface ItemRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderLocation(String location);

    List<Order> findByOrderPrice(Long price);

    List<Order> findByOrderPriceLessThan(Long price);

    List<Order> findByOrderPriceLessThanOrderByPriceDesc(Long price);
}
```


다음의 레퍼런스에서 JPA에서 지원하는 키워드와 해당 키워드가 어떤 역활을 하는지 알 수 있다.<br>
(https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)<br>



