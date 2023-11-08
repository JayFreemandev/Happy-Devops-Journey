Argo CD 설치하기(방법)     
설치방법에는 두가지가있다. 첫번째는 helm을 이용하는 방법, 두번째는 manifest를 사용하는 방법이다.      
여기서는 두번째 방법을 사용해서 설치한다.      

**네임스페이스 생성  **  
kubectl create namespace argocd  

Argo cd manifest 파일 다운로드후 배포  
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml  
배포가 잘되었는지 확인  

mezzo-skan@mezzoui-MacBookPro ~ % kubectl get pods -n argocd   
NAME                                                READY   STATUS    RESTARTS      AGE  
argocd-application-controller-0                     1/1     Running   2 (96s ago)   9d  
argocd-applicationset-controller-57db5f5c7d-xznmq   1/1     Running   2 (96s ago)   9d  
argocd-dex-server-c4b8545d-5mn86                    1/1     Running   2 (96s ago)   9d  
argocd-notifications-controller-7cddc64d84-vhm9l    1/1     Running   2 (96s ago)   9d  
argocd-redis-6b7c6f67db-bhjzw                       1/1     Running   2 (96s ago)   9d  
argocd-repo-server-6867ddcc74-b4pcv                 1/1     Running   2 (96s ago)   9d  
argocd-server-64957744c9-vbchz                      1/1     Running   2 (96s ago)   9d

**Argo CD 서버 접속방법**  
서버에 접속하기 위해서는 ingress controller을 사용하여 외부에서 접근가능 하도록 설정 하거나, 네트워크 정보를 LoadBalancer 서비를 생성하여 외부에서 접근 할 수있도록 설정 하는 것이다.  
서비스의 타입을 보면 ClusterIP로 되어 있다. 이중에 우리가 접속하려는 ArgoCD web serivce는argocd-server 이다. 외부에서 접근하기 위해서는 type을 nodePort로 만들어 주거나 ingress Controler 를 이용하는 것이다. 여기서는 node port로 설정한다.  
참고로 몇가지 방법이 더있음  

Service Type을 Load Balancer 로설정    
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'  

**Node Port로 설정  **  
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'  
Ingress 설정참고 → Ingress Configuration - Argo CD - Declarative GitOps CD for Kubernetes (argo-cd.readthedocs.io)  

**Port Forwarding 설정  **  
kubectl port-forward svc/argocd-server -n argocd 8080:443   

다음 명령어를 실행하면 서비스 타입과 몇가지 설정을 확인 할 수있다.   
Type 모두 ClusterIP로 인데 이중에 외부에서 접근할 argocd-server 만 Node Port로 변경할 한다.  
mezzo-skan@mezzoui-MacBookPro ~ %  kubectl get service -n argocd    
NAME                                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE  
argocd-applicationset-controller          ClusterIP   10.104.234.198   <none>        7000/TCP,8080/TCP            15d  
argocd-dex-server                         ClusterIP   10.106.150.106   <none>        5556/TCP,5557/TCP,5558/TCP   15d  
argocd-metrics                            ClusterIP   10.109.110.47    <none>        8082/TCP                     15d  
argocd-notifications-controller-metrics   ClusterIP   10.110.137.140   <none>        9001/TCP                     15d  
argocd-redis                              ClusterIP   10.97.26.116     <none>        6379/TCP                     15d  
argocd-repo-server                        ClusterIP   10.106.114.159   <none>        8081/TCP,8084/TCP            15d  
argocd-server                             ClusterIP    10.101.125.129   <none>       80/TCP,443/TCP               15d  
argocd-server-metrics                     ClusterIP   10.102.113.18    <none>        8083/TCP                     15d1.

**다음 명령어를 실행하여 service type을 node port 로 변경한다.**  
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'  
다음명령어를 실행하여 server type 이 변경된 것을 확인하다.  

mezzo-skan@mezzoui-MacBookPro ~ %  kubectl get service -n argocd   
NAME                                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE  
argocd-applicationset-controller          ClusterIP   10.104.234.198   <none>        7000/TCP,8080/TCP            15d  
argocd-dex-server                         ClusterIP   10.106.150.106   <none>        5556/TCP,5557/TCP,5558/TCP   15d  
argocd-metrics                            ClusterIP   10.109.110.47    <none>        8082/TCP                     15d  
argocd-notifications-controller-metrics   ClusterIP   10.110.137.140   <none>        9001/TCP                     15d  
argocd-redis                              ClusterIP   10.97.26.116     <none>        6379/TCP                     15d  
argocd-repo-server                        ClusterIP   10.106.114.159   <none>        8081/TCP,8084/TCP            15d  
argocd-server                             NodePort    10.101.125.129   <none>        80:32238/TCP,443:32619/TCP   15d  
argocd-server-metrics                     ClusterIP   10.102.113.18    <none>        8083/TCP                     15d3.
argocd-server 의 Type이 NodePort로 변경된 것을 확인 할 수있다.  

