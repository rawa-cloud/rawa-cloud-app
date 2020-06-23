echo load rawa-app ...
docker load -i ../images/rawa-app.tar

echo load rawa-web ...
docker load -i ../images/rawa-web.tar

echo load rawa-mweb ...
docker load -i ../images/rawa-mweb.tar

echo start...
docker-compose up -d

# sleep 90
# echo init data...
# docker-compose exec -T rawa-db mysql -u app_admin  --password=123456 rawa-cloud < ../data/init.sql

echo install success !!!

