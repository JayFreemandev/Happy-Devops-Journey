**Redis**

Remote Dictionary Server

원격, dictionary는 python에서 hashmap처럼 key,value의 자료구조이다. 

키 벨류로 이루어진 원격 서버를 의미한다.

**Cache**

나중의 요청에 대한 결과를 미리 저장했다 빠르게 사용하는 것

메모리 계층 구조상 위(cpu)로 갈수록 비싸고 빠르고 밑(hdd)으로 갈수록 느리고 싸다. 

**in memory db**

DB보다는 빠르고 Memory에 자주접근하고 덜 바뀌는 데이터를 저장하자

**redis의 hash와 자바의 hash 차이**

자바 경우 서버가 여러대일때 consistency 문제가 발생. 세션을 자바 객체로 저장한다면 다른 서버에서는 가져올수없는 예시가 존재한다. 또한 멀티 스레드 환경에서 race condition 가능성

**Race Condition**

여러 스레드가 경합하면서 context swtching이 자주 발생하면 원하지 않는 결과가 발생

**Race Condition 해결**

Redis는 싱글 스레드 사용해서 해결, 동시에 접근 못하게 Atomic Section에 대한 동기화를 제공하고 서로 다른 트랜잭션 read/write를 동기화
