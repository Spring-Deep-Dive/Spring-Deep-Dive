
# IoC 제어의 역전

일반적인 프로그램의 흐름

1. main() 메소드와 같은 프로그램 시작 시점에 사용할 오브젝트 결정
2. 결정한 오브젝트 생성
3. 생성된 오브젝트의 메소드 호출


```JAVA
public class UserDao {
    public void add(User user) {
        ...
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/db", "sa", "pw");
        PreparedStatement ps = c.prepareStatement(
            "insert into users(id, name, password) values(?,?,?)"
        );
        ...
        ps.executeUpdate();
        ...
    }

    public 
    ...
}

public static void main(String[] args) {
    UserDao dao = new UserDao();
    User user = new User();
    dao.add(user);
    ...
}
```

각 오브젝트는 자신이 사용할 오브젝트를 해당 시점에 생성하고 각 메소드를 사용한다. 즉, 오브젝트가 자신이 사용할 클래스를 결정하고 언제 사용할지 결정하는 방식으로, 사용하는 쪽에서 제어하는 구조이다.

`제어의 역전(IoC)`는 이러한 제어 흐름의 개념을 거꾸로 뒤집은 것을 말한다. 제어의 역전에서는 오브젝트가 자신이 사용할 오브젝트를 스스로 선택하지 않는다. 모든 오브젝트는 위임받은 제어 권한을 갖는 특별한 오브젝트에 의해 결정되고 만들어진다.

서블릿에서 개발자가 직접 서블릿의 실행을 제어할 수 없다. 서블릿에 대한 제어 권한을 지닌 컨테이너가 적절한 시점에 서블릿 클래스의 오브젝트를 생성하고 내부 메소드를 호출하게 된다. 

이러한 제어의 역전의 개념은 프레임워크와 라이브러리의 차이점에서 발견할 수 있다. 라이브러리를 사용하는 어플리케이션 코드에서는 필요한 시점에 라이브러리의 코드를 호출해서 기능을 사용한다. 반면에 프레임워크에서는 기본적인 프레임워크의 동작 흐름에 개발자의 코드가 호출되어 사용되는 형식이다.

```JAVA
public class DaoFactory {
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    public messageDao messageDao() {
        return new MessageDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}

public interface ConnectionMaker {
    public Connection makeConnection();
}

public class DConnectionMaker implements ConnectionMaker {
    ...
    public Connection makeConnection() {
        return DriverManager.getConnection("jdbc:mysql://localhost/db", "sa", "pw");
    }
}

public static void main(String[] args) {
    UserDao dao = new DaoFactory().userDao();
    ...
}

```

이전에는 UserDao에서 ConnectionMaker의 구현체를 결정하도록 되어있었다. 이후에는 DaoFactory에서 Conenction을 결정하도록 변경되었다.

