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
이러한 작업이 필요한 예시 상황은?
- HttpSession에서 세션정보를 로드
- HttpServletRequest에서 요청 url 및 IP 정보, 토큰 등 로드

```Java
@RestController
public class SomeController {
    @GetMapping("/foo")
    public String getFoo(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION);
        // ... do something with auth info
    }

    @GetMapping("/boo")
    public String getBoo(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION);
        // ... do something with auth info
    }
}

```

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
public class ResultJwtArgumentResolver implements HandlerMethodArgumentResolver {
	@Autowired
	private AuthService authService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return ResultJwt.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
                // return type은 본인이 Binding을 원하는 객체 Class
                // supportsParameter에서 검증한 ResultJwt.class
		return authService.getResultJwt(webRequest.getHeader("Authorization"));
	}
}

```

참조: https://velog.io/@gillog/Spring-HandlerMethodArgumentResolver-PathVariable-RequestHeader-RequestParam



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
`Message Converter`는 HTTP request body와 response body를 메시지로 다루는 방식을 말한다.

```Java
@ResponseBody
@PostMapping("/test")
public String hello(@RequestBody String param){
    return "result";
}
```
위와 같은 controller method를 정의했을때, 스프링은 메시지 컨버터를 통해 HTTP 요청이나 응답을 메시지로 변환하게 된다.
@RequestBody 어노테이션의 타입에 따라 메세지 컨버터를 선택하고, HTTP 요청 본문을 통째로 메시지로 변환하여 파라미터에 바인딩한다.
메소드의 상단 @ResponseBody를 입력하여 리턴 타입에 맞는 메시지 컨버터를 선택하여 리턴 값을 메시지로 변환하여 리턴해준다.

### Type of Message Converter

이렇게 사용될 수 있는 메세지 컨버터는 AnnotationMethodHandlerAdapter를 통해 등록할 수 있고, 4가지의 기본 컨버터가 제공된다.

- ByteArrayHttpMessageConverter
    byte[] 타입 객체를 지원하며, 미디어타입은 모두 이것을 지원한다.
    요청을 byte 배열 형태로 받을 수 있고, 응답인 경우엔 Content-Type application/stream으로 설정되어 전달된다.

- StringHttpMessageConverter
    String 객체 타입을 말하며, 미디어타입은 모두 이것을 지원한다.
    요청에 대한 HTTP 본문을 문자열 형태로 받고, 응답의 경우엔 Content-Type text/plain으로 전달된다.

- FormHttpMessageConverter
    MultiValueMap<String, String> 객체 타입을 지원하며, 미디어타입은 application/x-www-form-urlencoded를 지원한다.

- SourceHttpMessageConverter
    XML문서를 Source 타입의 오브젝트로 변환할 때 사용된다.




## View Resolver
