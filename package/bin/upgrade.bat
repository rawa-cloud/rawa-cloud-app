echo load rawa-app ...
docker load -i ../images/rawa-app.tar

echo load rawa-web ...
docker load -i ../images/rawa-web.tar

echo load rawa-mweb ...
docker load -i ../images/rawa-mweb.tar

echo start...
docker-compose up -d

echo install success !!!

pause
