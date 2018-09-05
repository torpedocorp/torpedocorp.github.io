---
layout: post
title: File Component 2
---

Apache Camel - File component (2)
=============================
### sortBy

* 특정 sorting 순서를 명시하지 않았다면, java.io.File.listFiles()에 따른 순서로 sorting

* URI : ?sortby=group1;group2;group3;....
	\- sorting 순서 우선순위 배분 가능 : group1을 이용하여 sorting 하는데 같은 값이 존재한다면, 다음 우선 순위인 group2 이용하여 sorting
	\- 각 group은 세미콜론(;)으로 구분
* File language 사용
* option
	\- reverse : 역순으로 sorting
    **§ 주의사항 : reverse option은 꼭 맨 앞에 명시해야 한다.**

    \- ignorecase : 특정 case를 ignore할 때 사용


code : [camel-route using sortBy.xml](https://github.com/torpedocorp/torpedocorp.github.io/blob/master/_posts/routeXML/route_fileComponent_3.xml)

-----------------------------------------------------------------
### sorter

* java.util.Comparator `<org.apache.camel.component.file.GenericFile>` class 이용
	→ Comparator() 구현하여 사용한다.


§ java.util.Comparator
* Interface Comparator`<T>`
* method
	- compare(T o1, T o2)
		: o1, o2를 비교함에 따라 양의 정수, 0, 음의 정수 return
	- equals(Object obj)
		: 비교하는 두 대상이 완전히 일치했을 때만 true return


**§ sortBy와 sorter를 하나의 uri option에 같이 사용할 수 없다.**

code : [camel-route using sorter - Comparator bean](https://github.com/torpedocorp/torpedocorp.github.io/blob/master/_posts/routeXML/kr/co/bizframe/comparator/MyFileSorter.java)

code : [camel-route using sorter.xml](https://github.com/torpedocorp/torpedocorp.github.io/blob/master/_posts/routeXML/route_fileComponent_4.xml)

-----------------------------------------------------------------
### Content Based Router (choice, when, otherwise)

####1. choice
* when, otherwise를 묶는 역할

#### 2. when
* "if"와 같은 역할
* `<simple>`, `<xpath>` 사용하여 조건문 사용 가능
* 여러 개의 `<when>` 사용할 수 있다.

#### 3. otherwise
* "else"와 같은 역할

code : [camel-route using choice, when, otherwise.xml](https://github.com/torpedocorp/torpedocorp.github.io/blob/master/_posts/routeXML/route_fileComponent_5.xml)


