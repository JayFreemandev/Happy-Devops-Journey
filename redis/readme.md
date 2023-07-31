Redis 학습 Repo

**Sorted Sets**
- Set과 유사하게 유니크한 값의 집합이고 각 값은 연관된 스코어를 가지고 정렬되어있다
- 정렬된 상태이기때문에 빠르게 최소/최대값을 구할 수 있다.
- 예) 순위 계산, 리더보드 구현등에 활용된다.  
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/e674cfec-abc6-4a0b-b37e-1c2a0c274c8f)  
RDB로 표현한다면 각 스코어가 갱신될때마다 동일한 테이블 접근해서 업데이트를 치다 경합조건(Race)에 걸릴 수 있다.

**Bitmaps**
- 비트 벡터를 사용해 N개의 Set을 공간 효율적으로 저장할 수 있다.
- 하나의 Bitmap은 4바이트다, 1개의 Integer(4바이트)에 Bitmaps를 사용하면 42억개의 비트들을 한 공간에 저장할 수 있게 된다.

**[유저 목록을 Redis Bitmap 구조로 저장하여 메모리 절약하기, 리멤버 기술 블로그](https://blog.dramancompany.com/2022/10/%EC%9C%A0%EC%A0%80-%EB%AA%A9%EB%A1%9D%EC%9D%84-redis-bitmap-%EA%B5%AC%EC%A1%B0%EB%A1%9C-%EC%A0%80%EC%9E%A5%ED%95%98%EC%97%AC-%EB%A9%94%EB%AA%A8%EB%A6%AC-%EC%A0%88%EC%95%BD%ED%95%98%EA%B8%B0/)**  
설문 조건에 맞는 유저를 타겟팅하여 응답을 수집하고, 참여한 유저에겐 소정의 리워드를 지급하고 있습니다.
특정 유저에게 맞는 설문만을 제공하기 위하여 내부의 타겟 시스템을 이용해 설문마다 참여할 수 있는 유저의 고유 ID를 보관하고 있습니다.
이 과정에서 Redis에는 설문에 참여할 수 있는 유저 ID 목록을 SET 구조로 저장했는데 보관수에 비해 Redis 메모리가 너무 비대해지는 문제

Redis에 대상 유저를 저장하는 용도로 채택한 이유
- 대상 유저 ID 목록의 크기는 적게는 수십명에서 많게는 수십만, 수백만의 단위로 구성됩니다.
유저 목록에 대한 데이터는 광고 서버에서 보존하고 있으므로, 대량의 크기를 가지는 정보를 다른 서비스의 DB에 다시 저장하는 작업은 피하였습니다.
- 대상 유저 ID 목록은 설문이 종료된 뒤에 다시 사용하지 않는 정보입니다. Time-to-live를 어플리케이션 레이어에서 구현하지 않고, Redis 내에서 처리할 수 있습니다.

19만명의 유저의 참여 정보를 저장하기 위해서 약 10MB에 달하는 메모리를 사용한다는 것은 문제가 될 것으로 우려하였습니다.
수 백만명을 대상으로 하는 설문 10개가 동시에 진행된다면 오로지 유저 목록을 담기 위해 사용하는 Redis의 메모리 크기만해도 기가바이트 단위가 될 것을 짐작할 수 있습니다.

이러한 상황을 초래하게 만들지 않고자, 메모리 크기를 줄이기 위해 Redis의 Bitmap 구조를 고려

```
가장 큰 유저 ID가 50만이면서 50만명에 대한 참여 정보를 담는다면 SET 구조에선 20,194,504 Byte, Bitmap 구조에선 114,768 Byte
```

**까다로운 점**  
리멤버 리서치의 유저 타겟팅 과정에서 Redis Bitmap 구조를 이용한 것은 상수 시간의 시간 복잡도를 가지면서 차지하는 메모리는 절약하는데 이점을 얻었습니다.
그렇지만, Bitmap 구조가 모든 상황에서 좋은 것이 아니라는 점은 주의해야합니다.

상대적으로 큰 오프셋을 가지며 적은 갯수의 데이터를 보관한다면 Bitmap보다 SET에 저장할 때에 차지하는 메모리 크기가 더 작습니다.
최대 유저 ID가 1백만이면서 유저 10명의 대상 여부를 저장하는 경우가 이러한 경우입니다.
수십 혹은 수백 단위의 상대적으로 적은 수의 유저가 포함되는 경우엔 SET구조를 함께 사용하도록 고려하는 것이 좋아보입니다.

또한, Bitmap 자료 내에서 Bit가 1인 모든 유저의 ID를 구하는 것은 Redis 명령어로서 지원하지 않습니다. 따라서 이는 Redis의 Lua Scripting을 이용해 해결해야 합니다.

**FAQ**  
Q.비트맵은 id가 꽤 작은 정수형인 경우엔 유용하게 쓸 수 있을 것 같네요. 어드민에서 설문지별 유저목록 뽑는건 엄청 오래 걸릴거같은데.. 뽑는동안 redis 락 걸리지 않나요? 그리고 뽑는 동시에 다른 곳에 저장해두지 않는 이유가 있을까요 궁금합니다. 더 확장성 있는 구현이 있을 것 같은데  
A. 남겨주신 내용과 같이 대량의 유저 목록을 Bitmap 데이터에서 한번에 추출한다면 병목 현상이 발생하게 됩니다.
Lua스크립트를 통해 Redis에서 evalsha 명령어의 긴 처리 시간으로 인해 전체적인 성능 저하가 나타나는 상황을 피하고자 오프셋 방식의 조회를 적용해 나누어 요청하는 방식을 적용해 사용하고 있습니다.
이렇게 추출한 참여 대상 유저 목록은 영구적으로 저장하지 않고 캐싱 방식으로 저장하고 있습니다.

Q. user id가 숫자인 경우에만 사용이 가능할 것 같은데, user id에 알파벳이 포함되어 있다면 사용하기 어려운 방법이겠죠?  
A. Bitmap를 이용하는 경우엔 offset으로 정수를 넣을 수 있습니다. 따라서 알파벳이 포함된 user id라면 사용하기 어려울 것 같습니다.
Key에 유저 존재 여부, 전체 유저 수 조회를 구현하는 방법을 찾고 계신다면 아래 두가지 방법이 떠오릅니다.

- HyperLogLog의 PFCOUNT 명령어
  - Bitmap 구조에서 지원하는 BITCOUNT 와 비슷한 명령어를 사용해 유니크한 유저 수를 구하고 싶은 경우엔 HyperLogLog의 PFCOUNT 명령어가 있습니다.
  그러나 HyperLogLog는 확률적 데이터 구조이므로 약 2%의 오차를 가지고 있습니다.
  정확한 값을 내려주고 시간이 오래 걸리는 대신에, 오차를 범해 근삿값을 제공하는 대신 자원을 아주 적게 사용해도 되는 결과 값이라면 HyperLogLog를 고려해보면 좋을 것 같습니다.


**스프링에서 사용시 Lettuce vs Spring Data Redis**  
Lettuce는 Spring Data Redis안에 내장되어있는 라이브러리, RedisTemplate라는 추상 레이어를 제공

**캐시 운영 전략**

Cache-Aside(Lazy Loading)

- 항상 캐시를 먼저 체크, 없으면 원본(ex, DB)에서 읽온 후에 캐시에 저장
- 필요한 데이터만 캐시에 저장되고 캐시 미스에도 치명적이지 않은 장점
- 최초 접근이 느리고 업데이트 주기가 불규칙적이라 최신 데이터 보장 못함

Spring에서는 캐시 추상화 어노테이션 제공

- @Cacheable : 메소드에 캐시 적용(Default는 Cache-Aside)
- @CachePut: 메소드의 리턴값을 캐시로
- @CacheEvic: 메소드에 키값 기반으로 캐시 삭제

**Pub/Sub**

Sentinel  
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/040e9b5a-6acd-408c-a068-a9ac7a0c2da1)   
NHN에서는 두대 서버에서는 일반 Redis와 센티널을 띄우고 최저 사양의 서버에 센티널 로드를 올려 사용(최소 3개 이상의 센티널이 필요)

