---
layout: post
title: File Component (1)
tags: [A Tag, Test, Lorem, Ipsum]
---
 Apache Camel - File component (1)
=============================
 * File component는 Camel 안에서 file에 접근, 전송하는데 사용
* 다른 Camel component와 같이 사용할 수 있다.
 ----------------------------------------------------------------
#### URI 형태
**file:파일디렉토리경로[opiton]**
**file://파일디렉토리경로[option]**
 \- option을 여러 개 사용하고 싶다면, "&option1=value1&option1=value2&..." 사용
 \- from으로 시작하는 file component 디렉토리는 "${ }"을 사용해서는 안된다.
 ## URI Option
### move, moveFailed, preMove
#### 1. move
* processing 이후 filename 위치
* [default] processing 이후 input 폴더 하위의 .camel 폴더로 이동
	→ ${file:parent}/.camel/${file:onlyname}으로 이동
* File language, Simple language 사용 가능
* 상대적 위치, 절대적 위치 모두 가능
→ 상대적 위치를 사용한다면, file이 consumed 되는 폴더에서 하위 폴더로 생성하여 사용
ex) move=/backup/${file:name}
 #### 2. moveFailed
* processing 과정 중 문제가 생겨서 제대로 완료되지 않았을 때(=실패) 이동할 filename 위치
* File language, Simple language 사용 가능
ex) moveFailed=/error/${file:name:noext}
* 이 폴더로 이동하게 된다면 Camel은 erorr을 handling하게 되고, 이 파일은 다시 poiing 하지 않는다.
 #### 3. preMove
* processing 하기 전 이동할 위치
* 현재 어느 파일이 scan되고 있는지 확인하고자 할 때 유용
* preMove와 move 같이 사용할 수 있다.
ex) javafrom("file://inbox?preMove=inprogress&move=.done")
 -----------------------------------------------------------------
### noop, idempotent
 #### 1. noop
* processing 이후 파일 이동 환경 설정하는 역할
* [default] noop=false (processing된 파일들은 move option 따라 이동)
* true로 설정한다면,
	- idempotent=true가 같이 설정되어 processing 이후에도 같은 위치에 존재
    - move, moveFailed option과 같이 사용할 수 없다.
 **§ 주의 사항**
 noop을 사용하게 된다면 process가 끝난 이후에도 원래 자리에 그대로 유지한다.
그러나 application이 실행될 때마다 process는 다시 시작하게 되고, 중복된 파일이 전송된다. 왜냐햐면
file consumer는 memory를 기반으로 한 Idempotent consumer를 이용하기 때문에 프로그램이 실행될
때마다 모든 파일은 한번씩 process 된다.
 #### 2. idempotent
* [default] idempotent=false
* 이미 처리된 파일들은 다시 읽게 되는 걸 막기 위해서 사용
