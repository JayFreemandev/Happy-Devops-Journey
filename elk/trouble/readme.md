## Failed to create new OS thread 문제
- 문제
Pod 실행 후 바로  [Failed to create new OS thread (have 2 already; errno=22)](https://stackoverflow.com/questions/66349391/failed-to-create-new-os-thread-have-2-already-errno-22) 문제 발생하고 runtime 로그가 찍히는 경우

- 해결
docker config apple silicon m1 설정 후 쿠베 재시작  
![image](https://github.com/JayFreemandev/Happy-Devops-Journey/assets/72185011/40e862a6-931e-4535-8564-99868a55d1ce)


## kibana ingress 설정 후 timeout 문제
- 문제
ingress yaml 적용 이후 도메인으로 접속시 ping, nslookup, curl 모두 타임아웃과 응답없음
하지만 kubectl port-forward podname -n namepsace port:port 설정하고 localhost 접속시 동작

- 해결
Enabling the ingress addon on Mac shows that the ingress will be available on 127.0.0.1.
[Support Ingress on MacOS, driver docker](https://github.com/kubernetes/minikube/pull/12089

So you only need to add the following line to your /etc/hosts file.

```yaml
127.0.0.1  dashboard.com

```

Create tunnel (it will ask your sudo password) tunner 실행시켜야 적용된다.

```yaml
minikube tunnel
```

Then you can verify that the Ingress controller is directing traffic:

```yaml
curl dashboard.com
```

(also I used this Ingress)

```bash
kubectl apply -f - << EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: dashboard-ingress
  namespace: kubernetes-dashboard
spec:
  rules:
  - host: dashboard.com
    http:
      paths:
        - pathType: Prefix
          path: "/"
          backend:
            service:
              name: kubernetes-dashboard
              port:
                number: 80
```

만약 적용이 안된다면 mac dns flush 명령어 한번 날려주기
```
lookupd -flushcache
```
