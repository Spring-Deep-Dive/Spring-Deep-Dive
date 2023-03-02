### Datasource

JDK1.4부터 생긴 표준 인터페이스(javax.sql.Datasource). 드라이버 벤더에 의해 구현된다.

Datasource는 Connection Pool로의 연결을 관리하고, 트랜잭션도 가능하게 만들어야 한다.

유의할 점은 DBCP가 자바 표준으로 지정되어 있는 게 없다는 것.

따라서 벤더에 따라 사용법이 상이할 수 있다.

---

### DBCP(DataBase Connection Pool)

DB로의 추가 요청이 필요할 때 연결을 재사용할 수 있도록 관리되는 DB 연결의 캐시이다.

DBCP가 DB 연결 정보를 메인 메모리에 저장/관리하고, 연결이 필요할 때마다 불필요한 작업(커넥션 생성/삭제) 없이 DBCP에서 가져다 재사용 하면 된다.

HikariCP를 보면 volatile 키워드를 사용하여 pool을 메인 메모리에서 관리한다는 걸 확인할 수 있다.

```
public class HikariDataSource extends HikariConfig implements DataSource, Closeable {
	...
	private volatile HikariPool pool;
	...
}
```

DBCP는 여러 종류가 있는데, 설명할 필요가 있을까 싶다.

JPA 생태계는 하이버네이트가 점령했고, DBCP 쪽에선 HikariCP가 그렇다. 

성능상 압도적으로 빠른 이점 때문에 특별한 일이 없다면 HikariCP 쓰게 된다. 벤치마크 참고.

Spring Boot 2.0부터는 HikariCP가 디폴트 JDBC 커넥션풀이다.

spring-boot-starter-data-jpa, spring-boot-starter-jdbc에 의존성이 포함되어 있다.

---

### JDBC(Java Database Connectivity)

자바에서 데이터베이스에 접속할 수 있도록 하는 벤더 독립적 자바 API이다.

장점

1\. 데이터베이스로부터 데이터의 XML 형식을 자동으로 생성  
2\. 쿼리와 저장된 프로시저를 지원  
3\. ODBC 드라이버가 설치된 데이터베이스 접근 가능

단점

1\. 쿼리 실행 이전과 이후에 많은 코드를 작성 (커넥션, 스테이트먼트, 연결 및 해제)  
2\. 데이터베이스 로직에 있는 코드를 관리하기 위한 예외 작성 필요  
3\. 여러 개의 데이터베이스로부터 코드 반복

---

### Spring JDBC

JDBC의 장점을 유지하면서 단점을 극복. 아래 일들을 대신 해준다.

-   Connection 열기와 닫기
-   Statement 준비와 닫기
-   Statement 실행
-   ResultSet Loop처리
-   Exception 처리와 반환
-   Transaction 처리

개발자는 다음 작업만 하면 된다.

-   datasource 설정
-   sql문 작성
-   결과 처리

Spring에서 지원하는 JdbcTemplate, SimpleJdbcInsert, SimpleJdbcCall 같은 클래스를 사용할 수 있다.

이 중 **JdbcTemplate**은 기본적이고 가장 인기있는 접근법이다.

다음과 같은 기본적인 CRUD가 가능하다.

```
// SELECT (id가 10에 해당하는 학생 이름) 
String SQL = "select name from Student where id = ?"; 
String name = jdbcTemplateObject.queryForObject(SQL, new Object[]{10}, String.class);

// INSERT (id가 11인 Zara 학생을 삽입)
String SQL = "insert into Student (name, age) values (?, ?)"; 
jdbcTemplateObject.update(SQL, new Object[]{"Zara", 11});

// UPDATE (id가 10인 학생의 이름을 Zara로 수정)
String SQL = "update Student set name = ? where id = ?"; 
jdbcTemplateObject.update("UPDATE S, new Object[]{"Zara", 10});

// DELETE (id가 10인 학생을 삭제)
String SQL = "delete from Student where id = ?"; 
jdbcTemplateObject.update(SQL, new Object[]{10});
```

JPA에서 식별자 생성 전략을 IDENTITY로 줬을 경우 눈물을 머금고 쓸 수 없었던 **Batch Insert**도 가능하다.

```
@Repository
@RequiredArgsConstructor
public class ItemJdbcRepositoryImpl implements ItemJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${batchSize}")
    private int batchSize;

    @Override
    public void saveAll(List<ItemJdbc> items) {
        int batchCount = 0;
        List<ItemJdbc> subItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            subItems.add(items.get(i));
            if ((i + 1) % batchSize == 0) {
                batchCount = batchInsert(batchSize, batchCount, subItems);
            }
        }
        if (!subItems.isEmpty()) {
            batchCount = batchInsert(batchSize, batchCount, subItems);
        }
        System.out.println("batchCount: " + batchCount);
    }

    private int batchInsert(int batchSize, int batchCount, List<ItemJdbc> subItems) {
        jdbcTemplate.batchUpdate("INSERT INTO ITEM_JDBC (`NAME`, `DESCRIPTION`) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, subItems.get(i).getName());
                        ps.setString(2, subItems.get(i).getDescription());
                    }
                    @Override
                    public int getBatchSize() {
                        return subItems.size();
                    }
                });
        subItems.clear();
        batchCount++;
        return batchCount;
    }
}
```

### 참고

[https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc)

[https://www.baeldung.com/spring-jdbc-jdbctemplate](https://www.baeldung.com/spring-jdbc-jdbctemplate)

[https://bibi6666667.tistory.com/300](https://bibi6666667.tistory.com/300)

[https://homoefficio.github.io/2020/01/25/Spring-Data%EC%97%90%EC%84%9C-Batch-Insert-%EC%B5%9C%EC%A0%81%ED%99%94/](https://homoefficio.github.io/2020/01/25/Spring-Data%EC%97%90%EC%84%9C-Batch-Insert-%EC%B5%9C%EC%A0%81%ED%99%94/)
