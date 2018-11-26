---
layout: post
title: Apache Camel - Thread,Concurrency
tags: [Camel, Thread, thread pool, <threadPool>, <threadPoolProfile>, Multicast EIP, Splitter EIP, wire-tap EIP]
---

### Apache Camel - Thread

#### 1. Concurrency
Concurrency는 곧 multitasking을 말한다. 동시에 여러 작업을 진행하는 것을 뜻한다.
Camel에서는 route 안에서 여러 messages들을 동시에 수행할 수 있도록 제공해준다.
* Concurrency 구현 방법
ⅰ. ParallelProcessing
Parallel processing 지원하는 EIP는 multicast, recipient, splitter, list, wire tap, error handler 등이 존재한다.
parallelProcessing=true로 활성화하여 사용한다.
ⅱ. Thread pool
Thread pool에는 2가지 종류가 있다. (cached thread pool/fixed thread pool)
* Cached thread pool
최대 thread 개수가 정해져있지 않다. 즉, 새로운 task가 들어왔는데 현재 thread pool에서 사용할 수 있는 thread가 존재하지 않는다? 그러면 새로운 thread를 만들어서 해당 task를 수행한다.
Parallel processing보다 속도가 빠르다는 장점이 있지만, thread pool의 최대 thread 개수가 정해져있지 않기 때문에 task가 많아질수록 thread 개수는 계속해서 생성하게 되고, 결국 과부하로 이어질 가능성이 있다.
* Fixed thread pool
Thread 개수가 정해져 있는 thread pool
제한된 threads를 가지고
ⅲ. SEDA component
// 이건 넣어야 되나 고민된다.....

<br/>
#### 2. Thread pools
Ⅰ. Thread pool이란?
![thread_pools](C:/Users/NaYoung/Documents/Markdown/Camel-ESB/thread_pools.png)
Task queue로부터 task 꺼내어 thread pool에 넣고 실행하는 구조
단일 thread가 아닌 multiple threads를 사용하여, component는 앞선 exchange를 기다리는 시간 없이 다른 processing을 계속 진행하여 효율성 ↑

Ⅱ. Option
Option | Default | Description
---- | ---- | ----
poolSize | 10 | thread pool 개수
maxPoolSize | 20 | 최대 thread pool 개수
keepAlivetime | 60 | 종료된 후 thread 유지 시간
maxQueueSize | 1000 | Pool exhausted 되기 전 task queue가 넣을 수 있는 task 개수
rejectedPolicy | CallerRuns | (option) CallerRuns/Abort/DiscardOlddest, Discard
* rejectedPolicy option
  - CallerRuns : Thread에 가장 최근에 들어온 task부터 실행
  부작용 : 가장 최근에 들어온 task 끝나기 전에는 다른 tasks가 들어오는 것을 막는다.
  - Abort : 현재 진행하던 task를 중지하고 exception 발생
  - DiscardOlddest : task queue에 존재하는 task에서 오래된 순서부터 버린다.
  - Discard : task queue에 존재하는 tasks를 모두 버린다.

Ⅲ. 어떻게 thread pool을 만들어서 사용하는가?
\<threadPool>, \<threadPoolProfile> 이용해서 custom thread pool을 생성한다.
* \<threadPool>
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <threadPool id="hello" threadName="hello" poolSize="5" maxPoolSize="20" maxQueueSize="100" />
		<route>
			<from uri="file:d:/camel/src?delay=2000" />
      <threads executorServiceRef="hello">
			     <to uri="http://localhost:10020/esbService" />
      </threads>
		</route>
	</camelContext>
</beans>
```
주의점 : ```threadName```은 꼭 명시해줘야 한다.
* \<threadPoolProfile>
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <threadPoolProfile id="hello" poolSize="5" maxPoolSize="20" maxQueueSize="100" />
		<route>
			<from uri="file:d:/camel/src?delay=2000" />
      <threads executorServiceRef="hello">
			     <to uri="http://localhost:10020/esbService" />
      </threads>
		</route>
	</camelContext>
</beans>
```
executorServiceRef 이용하여 어떤 thread pool을 사용할 것인지 설정
→ 만약 executorServiceRef에 설정한 thread가 없다면 fall-back 한 뒤, 그 이름으로 정의된 thread profile이 있는지 찾아본다. 있다면, 해당 profile을 기반으로 thread pool을 만든 뒤 사용
주의점 : \<threadPool>과 다르게, ```threadName``` option을 사용할 수 없다.

<br/>
#### 3. Using concurrency with EIPs
① Threads EIP
Thread pool 이용하여 concurrency 구현
  → \<threadPool>, \<threadPoolProfile>
Apache Camel의 ```file component```를 Threads EIP에서 가장 많이 사용한다.
② Multicast EIP
![thread_pools](C:/Users/NaYoung/Documents/Markdown/Camel-ESB/multicast.png)
Multicast EIP에서 concurrency를 사용하지 않는다면, camel route는 정의한 순서대로 수행하기 때문에 오랜 시간 소요
→ 하나의 message를 서로 다른 대상자에게 동시에 보냄으로써 효율성 ↑
```parallelProcessing=true``` 이용
```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
  <route>
    <from uri="file:d:/camel/src?move=done&amp;moveFailed=fail&amp;delay=2000" />
    <multicast parallelProcessing="true">
         <to uri="http://localhost:10020/esbService" />
         <to uri="file://d:/camel/output" />
         <to uri="direct:end" />
    </multicast>
  </route>
</camelContext>
```
③ Wire Tap EIP
![thread_pools](C:/Users/NaYoung/Documents/Markdown/Camel-ESB/wire-tap.png)
Processing 과정이 수행 중, 종료되기 전에 새로운 message를 생성하여 보내고 싶을 때 사용
새로 만든 message는 새롭게 생성한 thread를 이용하여 정해진 endpoint로 보냄과 동시에, 원래 thread는 그대로 진행
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <threadPool id="hello" threadName="hello" poolSize="5" maxPoolSize="20" maxQueueSize="100" />

		<route>
      <from uri="file:d:/camel/src?move=done&amp;moveFailed=fail&amp;delay=2000" />
      <to uri="mock:result" />
		</route>

    <route>
      <from uri="direct:tap" />
      <to uri="mock:tap" />
    </route>
	</camelContext>
</beans>
```

④ Splitter EIP
Message를 분할하여 각각 processing을 진행할 때 concurrency 필요
threadPool 이용하여 concurrency 구현
split option 중 executorServiceRef 통해 custom thread pool 정의, 사용
	→ 이 option을 사용하게 되면, parallel processing은 자동으로 들어가게 된다.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

	<bean id="jetty" class="org.apache.camel.component.jetty9.JettyHttpComponent9">
	</bean>

 	<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
    <threadPool id="hello" threadName="hello" poolSize="5" maxPoolSize="20" maxQueueSize="100" />

		<route>
      <from uri="file:d:/camel/src?move=done&amp;moveFailed=fail&amp;delay=2000" />
      <split executorServiceRef="hello">
        <tokenize token=";" />
        <to uri="mock:result" />
      </split>
		</route>
	</camelContext>
</beans>
```
