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
`Message Converter`는 request의 본문에서 메시지를 읽어들이거나, response 본문에 메시지를 작성할 때 사용하는 컨버터를 말한다.<br>
Controller의 @ResponseBody를 통해서 HttpMessageConverter를 호출하고, return 타입에 따라서 JsonConverter 또는 StringConverter를 통해 response를 리턴하게 된다.

### HttpMessageConverter 인터페이스

```Java

/**
    Strategy interface for converting from and to HTTP requests and responses.
*/
public interface HttpMessageConverter<T> {

    /**
        Indicates whether the given class can be read/written by this convertier.
    */
    boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
    boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);

    List<MediaType> getSupportedMediaTypes();

    /**
        Return the list of media types supported by this converter.
    */
    default List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
		return (canRead(clazz, null) || canWrite(clazz, null) ?
				getSupportedMediaTypes() : Collections.emptyList());
	}

    /**
        Read an object of the given type from the given input message, and returns it.
    */
    T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;

    /**
        Write a given object to the given output message.
    */
    void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException;
}

```

스프링의 MessageConverter는 위의 인터페이스를 기반으로 구현된다.

### Type of Message Converter

이렇게 사용될 수 있는 메세지 컨버터는 AnnotationMethodHandlerAdapter를 통해 등록할 수 있고, 4가지의 기본 컨버터가 제공된다.

- ByteArrayHttpMessageConverter
- StringHttpMessageConverter
- FormHttpMessageConverter
- SourceHttpMessageConverter
- MappingJacksonHttpMessageConverter

### 동작 예시


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


`그렇다면 메시지 컨버터는 전체 동작과정 중 어디에 위치하나?`

<img src="/assets/images/MVC/message_converter.png">

ArugmentResolver에 request의 파라미터가 @RequestBody 또는 HttpEntity인 경우, HTTP 메시지 컨버터를 사용해 'read'작업을 수행한다. response의 경우, @ResponseBody 또는 HttpEntity를 처리하는 ReturnValueHandler에서 메시지 컨버터를 호출해 응답 결과에 대한 'write'작업을 수행한다.






## View Resolver
Controller(Handler)에서 View의 논리적인 이름을 리턴하고, DispatcherServlet은 View 이름을 View Resolver를 통해서 View 객체를 찾아서 생성하는 작업을 한다.

ViewResolver는 ViewResolver 인터페이스를 기반으로 하며, 다양한 종류가 존재한다.

```Java
/**
    Interface to be implemented by objects that can resolve views by name. View state doesn't change during the running of the application, so implementations are free to cache views. Implementations are encouraged to support internationalization, i.e. localized view resolution.
*/
public interface ViewResolver {
    /**
        Resolve the given view by name.
    */
    View resolveViewName(String viewName, Locale locale) throw Exception;
}
```


### InternalResourceViewResolver
```Java
/**
    Convenient subclass of UrlBasedViewResolver that supports InternalResourceView (i.e. Servlets and JSPs) and subclasses such as JstlView.
    The view class for all views generated by this resolver can be specified via setViewClass. See UrlBasedViewResolver's javadoc for details. The default is InternalResourceView, or JstlView if the JSTL API is present.
    BTW, it's good practice to put JSP files that just serve as views under WEB-INF, to hide them from direct access (e.g. via a manually entered URL). Only controllers will be able to access them then.
*/
public class InternalResourceViewResolver extends UrlBasedViewResolver {
    ...
    public InternalResourceViewResolver() {
        Class<?> viewClass = requiredViewClass();
        if (InternalResourceView.class == viewClass && jstlPresent) {
            viewClass = JstlView.class;
        }
        setViewClass(viewClass);
    }

    public InternalResourceViewResolver(String prefix, String suffix) {
		this();
		setPrefix(prefix);
		setSuffix(suffix);
	}

    protected Class<?> requiredViewClass() {
		return InternalResourceView.class;
	}

	protected AbstractUrlBasedView instantiateView() {
		return (getViewClass() == InternalResourceView.class ? new InternalResourceView() :
				(getViewClass() == JstlView.class ? new JstlView() : super.instantiateView()));
	}

}
```
주로 JSP를 사용할 때 사용되는 뷰 리졸버로, View를 생성할 필요없이 논리적인 이름만을 리턴한다.
이때, prefix/suffix 프로퍼티로 지정된 내용을 기반으로 url 경로 및 파일포맷을 생략할 수 있다.

```Java
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/view" />
    <property name="suffix" value=".jsp" />
</bean>
```
JSTL 라이브러리가 존재하면 JstlView를 사용하고, 존재하지 않으면 InternalResourceView를 사용한다.

### 기타 다른 ViewResolver

- VelocityViewResolver, FreeMarkerViewResolver
`Velocity`, `Freemarker`: template engine from Apache Software Foundation that can work with normal text files, SQL, XML, Java code and many other types.

- ResourceBundleViewResolver
외부에서 사용될 뷰를 결정하게 되는 경우, `views.properties`파일에 논리적 이름과 뷰 정보를 정의하여 컨트롤러마다 사용할 뷰를 달리 설정할 수 있다.

```Java
hello.(class)=org.springframework.web.servlet.view.JstlView
hello.url=/WEB-INF/view/hello.jsp

bye.(class)=org.springframework.web.servlet.view.velocity.VelocityView
bye.url=bye.vm
```

- XmlViewResolver
ResourceBundleViewResolver와 용도는 동일하고, views.properties 대신 /WEB-INF/views.xml을 사용한다.
추가로 이 파일은 서블릿 컨텍스트를 부모로 가지므로 DI가 가능하다는 장점이 있다.

- BeanNameViewResolver
뷰 이름과 동일한 이름을 가진 빈을 찾아서 뷰로 이용하게 해준다.