@Echo Off
Echo Are you sure uninstall app, the data and file will be lose forever?
Echo.
Set /p var=please input (Y/N):
If /i %var%==Y (goto run) else (Exit)

:run

docker-compose down

echo remove rawa-mysql ...
docker rmi rawa/mysql

echo remove rawa-app ...
docker rmi rawa/app

echo remove rawa-web ...
docker rmi rawa/web

echo remove rawa-mweb ...
docker rmi rawa/mweb

echo remove rawa-office ...
docker rmi rawa/office


echo uninstall success !!!

pause

