---
layout: post
title: File, Simple Language
tags: [Camel, file language, simple language]
---

Apache Camel - File Language 〮 Simple Language
=============================
## File Language

- 현재 File Language는 Simple language에 merge → Simple language에서 File language 사용 가능
- file과 관련하여 Simple Language의 확장 부분

### Syntax
* file:length : Long type으로 file length return (Camel 2.5부터는 file:size로 대체)
* file:modified : Date type으로 file 최근 수정 날짜 return

####1. Relative Paths
ex) if file is "d:/camel/src/test/text.txt"

* **file:absolute** - false
* **file:absolute.path** - d:/camel/src/test/text.txt
* **file:ext** - txt
 : 확장자만 return
* **file:name** - test/text.txt
* **file:name.ext** - txt
* **file:name.noext** - test/text
* **file:onlyname** - text.txt
* **file: onlyname.noext** - text
* **file:parent** - src/test
* **file:path** -  src/test/text.txt


####2. Absolute Paths
ex) if file is "d:/camel/src/test/text.txt"

§ Relative paths와 관련하여 몇 가지만 제외하고는 동일한 값 return


* **file:absolute** - true
* **file:parent** - d:/camel/src/test
* **file:path** - d:/camel/src/test/text.txt



-----------------------------------------------------------------
## Simple Language

* ${ } 이용해 value에 접근한다.
	→ message의 body에 접근할 때 : in.body, ${body}, ${in.body} 사용
* when, choice와 같은 content based router이외에도 filter와 같은 곳에서도 사용 가능

### 1. Variables
* **amelId** : CamelContext name
* **exchange** : Exchange
* **exchangeId** : exchange Id
* **id** : input message id
* **body, in.body** : input body
* **bodyAs(type)** : body를 type에 맞춰서 변환, null로 변환될 수 있다.
* **header, in.header** : input header

### 2. Operator Support

* syntax : ${leftValue} [operator support] rightValue
* rightValue는 ${ } expression, null, ' '로 묶은 String, constant value 사용할 수 있다.

**§ 주의 사항**
**1. 왼쪽에 위치할 value(leftValue)는 곡 ${ }로 묶어야 한다. **
**2. operator support 양옆으로 공백이 있어야 한다. **

* **==, =~, >, >=, <, <=, != ....**
* **contains, not contains ** : rightValue를 포함하느냐/포함하지않느냐
* **in, not in** : 원하는 element가 list에 존재하느냐/존재하지 않느냐 (list에서 각 element는 ,로 구분)
* **starts with** : leftValue가 rightValue로 시작하는지에 대한 testing
* ** ends with** : leftValue가 rightValue로 끝나는지에 대한 testing

## 참고 자료
* [Apache Camel - File Expression Language](http://camel.apache.org/file-language.html)
* [Apache Camel - Simple Expression Language](http://camel.apache.org/simple.html)
