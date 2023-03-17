### **1\. JPA의 Pagination API**

Pagination API를 사용하면 DB 벤더에 따라 천차만별인 페이징 쿼리를 따로 작성해주지 않아도 된다.

JPA가 DB 벤더에 맞는 페이징 쿼리를 자동으로 생성해주기 때문이다.

```
@DisplayName("간단한 페이징을 적용해본다.")
@Test
void usePagination() {
    List<Member> members = entityManager.createQuery("select m from Member m", Member.class)
            .setFirstResult(0)
            .setMaxResults(10)
            .getResultList();
}
```

```
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 limit ?, ?
```

현재 사용 중인 MySQL의 Dialect에 따라 JPQL 쿼리 및 Pagination 기능이 SQL 쿼리로 변환되었다.

### **2\. Spring Data JPA** 

Data JPA에선 **JpaRepository**가 **PagingAndSortingRepository**를 상속받았기 때문에 페이징과 정렬을 더욱 손쉽게 할 수 있다.

```
@NoRepositoryBean
public interface PagingAndSortingRepository<T, ID> extends Repository<T, ID> {
   Iterable<T> findAll(Sort sort);
   Page<T> findAll(Pageable pageable);
}
```

**PagingAndSortingRepository**의 **findAll()** 메서드에서 파라미터로 **Pageable**을 받는 것을 확인할 수 있다.

**Pageable**의 구현체인 **PageRequest**를 이용하여 다음과 같이 사용할 수 있다. 

```
@DisplayName("Pageable을 이용하여 페이징을 적용해본다.")
@Test
void usePaginationWithPageable() {
    // 아래 두 줄은 같은 동작을 수행한다.
    memberRepository.findAll(Pageable.ofSize(10));
    memberRepository.findAll(PageRequest.of(0, 10));
}
```

```
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 limit ?, ?
Hibernate: 
    select count(m1_0.id) 
    from member m1_0
```

그런데 로그를 확인해보면 **count** 쿼리가 자동으로 생성되어 실행된 것을 확인할 수 있다. 왜일까?

### **3\. Page, Slice, List**

JPA의 경우 반환값으로 Page, Slice, List 등을 지원한다.

**Page** 구현체의 경우 전체 Page의 크기를 알아야하므로, 필요한 Page의 요청과 함께 전체 페이지의 수를 계산하는 count 쿼리가 별도로 실행된다. JpaRepository의 구현체인 **SimpleRepository**에서 **findAll(Pageable pageable)**은 반환값이 Page<T>이므로, 위의 예제에서 카운트 쿼리가 자동으로 생성되어 실행된 것이다.

**Slice** 구현체의 경우 전후의 Slice 객체가 존재하는지 여부에 대한 정보를 가지고 있다. count 쿼리가 별도로 실행되지 않기 때문에 성능상 이점을 얻을 수 있다.

**List** 의 경우 가장 기본적인 반환값으로, count 쿼리 없이 단순 결과만 반환한다.

```
Page<Member> findPageBy(Pageable pageable);
Slice<Member> findSliceBy(Pageable pageable);
List<Member> findListBy(Pageable pageable);
```

```
@DisplayName("Page와 Slice, List 쿼리의 차이를 확인한다.")
@Test
void comparePageAndSliceAndList() {
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));
    memberRepository.findPageBy(pageRequest);
    memberRepository.findSliceBy(pageRequest);
    memberRepository.findListBy(pageRequest);
}
```

```
-- Page<T>의 경우
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 
    order by m1_0.name desc limit ?, ?
Hibernate: 
    select count(m1_0.id) 
    from member m1_0
        
-- Slice<T>의 경우
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 
    order by m1_0.name desc limit ?, ?
        
-- List<T>의 경우
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 
    order by m1_0.name desc limit ?, ?
```

실행된 쿼리를 확인해보면 다음과 같은 의문점이 든다.

**Slice의 경우 어떻게 다음 Slice 존재 유무를 판단하나?**

count 쿼리를 실행하지 않아서 전체 개수를 알 수 없는데 isFirst() , isLast() , hasNext() , hasPrevious() 와 같이

전체 페이지를 알아야만 실행할 수 있는 메서드를 제공하고 있다.

원리는 단순하다. 반환값이 Slice인 레파지토리 메소드에 페이지 사이즈를 10으로 설정한 Pageable을 전달하면, Spring Data JPA는 전달된 페이지 사이즈에 1을 더한 11만큼으로 쿼리를 실행한다.

위 코드 중 Slice<T>의 경우, Placeholder에 어떤 값이 들어가는지 확인해보면 다음과 같다.

```
Hibernate: 
    select m1_0.id, m1_0.name 
    from member m1_0 
    order by m1_0.name desc limit 0, 11
```
