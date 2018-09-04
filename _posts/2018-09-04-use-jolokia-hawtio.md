---
layout: post
title: jolokia와 hawtio 사용해보기 
tags: [jolokia, hawtio]
excerpt_separator: <!--more-->
---

# hawtio와 jolokia



## hawtio와 jolokia

![](http://3.bp.blogspot.com/-DFtaudeSNxo/UkmADhuA9HI/AAAAAAAAA3w/PbOCNOwEaJ4/s1600/Karaf_JMX.png)

 


## JMX

- JVM 상태를 모니터링할 때 간단하게 사용할 수 있는 api

- JDK 5.0 버전 이상의 서버에서는 필수로 제공됨  

 

### MBean

- 관리대상 자바 오브젝트

- MBean을 이용하여 디바이스, 어플리케이션 또는 관리가 필요한 모든 자원을 나타낼 수 있음

- 표준 MBean, 동적 MBean, Open MBean, Model MBean 등 총 4가지 종류가 있음  

 


## Jolokia

- HTTP를 통하여 MBean의 속성에 접근할 수 있게 해주어 JMX값을 JSON 형식으로 받아볼 수 있게 해주는, 일종의 JMX-HTTP 커넥터  


## Hawtio2

- Jolokia로부터 받은 json 데이터를 AngularJS로 화면에 뿌려주는 웹 콘솔  
 

## 참고 자료

- [상단 이미지](http://coderthoughts.blogspot.com/2013/10/jmx-role-based-access-control-for-karaf.html)

- [자바 성능 튜닝 이야기 (이상민 지음)](http://www.insightbook.co.kr/book/programming-insight/%EA%B0%9C%EB%B0%9C%EC%9E%90%EA%B0%80-%EB%B0%98%EB%93%9C%EC%8B%9C-%EC%95%8C%EC%95%84%EC%95%BC-%ED%95%A0-%EC%9E%90%EB%B0%94-%EC%84%B1%EB%8A%A5-%ED%8A%9C%EB%8B%9D-%EC%9D%B4%EC%95%BC%EA%B8%B0)

- [Jolokia: JMX-HTTP bridge](https://oddpoet.net/blog/2013/09/26/jolokia-jmx-http-bridge/)

- [hawtio Chrome Extension 설치 방법은?](http://opennaru.freshdesk.com/support/solutions/articles/1000076976-hawtio-chrome-extension-%EC%84%A4%EC%B9%98-%EB%B0%A9%EB%B2%95%EC%9D%80-)

- [ActiveMQ 모니터링(hawtio + jolokia)](http://tomining.tistory.com/90)

- [hawtio/hawtio-core](https://github.com/hawtio/hawtio-core)

- [https://jdm.kr/blog/231](https://jdm.kr/blog/231)

- [JBoss Fuse Jolokia requests](https://www.rubix.nl/blogs/jboss-fuse-jolokia-requests)