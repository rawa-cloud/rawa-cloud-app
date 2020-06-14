echo bulid dist
rm -rf dist
rm -rf dist.zip

mkdir dist
mkdir dist/images


echo save rawa-mysql ...
docker save -o ./dist/images/rawa-mysql.tar rawa/mysql

echo save rawa-app ...
docker save -o ./dist/images/rawa-app.tar rawa/app

echo save rawa-web ...
docker save -o ./dist/images/rawa-web.tar rawa/web

echo save rawa-mweb ...
docker save -o ./dist/images/rawa-mweb.tar rawa/mweb


echo save rawa-office ...
docker save -o ./dist/images/rawa-office.tar rawa/office

cp -rvf bin dist/bin
cp -rvf data dist/data
cp -rvf 安装说明.txt dist/安装说明.txt

zip -r -9 dist.zip dist/*