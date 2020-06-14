#!/bin/bash

echo "check Docker......"
docker -v
if [ $? -eq  0 ]; then
    echo "Docker has installed!"
else
  echo "install docker"
    curl -sSL https://get.daocloud.io/docker | sh
    echo "install docker...completed!"
fi

systemctl start docker

docker -v

echo "check Docker Compose......"
docker-compose -v
if [ $? -eq  0 ]; then
    echo "Docker Compose has installed!"
else
  echo "install docker-compose"
    curl -L https://get.daocloud.io/docker/compose/releases/download/1.22.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    echo "install docker compose...completed!"
fi

docker-compose -v


echo load rawa-mysql ...
docker load -i ../images/rawa-mysql.tar

echo load rawa-app ...
docker load -i ../images/rawa-app.tar

echo load rawa-web ...
docker load -i ../images/rawa-web.tar

echo load rawa-mweb ...
docker load -i ../images/rawa-mweb.tar


echo load rawa-office ...
docker load -i ../images/rawa-office.tar

mkdir ../rawa-data
mkdir ../rawa-data/mysql
mkdir ../rawa-data/mysql/data
chown -R 999 ../rawa-data/mysql/data

echo start...
docker-compose up -d

# sleep 90
# echo init data...
# docker-compose exec -T rawa-db mysql -u app_admin  --password=123456 rawa-cloud < ../data/init.sql

echo install success !!!

