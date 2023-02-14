**Spring MVC**

외부에서 요청이 오면 DispatcherServlet이 doService를 실행하고, 그 안에서 doDispatch를 실행한다.

doDispatch에선 아래와 같이 HandlerAdapter를 가져와서 handle 메소드를 실행한다.

```
public class DispatcherServlet extends FrameworkServlet {

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	...
    	doDispatch(request, response);
    	...
    }
    
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ...
        mappedHandler = this.getHandler(processedRequest);
        ...
        HandlerAdapter ha = this.getHandlerAdapter(mappedHandler.getHandler());
        ...
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
        ...
    }
}
```

```
public interface HandlerAdapter {

    @Nullable
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}
```

---

**HandlerMapping**

DispatcherServlet에 Client로부터 Http Request가 들어 오면 HandlerMapping은 요청처리를 담당할 Controller를 mapping한다. Spring MVC는 interface인 HandlerMapping의 구현 클래스도 가지고 있는데, 용도에 따라 여러 개의 HandlerMapping을 사용하는 것도 가능하다. 빈 정의 파일에 HandlerMapping에 대한 정의가 없다면 Spring MVC는 기본HandlerMapping(**RequestMappingHandlerMapping**)을 사용한다.

Spring MVC(3.1이후버전) 제공하는 주요 HandlerMapping 구현 클래스는 아래와 같다.

-   BeanNameUrlHandlerMapping
-   **RequestMappingHandlerMapping**(DefaultAnnotationHandlerMapping이 deprecated되면서 대체됨)
-   ControllerClassNameHandlerMapping
-   SimpleUrlHandlerMapping

**HandlerMapping 초기화**

```
public class DispatcherServlet extends FrameworkServlet {

    private void initHandlerMappings(ApplicationContext context) {
            this.handlerMappings = null;
            if (this.detectAllHandlerMappings) {
                Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
                if (!matchingBeans.isEmpty()) {
                    this.handlerMappings = new ArrayList(matchingBeans.values());
                    AnnotationAwareOrderComparator.sort(this.handlerMappings);
                }
            } else {
                try {
                    HandlerMapping hm = (HandlerMapping)context.getBean("handlerMapping", HandlerMapping.class);
                    this.handlerMappings = Collections.singletonList(hm);
                } catch (NoSuchBeanDefinitionException var4) {
                }
            }

            if (this.handlerMappings == null) {
                this.handlerMappings = this.getDefaultStrategies(context, HandlerMapping.class);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No HandlerMappings declared for servlet '" + this.getServletName() + "': using default strategies from DispatcherServlet.properties");
                }
            }
            ...
       }
 }
```

DispatcherServlet 내의 initHanlderMappings 메소드에서 핸들러를 초기화 한다.

1.  detetectAllHandlerMappings가 true일 경우(기본값은 true) BeanFactoryUtils.beansOfTypeIncludingAncestors()를 통해 등록된 모든 HandlerMapping을 가져온다.
2.  false일 경우 handlerMapping이라는 이름을 가진 bean을 가져온다.
3.  그래도 handlerMappings가 null일 경우 getDefaultStrategies()를 통해 default로 초기화 한다.

**HandlerMapping이 handler를 가져오는 과정**

```
public class DispatcherServlet extends FrameworkServlet {
    
    @Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    	if (this.handlerMappings != null) {
            Iterator var2 = this.handlerMappings.iterator();

            while(var2.hasNext()) {
                HandlerMapping mapping = (HandlerMapping)var2.next();
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }

        return null;
    }
}
```

위에서 살펴보았던 doDispatch에서 getHandler가 실행된다.

```
public interface HandlerMapping {

    @Nullable
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
```

HandlerMapping에 getHanlder가 정의돼있고 이 메소드는 AbstractHandlerMapping에 구현돼있다.

```
public abstract class AbstractHandlerMapping extends WebApplicationObjectSupport implements HandlerMapping, Ordered, BeanNameAware {

    @Nullable
    public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = this.getHandlerInternal(request);
        ...
        HandlerExecutionChain executionChain = this.getHandlerExecutionChain(handler, request);
        ...  
    return executionChain;
    }

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;
}
```

getHandlerInternal()를 통해 hanlder를 찾아오고 **HandlerExecutionChain**을 리턴한다. 이후 DispatcherServlet이 HandlerExecutionChain 객체로부터 HandlerAdapter 객체를 가져와서 해당 메소드를 실행하게 된다.

(**HandlerExecutionChain**은 실제로 호출된 핸들러에 대한 참조를 가지고 있다. 즉, 무엇이 실행되어야 될지 알고 있는 객체라고 말할 수 있으며, 핸들러 실행 전과 실행 후에 수행될 HandlerInterceptor도 참조하고 있다. HandlerExecutionChian이 발견되지 않았으면 404 전달, 발견되었으면 HandlerAdapter를 결정한다.)

