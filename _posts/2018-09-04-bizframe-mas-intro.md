---
layout: post
title: Bizframe-mas 소개 
tags: [토피도, torpedo, bizframe, mas, camel, ESB, msa]
excerpt_separator: <!--more-->
---

bizframe-mas는 어플리케이션을 실행 관리하기 위한 어플리케이션 컨테이너입니다.  
<!--more-->
bizframe-mas를 이용하여 일반적인 JAVA 프로그램을 구동하여 관리할 수도 있으며
서버프로그램,  Tomcat, Jetty와 같은 웹어플리케이션 혹은 apache-camel과 같은 라우팅 엔진을 구동하여 ESB 서버로 사용할 수 있습니다.    



## 1. 특징  

  * JVM 기반하에서 application 구동 및 관리를 위한 서버
  * Application 초기화/종료 액션 가능
  * Service 시작/중지 액션 가능
  * Application 간 독립적인 클래스로더 사용으로 Application간 독립성 강화
  * 일반 JAVA 클래스를 로딩할 수 있으며 cdi와 jetty는 내장시킴
  * Application 간 메시지 교환을 위해서 route 구조 내장 시킴
  * 커맨드라인 명령 수행
  * 웹어플리케이션(Tomcat, Jetty)를 내장하여 웹어플리케이션 구동 가능
  * Camel 엔진을 내장하여 ESB 엔진으로 사용 가능  



## 2. 아키텍처

 ![bizframe-mas 아키텍처](/images/bizframe-mas-intro/architecture.png)



## 3. 어플리케이션 유형

 mas의 어플리케이션은 기본 어플리케이션, 서비스 어플리케이션, 라우트 어플리케이션으로 나누어 집니다.  
 각각의 어플리케이션은 해당 인터페이스를 구현함으로써 작성할 수 있습니다.  


 ![어플리케이션 ](/images/bizframe-mas-intro/application-type.png)



 - 기본 인터페이스
```java
public interface Application {
		public void init(ApplicationContext context) throws ApplicationException;
		public void destroy(ApplicationContext context) throws pplicationException;
 }
```

 - 서비스 인터페이스
```java
public interface Serviceable {
		public void start() throws Exception;
		public void stop() throws Exception;
}
```

 - 라우트 인터페이스
```java
public interface Routable {
		public void onMessage(Exchange exchange) throws Exception;
}
```



## 4. 어플리케이션 상태


![어플리케이션 상태 ](/images/bizframe-mas-intro/application-status.png)



## 5. 어플리케이션 배치 구조


![어플리케이션 상태 ](/images/bizframe-mas-intro/application-deploy.png)
