# Spring Data

Spring Data는 데이터 액세스 기술로, 데이터베이스의 타입 및 형태에 따라 다양한 서브 프로젝트들로 구성(umbrella)되어 있다.

## Main Modules

- Spring Data Commons
- Spring Data JDBC
- Spring Data JPA
- Spring Data LDAP - Lightweight Directory Access Protocol
- and so on



## Spring Data Commons
Spring Data Fmaily의 기반이 되는 인프라를 제공한다.
Java Class를 영속시키기 위한 metadata model과 repository 인터페이스를 포함하고 있다.

```JAVA
// Meta-information about the CRUD methods of a repository.
public interface CrudMethods {

	Optional<Method> getSaveMethod();

	boolean hasSaveMethod();

	Optional<Method> getFindAllMethod();

	boolean hasFindAllMethod();

	Optional<Method> getFindOneMethod();

	boolean hasFindOneMethod();

	Optional<Method> getDeleteMethod();

	boolean hasDelete();
}
```


```JAVA
// RepositoryMetadata from spring-data-common git repository
public interface RepositoryMetadata {

	default Class<?> getIdType() {
		return getIdTypeInformation().getType();
	}

	default Class<?> getDomainType() {
		return getDomainTypeInformation().getType();
	}

	TypeInformation<?> getIdTypeInformation();

	TypeInformation<?> getDomainTypeInformation();

	Class<?> getRepositoryInterface();

	TypeInformation<?> getReturnType(Method method);

	Class<?> getReturnedDomainClass(Method method);

	CrudMethods getCrudMethods();

	boolean isPagingRepository();

	Set<Class<?>> getAlternativeDomainTypes();

	boolean isReactiveRepository();

	Set<RepositoryFragment<?>> getFragments();
}

```


참조: https://github.com/spring-projects/spring-data-commons/blob/main/src/main/java/org/springframework/data/repository/core/RepositoryMetadata.java


<br>

---

<br>

## Spring Data JDBC

Java Database Connectivity(JDBC) 기반의 repository 생성을 지원하는 Spring Data 모듈을 말한다.


```JAVA
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public class User {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    // constructors, getters, and setters
}

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT * FROM users WHERE email = :email")
    User findByEmail(String email);
}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

@Configuration
public class DatabaseConfig extends AbstractJdbcConfiguration {
    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions();
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .build();
    }
}
```

`@Query`는 커스텀 쿼리를 정의하며,
DatabaseConfig의 `DataSource`빈을 통해 사용되는 데이터베이스와 스크립트를 통해 데이터베이스 스키마를 정의한다. JPA와 마찬가지로 application.yml 파일을 통해서 정의할 수 있다.

<br>

---

<br>

## Spring Data JPA

```JAVA
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    // constructors, getters, and setters
}

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EntityScan(basePackages = "com.example.entity")
public class JpaConfig {
    @Bean
    public DataSource dataSource() {
        // configure and return DataSource
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // configure and return LocalContainerEntityManagerFactoryBean
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
```


<br>
<br>
<br>

### Spring Data JDBC vs Spring Data JPA

- JDBC는 JDBC를 통해 데이터베이스와 상호작용하지만, JPA는 Java Persistnce API를 통해 상호작용한다.
- 자바 객체에 대한 매핑을 어노테이션을 통해 수행하며, 중간 매핑 레이어가 존재하지 않는다. 반면, JPA는 어노테이션 뿐 만 아니라 XML을 통해 가능하며 연관관계 등의 섬세한 설정이 가능하다.
- JDBC는 기본적인 CRUD와 같은 쿼리만을 제공하지만, JPA는 지연로딩과 캐싱, 트랜잭션 관리와 같은 추가 기능이 제공된다.
- JPA는 NoSQL을 포함한 더욱 광범위한 데이터베이스를 지원한다.


<br>
<br>
<br>

### What about pure JDBC?

```JAVA
import java.sql.*;

public class JdbcExample {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Create a new user
            createUser(conn, "John Doe", "john@example.com");

            // Find a user by email
            User user = findUserByEmail(conn, "john@example.com");
            System.out.println(user);

            // Update a user's name
            updateUser(conn, user.getId(), "Jane Doe");

            // Delete a user
            deleteUser(conn, user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createUser(Connection conn, String name, String email) throws SQLException {
        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        System.out.println("User created with ID " + rs.getLong(1));
                    }
                }
            }
        }
    }

    private static User findUserByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    return new User(id, name, email);
                }
            }
        }
        return null;
    }

    private static void updateUser(Connection conn, long id, String name) throws SQLException {
        String sql = "UPDATE users SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setLong(2, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User updated with ID " + id);
            }
        }
    }

    private static void deleteUser(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User deleted with ID " + id);
            }
        }
    }
}

class User {
    private long id;
    private String name;
    private String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

}
```

<br>
<br>
<br>

### What about MyBtis?
myBatis는 Spring Boot Starter에서 기본 제공하는 모듈이 아니기에 직접 의존성을 추가해줘야 한다.