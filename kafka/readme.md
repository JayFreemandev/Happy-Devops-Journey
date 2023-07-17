kafka

lunch
```
docker-compose -f zk-single-kafka-single.yml up -d
```

running check
```
docker-compose -f zk-single-kafka-single.yml ps
```

컨테이너 접속(.sh 없이 커맨드 사용)
```
docker exec -it kafka1 /bin/bash
```

카프카 stop or down
```
docker-compose -f zk-single-kafka-single.yml stop
or
docker-compose -f zk-single-kafka-single.yml down
```

topics
```
kafka-topics
```
