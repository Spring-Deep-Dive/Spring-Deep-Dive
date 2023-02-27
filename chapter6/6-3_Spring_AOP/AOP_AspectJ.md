# AOP & AspectJ

AOP와 AspectJ 둘 다 Aspect-oriented programming(AOP)를 위한 프레임워크를 말한다.

- Language / Syntax
Spring AOP는 XML이나 어노테이션을 통해 설정을 할 수 있으며, 이를 통해 apect를 정의한다.
반면, AspectJ는 자체 syntax를 제공한다.
따라서 AspectJ를 통해 더욱 정밀하고 다양한 기능을 응용할 수 있지만, Spring AOP만을 사용하는것에 비해 학습을 요구한다.

- Runtime weaving
스프링 AOP는 dynamic proxy를 사용하여 runtime weaving 만을 지원한다.
AspectJ는 compile time, load time weaving을 지원하며, 컴파일/런타임에 동적으로 aspect를 코드에 삽입시킬 수 있다.

- Performance
AspectJ는 Proxy를 기반으로 하는 weaving 대신 바이트코드 weaving을 하기때문에 Spring AOP보다 빠르다. 

- Spring AOP는 Spring 프레임워크의 일부이며, AspectJ는 Spring과 함께 사용하기 위해 추가 구성 및 설정이 필요하다.


- Scope
Spring AOP는 스프링에 의해 빈으로 관리되는 대상에 대해서만 weaving을 적용한다. 반면, AspectJ는 Spring에서 지원하지 않는 써드파티 라이브러리에 대한 weaving을 수행할 수 있다. 