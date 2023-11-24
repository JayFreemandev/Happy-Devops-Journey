**flask image 커맨드**  
docker build -t jayfreemandev/flask-hello:1.0 -f Dockerfile .  
docker run -dit -p 8080:8080 --name flask-hello  jayfreemandev/flask-hello:1.0  
docker logs flask-hello  

docker login  
docker push jayfreemandev/flask-hello:1.0  

**minikube 커맨드**  
mac  
`brew install kubectl`  
`brew install minikube`  
  
minikube start --memory 8192 --cpus 4  
kubectl create namespace hello  
kubectl get namespace  
kubectl config current-context  

flask yaml 배포  
flask폴더 이동후 kubectl apply -f flaskApp.yaml  
minikube service flask-hello -n hello  

**elk 커맨드 입력**
kubectl create namespace elk  
3. helm repo add elastic https://helm.elastic.co  
4. es폴더 이동후 kubectl apply -f pv.yaml -n elk  
5. helm install elasticsearch elastic/elasticsearch --version="7.17.3" -f values.yaml -n elk  
6. filebeat폴더 이동후 kubectl apply -f pv.yaml -n elk  
7. helm install filebeat elastic/filebeat --version="7.17.3" -f values.yaml -n elk  
8. logstash폴더 이동후 kubectl apply -f pvc.yaml -n elk  
9. logstash폴더 이동후 kubectl apply -f logstash.yaml -n elk  
10. kibana폴더 이동후 kubectl apply -f kibana-ingress.yaml -n elk  
11. kibana폴더 이동후 helm install kibana elastic/kibana --version="7.17.3" -n elk  
12. logstash폴더 이동후 kubectl apply -f . -n elk  
13. kibana폴더에서 kubectl apply -f . -n elk  

helm install 마다 pod running check 해주기   
```
kubectl get pods -n elk  
```