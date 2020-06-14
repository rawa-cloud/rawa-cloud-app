read -r -p "Are you sure uninstall app, the data and file will be lose forever? [Y/n] " input

if [[ "$input" != "Y" ]];then
	exit;
fi

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