**4.제 서비스의 외부 접근 설정 이후 IP 및 포트를 확인하여 접속한다.**  
#### 서비스 상세 정보 확인 
kubectl describe svc argocd-server -n argocd  

mezzo-skan@mezzoui-MacBookPro ~ % kubectl describe svc argocd-server -n argocd   
Name:                     argocd-server    
Namespace:                argocd    
Labels:                   app.kubernetes.io/component=server       
                          app.kubernetes.io/name=argocd-server    
                          app.kubernetes.io/part-of=argocd    
Annotations:              <none>     
Selector:                 app.kubernetes.io/name=argocd-server    
Type:                     NodePort    
IP Family Policy:         SingleStack    
IP Families:              IPv4   
IP:                       **10.101.125.129**    
IPs:                      10.101.125.129        
Port:                     http  80/TCP     
**TargetPort:               8080/TCP**       
NodePort:                 http  32238/TCP    
Endpoints:                10.244.0.88:8080      
Port:                     https  443/TCP    
**TargetPort:               8080/TCP**     
NodePort:                 https  32619/TCP        
Endpoints:                10.244.0.88:8080     
Session Affinity:         None    
External Traffic Policy:  Cluster    
Events:                   <none>     

mezzo-skan@mezzoui-MacBookPro ~ % kubectl get svc -n argocd     
NAME                                      TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE      
argocd-applicationset-controller          ClusterIP   10.104.234.198   <none>        7000/TCP,8080/TCP            15d    
argocd-dex-server                         ClusterIP   10.106.150.106   <none>        5556/TCP,5557/TCP,5558/TCP   15d        
argocd-metrics                            ClusterIP   10.109.110.47    <none>        8082/TCP                     15d     
argocd-notifications-controller-metrics   ClusterIP   10.110.137.140   <none>        9001/TCP                     15d   
argocd-redis                              ClusterIP   10.97.26.116     <none>        6379/TCP                     15d     
argocd-repo-server                        ClusterIP   10.106.114.159   <none>        8081/TCP,8084/TCP            15d    
**argocd-server                             NodePort    10.101.125.129   <none>        80:32238/TCP,443:32619/TCP   15d**      
argocd-server-metrics                     ClusterIP   10.102.113.18    <none>        8083/TCP                       

15dIP 와 Port를 확인하면 외부에서 접속 가능한 NodePort가 생성된것을 볼수있다.  
[http://10.101.125.129:32238](http://10.101.125.129:32238) 로 접속하면 argoCD web ui 에 접속 할 수있다.  

접속하려면 비밀번호가 필요한데, 다음 명령어를 통해 비밀번호를 확인 할 수있다.  

kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo    
**ZpiWlqPPMqvOAmpq**어카운트 정보는 admin 임으로 id 와 passwd 를 입력한뒤 로그인 한다.      
추가)       
미니쿠버(minikube) 에서는 아무리 Cluster IP를 입력하고 서버에 접근 하려고 해도 접속 할 수 없다. 미니쿠버 터널링을 해주어야 한다.     
다음 명령어를 실행하여 argocd-service 의 터널링을 실행한다.                 
minikube service <service name> -n <nsmespace>   

minikube service **argocd-server  -n argocd    
|-----------|---------------|-------------|---------------------------|    
| NAMESPACE |     NAME      | TARGET PORT |            URL            |       
|-----------|---------------|-------------|---------------------------|    
| argocd    | argocd-server | http/80     | http://192.168.49.2:32238 |    
|           |               | https/443   | http://192.168.49.2:32619 |     
|-----------|---------------|-------------|---------------------------|  
🏃  argocd-server 서비스의 터널을 시작하는 중      
|-----------|---------------|-------------|------------------------|     
| NAMESPACE |     NAME      | TARGET PORT |          URL           |     
|-----------|---------------|-------------|------------------------|       
| argocd    | argocd-server |             | http://127.0.0.1:50736 |  
|           |               |             | http://127.0.0.1:50737 |  
|-----------|---------------|-------------|------------------------|  
[argocd argocd-server  http://127.0.0.1:50736  
http://127.0.0.1:50737]  
❗  Because you are using a Docker driver on darwin, the terminal needs to be open to run it.**  
터널링된 정보와 함께 접속할수있는 url을 알려주는데 ‘http://127.0.0.1:50737’ 로 접속하면 웹UI에 접근 가능 하다. 미니쿠버는 보안상 이렇게 터널링을 꼭해주어야 한다고 하는데, 풀수 있는 방법도 있다.  필자는 굳이 풀지 않고 터널링을 이용해서 테스트 하는것을 추천한다.   



아이디와 비밀번호를 넣고 로그인한다.  

해당 화면은 +NEW APP 을 통해 Argo CD를 사용한 샘플이다.