Redis 선택시 아키텍처 선택 기준
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/bf2d576e-0034-4f22-89c3-53605386d2b3)    
자동 페일 오버도 필요하고 확장 가능성을 열어둬서 샤딩까지 필요하다면 클러스터 구조 샤딩까지 필요없다면 센티널 구조  
자동 페일 오버는 필요없지만 복제는 필요하다 레플리케이션, 다 필요없다 그냥 Redis 한대만 구성

**운영팁과 장애**
Redis는 싱글 스레드라서 keys * 같은거 사용하다 스레드 잡히면 곤란해진다 scan으로 대체해서 사용해라 반대로 Delete로 key를 지울때 key안에 백만개가 넘는다면 지우는동안 아무것도 못하게된다.
이때 unlink 커맨드를 사용하면 키를 백그라운드에서 지워주기때문에 사용하면 좋다.

**장애를 막아주는 설정**  
```
STOP-WRITE=ON-BGSAVE-ERROR = NO
```
yes가 default로 되어있는데 RDB 파일 저장 실패시 redis의 모든 write를 막는 기능이다. 만약 Redis 모니터링을 실시간으로 하고있다면 기능을 꺼도 된다.

```
MAXMEMORY-POLICY = ALLKEYS-LRU
```
redis를 캐시로 사용할 때 Expire Time 설정 권장, 메모리가 가득 차면 새로운 정보 입력이 불가능하기때문에 장애로 발생 가능성이 있다.
volatile-lru Expired Time이 가장 최근에 사용하지 않은 key를 삭제하는데 만약 만료시간이 없는 key만 남아 있다면? 똑같이 장애 발생
추천 설정은 allkeys-lru, 모든 키에 대해 lru 방식으로 키를 삭제하겠다.

