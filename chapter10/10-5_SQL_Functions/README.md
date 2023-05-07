# QueryDSL을 이용한 SQL함수

---

## 개요

- 특정 DBMS의 내장 함수를 사용하기 위해 사용방법을 소개

### Dialect 설정

```java
package com.adop.vonda.bus.vondabusapi.Common.Configuration;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySQLCustomDialect extends MySQL8Dialect {

    public MySQLCustomDialect() {
        super();
        this.registerFunction("uuid", new StandardSQLFunction("uuid", StandardBasicTypes.STRING));
    }
}
```

- uuid()를 사용하기 위한 설정

```java
spring:
  jpa:
    database-platform:com.adop.vonda.bus.vondabusapi.Common.Configuration.MySQLCustomDialect
```

- 설정파일의 패키지 경로를 적어준다.

### 사용 방법

```java
query
.select(member.name,Expressions.stringTemplate("uuid()"))
.from(member)
.fetch();
```

- 파라미터가 있을 경우

```java
Expressions.stringTemplate("CONCAT_WS('/', {0}, {1}, {2}, {3})", param1, param2, param3, param4);
```

- 실제로 MySQL 내장함수나 특정 벤더에 종속적인 내장 함수는 코드로 대부분 쉽게 구현이 가능한 것이라, 권장되는 사용방법이 아니긴 하나, 부득이하게 사용하여야 하는 경우 사용할 수 있다.