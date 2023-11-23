도커이미지 빌드 명령어

docker build -t jayfreemandev/producer:1.1 -f Dockerfile .

docker run -dit -p 8080:8080 --name flask-hello  jayfreemandev/flask-hello:1.0

docker logs flask-hello

Docker login

docker tag quanttree-rx-producer jayfreemandev/producer:1.1

docker push jayfreemandev/producer:1.1

minikube service flask-hello -n hello 