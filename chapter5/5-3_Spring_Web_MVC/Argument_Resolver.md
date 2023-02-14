# Argument Resolver

Argumennt Resolver는 수신한 요청(request)에 대한 데이터를 컨트롤러의 파라미터에 지정된 형식에 따라서 넘겨받을 수 있도록 하는 역할을 담당한다.

이러한 Argument Resolver는 HandlerMethodArgumentResolver 인터페이스를 기반으로 한다.

```Java
/*
  Strategy interface for resolving method parameters into argument values in the context of a given request
 */
 public interface HandlerMethodArgumentResolver {
    /*
        Wether the given {@linkplain MethodParameter method parameter} is supported by this resolver
        @param parameter the method parameter to check
        @ return {@code true} if this resolver supports the supplied parameter;
        {@code false} otherwise
    */
    boolean supportsParameter(MethodParameter parameter)

    /*
        Resolves a method parameter into an argument value from a given request. A {@link WebDataBinderFactory} provides a way to create a {@link WebDataBinder} instance when needed for data binding and type conversion purposes.
        
        Params
        @param parameter - the method parameter to resolve. This parameter must have previously been passed to {@link #supportsParameter} which must have returned {@code true}
        @param mavContainer - theModelAndViewContainer for the current request
        @param webRequest - the current request
        @param binderFactory - a factory for creating WebDataBinder instance

        Returns
        the resolved argument value, or null if not resolvable        
    */
    @Nullable
    Object resolveArgument(MethodParameter parameter, @Nullalbe ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throw Exception;
 }
```

ArguemntResolver는 컨트롤러를 통해 들어오는 파라미터를 가공하거나 추가 및 수정해야하는 경우에 사용될 수 있다.

## Argument Resolving Process
1. Client request
2. Dispatcher Servlet handle request
3. Handler mapping for client request by RequestMappingHandlerAdapter
4. Process Interceptor
5. Process Argument Resolver <- Invoke point for ArgumentResolver
6. Process EMssage Converter
7. Invoke controller method

### 구현 메소드 - supportsParameter()
HandlerMethodArgumentResolver 인터페이스에서 정의된 `supportsParameter()`는 주어진 파라미터가 Resolver에 의해 처리될 수 있는 Type인지 체크하는 역할을 수행한다. 이에 대한 리턴값이 true인 경우엔 resolveArgument()를 실행하게 된다.

스프링에서 제공하는 디폴트 argument resolver는 아래에서 확인할 수 있다.
https://github.com/spring-projects/spring-framework/blob/v5.0.0.RELEASE/spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.java



### 구현 메소드 - resolveArguemnt()
resolveArgument()는 실제의 파라미터와 바인딩하여 리턴할 객체를 생성하는 메소드이다.
NativeWebRequest 객체에 접근해서 클라이언트로부터 전달받은 요청의 파라미터를 컨트롤러에게 전달하기 전에 필요한 작업을 수행한다.

## Custom ArgumentResolver 등록
필요한 ArgumentResolver를 등록하기 위해서 해당 클래스를 생성하고, servlet-context.xml 또는 Configuration을 통해 Resolver를 등록한다.

```Java
@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomArgumentResolver customArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customArgumentResolver);
    }
}
```

## Meesage Converter




## View Resolver
