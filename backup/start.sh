#备份 mysql
echo backup mysql...
ls_date=`date +%Y-%m-%d_%H-%M-%S`
docker exec rawa-mysql /usr/bin/mysqldump -u app_admin  --password=123456 rawa-cloud > ./data/rawa-mysql_backup_${ls_date}.sql

echo backup file
docker cp rawa-app:/nas ./file/rawa-file_backup_${ls_date}

echo complete!!!