getHandlerInternal은 자식 클래스인 **AbstractHandlerMethodMapping**에 구현되어 있다.

```
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {

    @Nullable
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        String lookupPath = this.initLookupPath(request);
        this.mappingRegistry.acquireReadLock();

        HandlerMethod var4;
        try {
            HandlerMethod handlerMethod = this.lookupHandlerMethod(lookupPath, request);
            var4 = handlerMethod != null ? handlerMethod.createWithResolvedBean() : null;
        } finally {
            this.mappingRegistry.releaseReadLock();
        }

        return var4;
    }
}
```

lookupPath는 현재 servlet mapping 안에서의 검색경로인데, request 요청을 분석해서 얻을 수 있다. 이 lookupPath를 바탕으로 lookupHandlerMethod를 통해 적절한 handlerMethod를 가져온 후 리턴한다.

---

**HandlerAdapter**

디스패처 서블릿은 컨트롤러로 요청을 직접 위임하는 것이 아니라 HandlerAdapter를 통해 컨트롤러로 위임한다. @Controller에 @RequestMapping 관련 어노테이션으로 구현된 컨트롤러가 요청을 처리할 대상이라면 **RequestMappingHandlerMapping**이 찾아지고, 이를 처리할 어댑터가 **RequestMappingHandlerAdapter**이다.

```
public abstract class AbstractHandlerMethodAdapter extends WebContentGenerator implements HandlerAdapter, Ordered {

    @Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.handleInternal(request, response, (HandlerMethod)handler);
    }
    
    @Nullable
    protected abstract ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;
}
```

이를 상속한 RequestMappingHandlerAdapter를 참고해보자.

```
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware, InitializingBean {
	
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ...
        ModelAndView mav;
        ...
        mav = this.invokeHandlerMethod(request, response, handlerMethod);
        ...
        return mav;
    }
}
```

RequestMappingHandlerAdapter의 handleInternal 메소드 안에서 **invokeHandlerMethod**에 의해 Controller로 요청을 보내게 된다. 

```
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter implements BeanFactoryAware, InitializingBean {
    
    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        WebDataBinderFactory binderFactory = this.getDataBinderFactory(handlerMethod);
        ModelFactory modelFactory = this.getModelFactory(handlerMethod, binderFactory);
        ServletInvocableHandlerMethod invocableMethod = this.createInvocableHandlerMethod(handlerMethod);
        if (this.argumentResolvers != null) {
            invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }

        if (this.returnValueHandlers != null) {
            invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }

        invocableMethod.setDataBinderFactory(binderFactory);
        invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
        modelFactory.initModel(webRequest, mavContainer, invocableMethod);
        mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
        asyncWebRequest.setTimeout(this.asyncRequestTimeout);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.setTaskExecutor(this.taskExecutor);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        asyncManager.registerCallableInterceptors(this.callableInterceptors);
        asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);
        if (asyncManager.hasConcurrentResult()) {
            Object result = asyncManager.getConcurrentResult();
            mavContainer = (ModelAndViewContainer)asyncManager.getConcurrentResultContext()[0];
            asyncManager.clearConcurrentResult();
            LogFormatUtils.traceDebug(this.logger, (traceOn) -> {
                String formatted = LogFormatUtils.formatValue(result, !traceOn);
                return "Resume with async result [" + formatted + "]";
            });
            invocableMethod = invocableMethod.wrapConcurrentResult(result);
        }

        invocableMethod.invokeAndHandle(webRequest, mavContainer, new Object[0]);
        return asyncManager.isConcurrentHandlingStarted() ? null : this.getModelAndView(mavContainer, modelFactory, webRequest);
    }
}
```

invokeHandlerMethod 메서드 내부에는 HandlerMethodArgumentResovlers를 통해 HttpServlet, Model, @RequestParam, @ModelAttribute, @RequestBody, HttpEntity와 같은 부분들을 유연하게 처리하는 로직이 있으며, HandlerMethodReturnValueHandler를 통해 @ResponseBody, HttpEntity를 처리하는 로직이 존재하고 기타 로직들을 수행하여 사용자의 요청을 Controller로 전달한다.

**참고**

[https://www.egovframe.go.kr/wiki/doku.php?id=egovframework:rte2:ptl:handlermapping](https://www.egovframe.go.kr/wiki/doku.php?id=egovframework:rte2:ptl:handlermapping)

[https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/handler/AbstractHandlerMapping.html](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/handler/AbstractHandlerMapping.html)

[https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.html](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.html)
