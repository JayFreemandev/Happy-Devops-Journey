### ELK란
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/2dc6c923-332f-4751-82c2-ec62244334fe)  
- **Logstash**
 -> 데이터를 수집하여 **변환**한 후, Elasticsearch 같은 stash로 전송하는 데이터 처리 파이프라인.
- **Elasticsearch**
 -> 검색 및 분석 엔진. JSON 기반의 분산형 RESTFul 검색 엔진.
- **Kibana**
 -> Elasticsearch에서 차트와 그래프를 이용해 데이터 시각화를 가능하게 해주는 도구.

 
**logstash vs fluentd vs fluentbit vs vector**  
logstash가 fluentd보다는 메모리 사용량이 많고 공식적인 elastic 스택의 일부이기 때문에 지원하는 기능이 더 많음. fluentd는 상대적으로 더 가볍고 성능면에서 10000개 로그 수집에 문제없이 비슷하게 쌓을 수 있다. 소규모라면 더 가벼운 fluentbit가 존재한다. fleuntd memory 40mb / fluentbit memory 650kb 메모리 차이가 큰 편이고 fluentd는 1000개 이상의 플러그인을 제공하지만 fluentbit은 100개 미만

#### 기업별 도입 case
**무신사에서는 fleuntd -> logstash**  
디스크 용량 부족으로 로그가 유실 될 가능성이 있고 
기존 29CM 에는 Kubernetes 환경에 맞게 Fluentd 를 활용한 EFK 기반의 로그 파이프라인이 기존에 존재
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/eec1f1d3-794f-4665-9296-114a2312ae97)  

Kubernetes 환경에 맞게 가벼운 Fluentd 를 사용했고 Fluentd 컨테이너들은 각각의 서비스에 사이드카로 주입되어 배포됩니다.
사이드카로 주입된 각각의 Fluentd 컨테이너들은 Fluentd Aggretator 에게 로그데이터를 송신하고,Aggregator는 모든 로그 데이터를 집계하여 ES 에 인덱싱

Disk 용량 부족으로 인해 ES 에 로그가 적재되지 못하게 되면 Disk 용량을 확보하고 ES 복구하기 전까지는 로그 유실을 피할 수 없었습니다.
ES 의 인덱싱 속도가 잠시 느려지거나 일시적인 장애 발생 시 Fluentd Aggregator 가 버퍼 역할을 해줘야 하는데 
해당 버퍼는 너무 단순하고 대량의 데이터를 쓰기에는 적절하지 못했습니다.

After
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/c395c5ee-712b-4c61-b3f2-c682ade4cf6f)
로그를 송신하는 구간과 수집하는 구간 사이에 Kafka 를 버퍼로 두었다. 29CM 의 신규 로그 시스템에서는 Kafka 의 retention 을 3일(24 * 3 hours) 로 지정하여, 주말을 포함하여 ES 의 장애가 발생하더라도 ES 가 다시 복구 되는 즉시 Kafka 에 쌓인 모든 로그 데이터를 ES에 인덱싱 할 수 있게

로그를 수집하고 적재하는 역할에 있어서 기존 Fluentd 를 Logstash 로 변경하였습니다.

신규 로그 시스템에서는 Kafka 를 통한 버퍼 구간을 도입하는게 중요했는데 Fluentd 의 기본 베이스 이미지에서는 Kafka 의 플러그인이 미설치 되어 있었고 Logstash 에서는 Kafka 플러그인이 기본으로 제공되고 있었습니다. 로그 수집 스택에 있어서 ELK 스택 (ElasticSearch + Logstash + Kibana) 가 오랜 시간 검증

**Toss에서는 올해 Logstash -> vector로 전환하여 ==메모리 96% 절약== 달성**  
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/a5b57e7b-2184-433e-918c-dc1900baecea)  
Datadog에서 인수, 깃헙 스타수는 fluentd를 앞서고있음 logstash는 jvm기반이지만 vector는 rust기반이라 메모리 효율적이고 성능이 좋다. 토스 로그는 하루 100억건 이상 쌓이고 있었고 쿠버네티스에서 클러스터 관리를 하고있었는데 logstash -> vector 전환 이후 메모리 사용량이 260gb -> 10gb 감소

### log collector benchmark  
Who is the winner — Comparing Vector, Fluent Bit, Fluentd performance(2021)에 따르면 Vector가 모든 면에서 다른 collector를 압도하지는 않는다. 30분동안 로그 부하를주고 각각 어느정도 cpu와 memory를 소비하는지 벤치마킹을 진행했다.

![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/6ac8a814-5a44-4fc9-a226-e4c30d621e07)  
Log Per Second (LPS) 기준으로 보면 수집량 자체는 light에서 medium까지는 모든 collector들이 다 비슷하나 Heavy 대규모로 가는순간 Vector가 LPS 수치상 높은 성능을 보여준다.

![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/39ebcc1e-d8ee-44b1-b1ce-dd61f4e5a525)  
메모리상 이점도 lps와 동일하게 대규모로 갈수록 많게는 50% 이상, 최소 20% 메모리 사용량이 감소한다.

![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/b9ef6009-3962-4f72-af28-569b2072c534)

![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/3bd05ae4-ac1a-4a64-ade1-a32c82adda70)
lps per cpu와 k8s에서 cpu를 할당하는 단위인 milicore를 측정했을때 대규모 부하일시 vector는 다른 collector들에 비해 2배~3배 더 많은 cpu를 소모하게된다. 처음 lps 측정 결과 vector가 앞도하는 결과물을 보여준것은 cpu를 기반으로한 scale up에 특화되어있다는 의미이고 다른 collector들은 그렇지 않음을 보여준다.

cpu로만 본다면 fluentbit도 vector만큼 높은 성능을 보여주고있다. 메모리를 줄이고 cpu만 늘리면서 확장한다는 가정이면 vector도입이 유리해보이고 cpu를 높일 수 없는 상황이라면 fluentbit을 선택해도 합리적인 성능을 보여준다.

### reference  
[Comparing Vector, Fluent Bit, Fluentd performance](https://medium.com/ibm-cloud/log-collectors-performance-benchmarking-8c5218a08fea)  
[ELK 스택을 사용하면서 겪었던 이슈들](https://blooog.tistory.com/entry/Elasticsearch-ELK-%EC%8A%A4%ED%83%9D%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%A9%B4%EC%84%9C-%EA%B2%AA%EC%97%88%EB%8D%98-%EC%9D%B4%EC%8A%88%EB%93%A4)
