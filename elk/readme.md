ELK journey
29CM 로그 시스템의 주요 오픈 소스 (ELK + kafka)

운영시 고려할점
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
