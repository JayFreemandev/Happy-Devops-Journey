쿠버네티스 학습용 repo

### 자주 사용한 커맨드
kubectl cli  & minikube install
`brew install kubectl`
`brew install minikube`

미니쿠베 시작
```
minikube start --memory 8192 --cpus 4
```

미니쿠베 문제생긴 경우
```
minikube stop; minikube delete

home 에서 ls -al
rm -rf ./.minikube
rm -rf ./.kube
```

ELK 적용중 자주 사용된 command
- yaml or url 쿠버 적용하기
```
kubectl apply -f [파일명 또는 URL]
```

- 쿠버 리소스 목록 확인하기 (줄여서 커맨드 가능)
	- pods  - po
	- nodes - no
	- services - svc
```
kubectl get [리소스 타입]
```

- 리소스 삭제
```
kubectl delete [리소스]
```

- 로그 확인
```
kubectl logs -f [파드이름] 

namespace가 있다면 예를 들어 rx

kubectl logs -f rx_core_pod_name -n rx
