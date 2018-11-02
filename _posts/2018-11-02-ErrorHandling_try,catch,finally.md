---
layout: post
title: Error Handling - Try/Catch/Finally
tags: [Camel, Error Handling, <doTry>, <doCatch>, <doFinally>]
---

### Error Handling - Try/Catch/Finally

### 1. Error handler 종류
1. Try/Catch/Finally
2. DefaultErrorHandler
3. onException

### 2. Try/Catch/Finally
* Syntax

\<doTry>, \<doCatch>, \<doFinally>

→ 앞쪽에 'do' 접미사 붙여 사용하여 Java 문법과의 혼동, 충돌 방지
* Camel 2.0 이후부터만 사용할 수 있다. Camel 1.x 버전에서는 Camel 1.x의 default error handler인 dead letter channel을 사용해야 한다.
* Try/catch/finally를 사용하게 되면 기존 camel이 제공하는 error handler인 DefaultErrorHandler나 onException은 같이 사용할 수가 없다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring"> 	
		<route>
			<from uri="file:d:/camel/src?delay=2000" />
      <doTry>
			     <to uri="file://d:/camel/output" />
           <doCatch>
              <exception>java.lang.Exception</exception>
              <to uri="file://d:/camel/output/exception" />
           </doCatch>
           <doFinally>
              <to uri="file://d:/camel/output/finally" />
           </doFinally>
      </doTry>
		</route>
	</camelContext>
</beans>
```

* 일을 처리하는 도중 java.lang.Exception이 발생하게 되면 ```<to uri="file://d:/camel/output" />``` 대신 \<doCatch> uri 경로로 설정한 ```<to uri="file://d:/camel/output/exception" />```으로 이동하게 된다. 마지막으로 \<doFinally> 실행되어 ```<to uri="file://d:/camel/output/finally" />```로 전송된다.
* 주의점
  * <doTry> 구문 안에 <doCatch>, <doFinally>을 넣어야 한다.
  * <doCatch> 안에 한 개 이상의 exception을 정의할 수 있다.

```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring"> 	
  <route>
    <from uri="file:d:/camel/src?delay=2000" />
    <doTry>
         <to uri="file://d:/camel/output" />
         <doCatch>
            <!--1개 이상의 exception을 정의하여 여러 개의 exception을 탐지할 수 있도록 설정 가능 -->
            <exception>java.lang.Exception</exception>
            <exception>java.io.FileNotFoundException</exception>
            <to uri="file://d:/camel/output/exception" />
         </doCatch>
         <doFinally>
            <to uri="file://d:/camel/output/finally" />
         </doFinally>
    </doTry>
  </route>
</camelContext>
```

* \<doFinally>는 생략이 가능하다. 필요하지 않는다면 작성하지 않아도 된다.
