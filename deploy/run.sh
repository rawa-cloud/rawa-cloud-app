
echo '启动mysql服务'

docker run -p 9001:3306 --name rawa-mysql rawa/mysql


echo '启动app'

docker run -p 9002:8080 --name rawa-app rawa/app


echo '启动office服务'
docker run --rm -p 9004:80 --name rawa-office rawa/office
#
#docker run --rm -p 3000:3000 --name rawa-office thecodingmachine/gotenberg:6

echo '启动web'

docker run -p 9003:8000 --name rawa-web rawa/web