하지만 대규모 트래픽 환경에서 TTL을 너무 작게 설정한 경우 캐시 sTAMPED 현상이 발생한다. 여러 키들이 동시에 만료되면 DB에 Duplicate Read/Write가 발생하면 지연 발생.
실제 NHN 티케팅에서 하나의 인기있는 공연이 오픈되면 몇십개의 어플리케이션 서버에서 커넥션이 연결된다. 부하 확인을 위해 개발 서버의 스카우터 로그를 확인했고 TTL 시간을 늘려줌으로써 해결

MaxMemory값 설정
Persistence 복제시 RDB나 AOF rewrite할때 fork()로 child process에서 copy on write를 시도해서 실제 parent process(서비스 운영중인)는 전혀 영향이 가지 않는다.
하지만 메모리를 복사해서 사용하기때문에 두배로 사용하는 경우가 발생 할 수 있어서 실제 메모리의 절반으로 설정하는것이 안전하다.(메모리 풀로 장애 발생 가능성 방지)

메모리 모니터링이 가장 중요하고 used_memory와 used_memory_rss 값중에 rss를 보는게 더 중요하다 왜냐하면 그냥 used memory는 논리적으로 redis가 사용되는 메모리고
rss는 OS가 redis에게 할당하기 위해 사용한 물리적 메모리 양이다. 실제 저장된 데이터는 작은데 rss는 치솟고 있다면 이 차이가 큰 경우를 fragmentation이 크다고 이야기한다.
주로 삭제되는 key가 많을때 증가한다. (TTL로 인한 eviction이 빈번하게 생기는 경우)

```
config set activedefrag yes
```
(공식 문서에서도 단편화가 많이 발생할때만 켜서 확인하라고 권장중이다.)

큰 메모리를 사용하는 instance 하나보다는 적은 메모리를 사용하는 instance 여러개가 안전함
```
CPU 4 core, 32GB memory < 8GB instance x 3개
```
fork()때문에 read가 많은 서비스는 전혀 문제가 없다. write가 heavy한 memory를 2배 이상 사용함(copy on write)

CPU 100%일 경우
- 좀 더 좋은 CPU 서버로 이전, 웬만하면 단순 get/set은 초당 10만 이상 처리 가능
- O(n) 계열 명령어가 많은 경우 Monitor과 Log로 특정 패턴 파악해서 수정이 필요


reference  
[우아한테크세미나 191121 우아한Redis by 강대명님](https://www.youtube.com/watch?v=mPB2CZiAkKM&t=6s)  
[NHN Redis 야무지게 사용하기](https://www.youtube.com/watch?v=92NizoBL4uA&pp=ygUJ66CI65SU7Iqk)
