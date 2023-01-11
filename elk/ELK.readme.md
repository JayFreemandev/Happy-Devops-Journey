# ELK란
![Untitled](https://user-images.githubusercontent.com/72185011/211808256-4ec2e476-fc2d-4b50-9b36-2e4e195e33a0.png)  
[https://www.elastic.co/kr/what-is/elk-stack](https://www.elastic.co/kr/what-is/elk-stack)

ELK는 세기술의 약자로 **E**lasticsearch 와 **L**ogstash, **K**ibana의 앞글자들로 ELK라는 동물이 있어서 단어의 순서를 ELK 로 축약한 것 같다.
Elasticsearch, Logstash 및 kibana이 오픈소스 프로젝트 세개의 머리글자이다.
Elasticsearch 는 **검색 및 분석 엔진**이다. **(로그 저장 및 검색)**
Logstash 는 여러 소스에서 동시에 데이터를 **수집하여 변환**한 후 Elasticsearch와 같은 "stash"로 전송하는 서버 사이드 데이터 처리 파이프라인이다.  **(로그 수집 엔진)**
Kibana는 사용자가 Elasticsearch 에서 차트와 그래프를 이용해 **데이터를 시각화**할 수 있게 해준다. **(로그 시각화 및 관리)**
<br>
<br>

1) Data Processing (Logstash) **"Collect & Transform"**  
먼저 서버 내의 로그, 웹, 메트릭 등 다양한 소스에서 데이터를 수집한다.  
데이터 변환 및 구조 구축 
데이터 출력 및 송신  
- > 데이터(로그)를 수집하고 변환하는 작업을 먼저 해야한다. 
(수집할 로그를 선정해서, 지정된 대상 서버(ElasticSearch)에 인덱싱하여 전송하는 역할을 담당)  

로그란 시스템이나 애플리케이션 상태 및 행위와 관련된 풍부한 정보를 포함하고 있다. 이러한 정보를 각각 시스템마다 파일로 기록하고 있는 경우가 대다수 일것이다.
그렇다면 과연 이러한 정보를 파일로 관리하는 것이 효율적인 것인가를 생각해볼 필요가 있다.
한곳에 모든 로그데이터를 시스템별로 구분하여 저장하고 하나의 뷰에서 모든 시스템의 로그데이터를 볼 수 있다면 굉장히 관리가 편해질 것이다.  

이러한 모든 로그정보를 수집하여 하나의 저장소(DB, Elasticsearch 등)에 출력해주는 시스템이 로그스태시라는 시스템이다. 
Filebeat와 연동을 한다면 파일에 축적되고 있는 로그데이터를 하나의 저장소로 보낼 수도 있고, 카프카의 토픽에 누적되어 있는 메시지들을 가져와 하나의 저장소에 보낼 수도 있다.
<br>
<br>

2) Storage (Elasticsearch) **"Search & Analyze"**  
그리고 데이터를 저장 및 분석, 관리한다.  
<br>
<br>

3) Visualize (Kibana) **"Visualize & Manage"**  
Dashborad를 통한 데이터 탐색  
팀원들과 공유 및 협업하는데 사용 가능  
액세스 제어 사용 가능  
- > 검색하고 수집하여 분석, 저장, 관리한 데이터를 기반으로 시각화를 한다.  
(데이터를 시각적으로 탐색하고 실시간으로 분석 할 수 있다.  
- 시각화를 담당하는 HTML + Javascript 엔진이라고 보면 된다.)  
<br>
<br>
  
Elasticsearch는 json 기반의 분산형 오픈 소스 RESTful 검색 엔진이라고한다.  
**로그를 손쉽게 수집**하기 위해 강력한 수집 파이프라인인 **Logstash**을 도입했고,  
이를 **시각화** 하기 위해 유연한 시각화 도구인 **Kibana**를 도입했다고 한다.  

Logstash는 데이터 수집의 역할을 맡고 있는데 원하는 형태로 데이터 입출력 변환까지 맡고 있어서 오버헤드가 컸다고한다.
따라서 많은 사용자들이 "파일을 추적하고 싶다"는 등 요구사항을 제시했고, Elastic 회사는 **오로지 데이터의 수집만을 담당하는 경량화된 모듈인 Beats를 도입**했다.
Beat는 서버에 에이전트 형식으로 설치하는 오픈소스 데이터 수집기 입니다. Beats 는 데이터를 Elasticsearch에 직접 전송할 수 있으며, Logstash를 통해서 데이터를 전송할 수도 있습니다.  
<br>
<br>

**Overall**    
![Untitled 1](https://user-images.githubusercontent.com/72185011/211808221-4512db6f-28b8-4877-b963-2c9012c67ec5.png)
