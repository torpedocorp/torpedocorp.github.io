---
layout: post
title: Error Handling - onException
tags: [Camel, Error Handling, <onException>, <handled>, <continued>, <onWhen>]
---

### Error Handling - oneException

###onException
#### 1. onException이란?
모든 exception이 아닌, 특정 exception에 대하여 처리하고 싶을 때 정의
Syntax : \<onException>
주로 error handler(defaultErrorHandler, Dead Letter Channel)와 같이 결합하여 사용

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" errorHandlerRef="myDefault" xmlns="http://camel.apache.org/schema/spring">
    <errorHandler id="myDefault" type="DefaultErrorHandler" />

    <onException>
      <exception>java.io.IOException</exception>
    	<to uri="file://d:/camel/output/exception"/>
    </onException>

		<route>
			<from uri="file:d:/camel/src?delay=2000" />
			<to uri="http://localhost:10020/esbService" />
		</route>
	</camelContext>
</beans>
```
* routing 도중 java.io.IOException이 발생하게 되면, \<onException> 구문에서 처리한다.
* java.io.IOException을 제외한 다른 exception이 발생한다면, 그 exception은 \<onException>이 아닌 DefaultErrorHandler인 ```myDefault```에서 처리한다.

1. 특징
하나의 \<onException>에 여러 개의 exception을 정의할 수 있다.
```xml
<camelContext id="camel" errorHandlerRef="myDefault" xmlns="http://camel.apache.org/schema/spring"> 	
  <errorHandler id="myDefault" type="DefaultErrorHandler">
  </errorHandler>

  <onException>
    <exception>java.io.IOException</exception>
    <exception>java.sql.SQLException</exception>
    <to uri="file://d:/camel/output/exception" />
  </onException>

  <route>
    <from uri="file:d:/camel/src?delay=2000" />
    <to uri="http://localhost:10020/esbService" />
  </route>
</camelContext>
```

\<onException>은 다른 error handler보다 우선 순위가 높다.
ex) defaultErrorHandler와 java.lang.Exception을 감지하는 onException, 2개가 정의 되어 있는데 java.lang.Exception이 발생한다면 \<onException> 구문이 실행된다.

2. 실행 원리
발생한 exception을 \<onException> 구문과 비교할 때, exception의 상속 구조대로 밑→위로 올라가면서 하나씩 비교해 일치하는 exception이 있는지 비교한다.
발생한 exception에 대해 상속 구조에 포함된 onException이 2개 이상 존재한다면, 발생하는 exception과 정확하게 일치하는 것을 먼저 handling하고, 완전히 일치하는 것이 없다면, “gap detection” 이용하여 실행할 어떤 \<onException>을 실행할 것인지 판단한다.

※ Gap detection
발생한 exception과 route에 안에서 정의한 exception 사이의 gap을 계산하여 가장 낮은 gap을 차지하는 \<onException>을 선택한다.

```xml
<onException>
  <exception>java.io.IOException</exception>
  <to uri="file://d:/camel/output/exception/route_1" />
</oneException>

<onException>
  <exception>java.lang.Exception</exception>
  <to uri="file://d:/camel/output/exception/route_2" />
</oneException>
```

If) 위와 같은 \<onException> 구문을 정의했는데, route에서 processing 중 FileNotFoundException 발생했다면 어떤 \<onException> 구문이 실행되는가?
![gap_detection](/images/camel-error-handling/gap_detection.png)

①	 FileNotFoundException과 정확하게 일치하는 \<onException> 존재하지 않는다. 그렇다면 “gap detection”으로 실행된 구문을 결정한다.
②	 java.io.FileNotFoundException과 java.io.IOException과의 gap은 1, java.lang.Exception는 gap=2이다.
③	 java.io.IOException과의 gap이 더 작기 때문에 java.io.IOException 정의된 \<onException> 실행된다.


#### 2. Redelivery(redeliveryPolicy)
\<onException>에서는 재전송에 대한 default는 0으로 설정 (재전송 X)
재전송하고자 한다면 redeliveryPolicy를 통해 재전송 설정

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <onException>
      <exception>java.io.IOException</exception>
      <redeliveryPolicy maximumRedeliveries="5" retryAttemptedLogLevel="WARN"/>
    	<to uri="file://d:/camel/output/exception"/>
    </onException>

		<route>
			<from uri="file:d:/camel/src?delay=2000" />
			<to uri="http://localhost:10020/esbService" />
		</route>
	</camelContext>
</beans>
```

#### 3. \<handled>
(default) handled=false
“handled=true”로 설정하게 되면 Camel이 제공하는 것이 아닌, 원하는 custom response를 caller에게 보낼 수 있다.
![handled](/images/camel-error-handling/handled.png)

```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
  <onException>
    <exception>java.io.IOException</exception>
    <handled><constant>true</constant></handled>
    <!--<constant> 이용해서 묶어줘야만 error 없이 사용할 수 없다. -->
    <to uri="file://d:/camel/output/exception"/>
  </onException>

	<route>
		<from uri="file:d:/camel/src?delay=2000" />
		<to uri="http://localhost:10020/esbService" />
	</route>
</camelContext>
```

#### 4. \<continued>
“continued=true”를 사용하게 된다면 exception이 발생하더라도 무시하고 기존 routing 진행
\<onException>을 쓰지 않고 continue 기능을 사용하고 싶다면, try/catch/finally을 이용해야 한다.
주의점 : 하나의 \<onException>에 handled와 continued를 같이 사용할 수 없다.
![continued](/images/camel-error-handling/continued.png)

```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
  <onException>
    <exception>java.io.IOException</exception>
    <continued><constant>true</constant></continued>
    <to uri="file://d:/camel/output/exception"/>
  </onException>

	<route>
		<from uri="file:d:/camel/src?delay=2000" />
		<to uri="http://localhost:10020/esbService" />
	</route>
</camelContext>
```

#### 5. \<onWhen>
\<onWhen>과 \<onException>을 같이 사용한다면, exception 발생과 더불어 \<onWhen> 조건에 맞는 case에만 실행

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

  <camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <onException>
      <exception>java.io.IOException</exception>
      <onWhen>
        <simple>${header.size} == null</simple>
      </onWhen>
      <to uri="file://d:/camel/output/exception"/>
    </onException>

	   <route>
		   <from uri="file:d:/camel/src?delay=2000" />
		   <to uri="http://localhost:10020/esbService" />
	   </route>
  </camelContext>
</bean>
```

* java.io.IOException 발생했을 때, header에서의 size가 null일 때만 \<onException> 구문 실행한다. 만약, java.io.IOException이 발생하여도 header.size가 null이 아니라면 실행되지 않는다.
