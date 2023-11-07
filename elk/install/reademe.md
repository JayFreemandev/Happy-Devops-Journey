**차례대로 커맨드 입력**

helm install 마다 pod running check 해주기 
```
kubctl get pods 
```

3. helm repo add elastic https://helm.elastic.co
4. es폴더 이동후 kubectl apply –f pv.yaml
5. helm install elasticsearch elastic/elasticsearch --version="7.17.3" -f values.yaml
6. filebeat폴더 이동후 kubectl apply –f pv.yaml
7. helm install filebeat elastic/filebeat --version="7.17.3" -f values.yaml
8. kibana폴더 이동후 helm install kibana  --version="7.17.3"  elastic/kibana
9. kubectl apply –f .
10. logstash폴더 이동후 kubectl apply –f .

> 압축파일 내부 readme 참고 가능 
> 참조 영상 : [유튜브](https://www.youtube.com/watch?v=0TZCNv45cuU)
> 참조 깃허브 : [원본 깃허브](https://github.com/shawon100/elasticsearch-logstash-kibana-kubernetes)
> 참조 자료 : [원본글](https://www.shawonruet.com/2023/05/how-to-install-or-setup-elasticsearch.html)
