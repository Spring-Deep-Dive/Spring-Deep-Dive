**Spring Boot 3**의 **JpaRepository**에 대해 정리한다.

최상위 인터페이스인 **Repository**부터 **JpaRepository**까지 하나씩 훑어보자.

### **Repository<T, ID>**

```
@Indexed
public interface Repository<T, ID> {
}
```

_... General purpose is to hold type information as well as being able to discover interfaces that extend this one during classpath scanning for easy Spring bean creation._

CrudRepository나 PagingAndSortingRepository 등의 인터페이스 외에 따로 필요한 인터페이스가 있으면 Repository 인터페이스를 상속해주면 된다. 

### **CrudRepository<T, ID>**

```
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {
	<S extends T> S save(S entity);
	<S extends T> Iterable<S> saveAll(Iterable<S> entities);
	Optional<T> findById(ID id);
	boolean existsById(ID id);
	Iterable<T> findAll();
	Iterable<T> findAllById(Iterable<ID> ids);
	long count();
	void deleteById(ID id);
	void delete(T entity);
	void deleteAllById(Iterable<? extends ID> ids);
	void deleteAll(Iterable<? extends T> entities);
	void deleteAll();
}
```

기본적인 CRUD를 지원하는 인터페이스다.

@NoRepositoryBean 어노테이션은 이 인터페이스가 Repository 용도로서 사용되는 것이 아닌 단지 Repository의 메서드를 정의하는 인터페이스라는 정보를 부여한다.

단순히 CRUD만 필요하다면 이 인터페이스를 상속하면 된다.

### **ListCrudRepository<T, ID>**

```
@NoRepositoryBean
public interface ListCrudRepository<T, ID> extends CrudRepository<T, ID> {
   <S extends T> List<S> saveAll(Iterable<S> entities);
   List<T> findAll();
   List<T> findAllById(Iterable<ID> ids);
}
```

saveAll, findAll, findAllById를 List<T>로 반환할 수 있다.

### **PagingAndSortingRepository<T, ID>**

```
@NoRepositoryBean
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {
	Iterable<T> findAll(Sort sort);
	Page<T> findAll(Pageable pageable);
}
```

**PagingAndSortingRepository**는 **Spring Boot 3**부터 CrudRepository가 아닌 **Repository**를 상속 받는다.

이름 그대로 페이징과 정렬을 지원한다.

### **ListPagingAndSortingRepository<T, ID>**

```
@NoRepositoryBean
public interface ListPagingAndSortingRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
   List<T> findAll(Sort sort);
}
```

마찬가지로 findAll을 List<T>로 반환하게 해준다.

### **JpaRepository<T, ID>**

```
@NoRepositoryBean
public interface JpaRepository<T, ID> extends ListCrudRepository<T, ID>, ListPagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
	void flush();
	<S extends T> S saveAndFlush(S entity);
	<S extends T> List<S> saveAllAndFlush(Iterable<S> entities);
	@Deprecated
	default void deleteInBatch(Iterable<T> entities) {
		deleteAllInBatch(entities);
	}
	void deleteAllInBatch(Iterable<T> entities);
	void deleteAllInBatch();
	@Deprecated
	T getOne(ID id);
	@Deprecated
	T getById(ID id);
	T getReferenceById(ID id);
	@Override
	<S extends T> List<S> findAll(Example<S> example);
	@Override
	<S extends T> List<S> findAll(Example<S> example, Sort sort);
}
```

이제 최하위 인터페이스인 **JpaRepository**에 정의된 메서드를 하나씩 살펴보자. (Deprecated 메서드들은 제외)

-   **flush**: 영속성 컨텍스트의 모든 변경 내용을 DB에 반영한다.
-   **saveAndFlush**: 엔티티를 저장하고 플러시까지 즉시 한다.
-   **saveAllAndFlush**: 모든 엔티티를 저장하고 플러시까지 즉시 한다.
-   **deleteAllInBatch**: 지정된 엔티티들을 일괄 삭제한다. 구현체인 SimpleJpaRepository를 확인해보면 알겠지만, delete 쿼리를 하나씩 날리는 deleteAll()보다 훨씬 빠르다.
-   **getReferenceById**: Spring Boot 2.7부터 deprecated된 getById와 getByOne 대신 쓰인다. EntityManager의 getReference를 사용하며, 조회된 entity 내부값 접근 전까지 lazy loading 처리한다. 즉, 내부의 값이 필요 없을 경우 findById보다 성능상 이점이 있다.
-   **findAll**: QueryByExampleExecutor 오버라이딩 메서드이다. 주어진 Example과 일치하는 모든 엔티티를 반환한다.

### **SimpleJpaRepository<T, ID>**

```
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {
	...
}
```

JpaRepository의 구현체다.

@Repository를 선언하여 빈 스캐닝 대상으로 설정하고, SimpleJpaRepository 대부분의 메서드가 조회 기능이므로  클래스 레벨에서 @Transactional의 readOnly 옵션을 true로 준 걸 확인할 수 있다.

위에서 언급했던 **deleteAll**과 **deleteAllInBatch**을 살펴보자.

```
@Override
@Transactional
public void deleteAll() {
   for (T element : findAll()) {
      delete(element);
   }
}
```

```
@Override
@Transactional
@SuppressWarnings("unchecked")
public void delete(T entity) {
   Assert.notNull(entity, "Entity must not be null");
   if (entityInformation.isNew(entity)) {
      return;
   }
   Class<?> type = ProxyUtils.getUserClass(entity);
   T existing = (T) em.find(type, entityInformation.getId(entity));
   if (existing == null) {
      return;
   }
   em.remove(em.contains(entity) ? entity : em.merge(entity));
}
```

**deleteAll**은 foreach 안에서 delete 메서드를 호출하고 있는데, delete 메서드를 보면 EntityManager에서 조회를 한 후 삭제하는 것을 확인할 수 있다.

```
@Override
@Transactional
public void deleteAllInBatch() {
   Query query = em.createQuery(getDeleteAllQueryString());
   applyQueryHints(query);
   query.executeUpdate();
}
```

```
public abstract class QueryUtils {
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";
}
```

반면 **deleteAllInBatch**의 경우, EntityManager의 createQuery을 이용해서 한 번의 delete에 모두 삭제한다.

### 참고

[https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html](https://docs.spring.io/spring-framework/docs/current/javadoc-api/index.html)

[https://www.baeldung.com/jpa-entity-manager-get-reference](https://www.baeldung.com/jpa-entity-manager-get-reference)
