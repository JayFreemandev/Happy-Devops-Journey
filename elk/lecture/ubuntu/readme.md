**docker command**  
docker pull ubuntu

docker create -i -t --name ubuntu_server_1 ubuntu

docker ps -a

docker start ubuntu_server_1

docker attach ubuntu_server_1

apt-get update
apt-get upgrade
