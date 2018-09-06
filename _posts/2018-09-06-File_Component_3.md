---
layout: post
title: File Component 3
tags: [Camel, Filter, GenericFile, setHeader]
---

Apache Camel - File component (3)
=============================
### Filter

* 특정 파일만 추출 or skip 하고 싶을 때 사용
* `org.apache.camel.component.file.GenericFilter ` class를 implement하여 사용
```java
public class [method 이름] implements GenericFileter{

	public boolean accept(GenericFile pathname){
   
   		// accpet()에서 false를 return 한다면, 그 file은 skip
   		// Sample Code : file 이름이 test로 시작한다면 skip
   		return !pathname.getFileName().startsWith("test");
    }
}
```


* route에서 bean을 사용할 때는 **#** annotation 사용한다.
```xml
<bean id="[bean 이름]" class="bean으로 만들 filter java class 위치(경로)" />

<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
	<route>
    	<from uri="file:d/camel/src?filtr=#[bean id]"&amp;delay=5000" />
        <to uri="file://d:camel/output" />
    </route>
</camelContext>
```

-----------------------------------------------------------------
### org.apache.camel.component.file.GenericFile
* interface
* method (accept(GenericFile<T> file))을 override하여 사용
* option
	- changeFileName(String newname) : newname으로 file 이름 변경
	- getFileLength : file 크기를 String으로 return
	- getFileName : file 이름을 String으로 return

-----------------------------------------------------------------
### `<setHeader>` in Camel HTTP
* file component 간의 전송에서는 fileName이 그대로 유지되지만, HTTP 전송을 이용한다면 file name이 그대로 유지 X
* `<setHeader>` 이용하여 file 이름 value를 같이 전송

##### 1. route.xml [Client(Sender) 시점]
* `<setHeader>` headerName option 이용하여 file Component의 file 이름을 header에 저장
* `<setHeader>`에 `<simple>`, `<constant>` 모두 사용할 수 있다.
```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
	<route>
    	<from uri="file:d/camel/src?delay=5000" />
        <setHeader headerName="[header에 저장할 variable name]">
        	<simple>${in.header.CamelFileName}</simple> <!-- variable에 저장할 value -->
        </setHeader>
        <to uri="http://localhost:9999/myService/" />
    </route>
</camelContext>
```

##### 2. route.xml [Server(Receiver) 시점]
* File Component의 fileName option을 이용해 file 이름 지정
```xml
<camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
	<route>
    	<from uri="jetty:http://localhost:9999/myService/" />
        <to uri="file://d:camel/output?fileName=${in.header.[Sender에서 지정한 headername]}" />
    </route>
</camelContext>
```


### 참고 자료
* [Apache Camel - File2](http://camel.apache.org/file2.html)
* [Apache Camel - HTTP](http://camel.apache.org/http.html)
* [GenericFile](https://camel.apache.org/maven/camel-2.15.0/camel-core/apidocs/org/apache/camel/component/file/GenericFile.html)
* [How to set value in Header? - Camel-setHeader.xml](https://github.com/apache/camel/blob/master/camel-core/src/test/resources/org/apache/camel/model/setHeader.xml)

