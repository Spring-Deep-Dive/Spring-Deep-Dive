# Spring Package Structure

스프링에서 코드 구조에 대한 명확한 기준은 존재하지 않는다. 하지만 여러 개발자들에 의해 자리 잡은 모범적인 사례들이 존재하며, module, layer, feature 등을 기준으로 프로젝트를 관리할 수 있다.


## Structure by Feature

모든 클래스는 feature에 따라서 패키지별로 구분된다.

```
com
 +- project
        +- sample
            +- MainApplication.java
            |
            +- Member
            |   +- Member.java
            |   +- MemberController.java
            |   +- MemberService.java
            |   +- MemberRepository.java
            |
            +- Post
            |   +- Post.java
            |   +- PostController.java
            |   +- PostService.java
            |   +- PostRepository.java
                ...
```

- 특정 feature에 대한 수정 및 삭제가 용이하다.
- 특정 클래스를 찾기가 수월하다.
- 테스트 및 리팩토링이 쉽다.


## Structure by Layer

모든 클래스는 feature에 따라서 패키지별로 구분된다.

```
com
 +- project
        +- sample
            +- MainApplication.java
            |
            +- domain
            |   +- Member.java
            |   +- Post.java
            |   +- Comment.java
            |
            +- controllers
            |   +- MemberController.java
            |   +- PostController.java
            |   +- CommentController.java
            |
            +- services
            |   +- MemberService.java
            |   +- PostService.java
            |   +- CommentService.java
            |
            +- repositories
            |   +- MemberRepository.java
            |   +- PostRepository.java
            |   +- CommentRepository.java
            
```

- 프로젝트 구조가 상대적으로 직관적이다.
- 특정 feature에 대한 리팩토링이 힘들다
- 협업 시 merge conflict가 발생 빈도가 높은편이다.

