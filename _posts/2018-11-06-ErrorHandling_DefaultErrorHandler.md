---
layout: post
title: Error Handling - DefaultErrorHandler
tags: [Camel, Error Handling, DefaultErrorHandler, Redelivery]
---

### Error Handling - DefaultErrorHandler
#### 1. Exception을 알려주는 시점은 언제인가?

![exception_caller](/images/camel-error-handling/exception_caller.png)

Route 그림을 살펴보면 consumer, processor와 같은 nodes 사이에 channel이 존재한다.
만약 exception이 발생한다면, exception이 발생한 곳에서 전 단계로 돌아가며 가장 가까이에 있는 channel에 exception을 알려준다.
- If, 1번째 processor에서 error 발생?!
1번째 processor 전 단계에서 가장 가까운 channel은 consumer와 processor 사이에 위치한 channel이다. 따라서 이 channel에서 exception return
- If, 2번째 processor에서 error 발생?!
2번째 processor 전 단계에서 가장 가까운 channel은 1번째 processor와 2번째 processor 사이에 위치한 channel이다. 따라서 이 channel에서 exception return

#### 2. DefaultErrorHandler는 무엇인가?
Camel 2.0의 default error handler
* 특징
custom error handling X, 재전송 X, dead letter queue 존재 X
(dead letter queue는 ```Dead letter channel(Camel 1.x default error handler)```에만 존재한다)
* default 설정
routing 과정에서 exception이 발생한다면, 재전송이나 error handling 없이 caller에게 exception을 알려준 후 routing 바로 종료한다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" errorHandlerRef="myDefault" xmlns="http://camel.apache.org/schema/spring"> 	
 	 	<errorHandler id="myDefault" type="DefaultErrorHandler">
 		</errorHandler>

		<route>
			<from uri="file:d:/camel/src?delay=2000" />
			<to uri="http://localhost:10020/esbService" />
		</route>
	</camelContext>
</beans>
```

* ```“myDefault”```라는 이름의 DefaultErrorHandler를 먼저 정의한다.
* <camelContext>에서 errorHandlerRef=```”myDefault”``` 설정한다. 이 뜻은, 모든 routes에 사용할 수 있는 context-scope 범위로 설정한다는 것이다.
* from인 ```file:d:/camel/src?delay=2000```에서 end-point인 http 통신으로 전송할 때 exception이 발생하게 되면 ```"myDefault"``` error handler가 처리하게 된다.
* 재전송이나 error handling에 대한 어떤 option도 존재하지 않기 때문에 error 발생 후 Camel에서 정의된 대로 exception을 알려주고, 재전송은 하지 않는다.
	
#### 3. Redelivery (재전송)

Option | Type | Default | Description
---- | ---- | ---- | ----
MaximumRedeliveries | int | 0 | 재전송 횟수 (-1 : 성공할 때까지 무한 재전송)
RedeliveryDelay | long | 1000 | 재전송 시도 사이의 delay 시간 (고정값)
MaximumRedeliveryDelay | long | 60000 | 상한 redelivery 시간
AsyncDelayRedelivery | boolean | false | Camel의 asynchronous delayed redelivery 사용 여부
BackoffMultiplier | double | 2.0 | Starting delay 시간을 제외한 재전송 delay 시간
CollisionAvoidanceFactor | double | 0.15 | Random delay offset
DelayPattern | String | - | Group 별로 고정된 delay 시간
RetryAttemptedLogLevel | LoggingLevel | DEBUG | 재전송 시도가 실행되었을 때 log level
RetriesExhaustedLogLevel | LoggingLevel | ERROR | 재전송 시도가 실패되었을 때 log level
LogStackTrace | boolean | true | 모든 재전송 시도가 실패되었을 때의 stackTrace logged 여부
LogRetryStackTrace | boolean | false | Delivery가 실패했을 때의 stackTrace logged 여부
LogRetryAttempted | boolean | true | 재전송 시도 logged 여부
LogExhausted | boolean | true | 모든 재전송이 실패되었을 때의 logged 여부
LogHandled | boolean | false | Handled exception의 logged 여부
※ DefaultErrorHandler를 포함한 모든 error handler의 redelivey option으로 사용 가능


```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" errorHandlerRef="myDefault" xmlns="http://camel.apache.org/schema/spring"> 	
 	 	<errorHandler id="myDefault" type="DefaultErrorHandler">
      <redeliveryPolicy maximumRedeliveries="5" retryAttemptedLogLevel="ERROR" />
 		</errorHandler>

		<route>
			<from uri="file:d:/camel/src?delay=2000" />
			<to uri="http://localhost:10020/esbService" />
		</route>
	</camelContext>
</beans>
```

* DefaultErrorHandler를 선언하고 ```“myDefault”```라는 이름으로 정의한다.
* 재전송에 관하여 최대 재전송 횟수는 5번, 재전송 시도에 대한 log level은 ERROR 단계로 설정한다.
* \<route>를 수행하는 도중 exception이 발생한다면 ```myDefault``` error handler에서 처리한다. 재전송을 5번까지 시도한 다음, 만약 5번 모두 실패한다면 exception을 알린 후 routing을 종료한다.
