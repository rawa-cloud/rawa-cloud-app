use mysql;
select host, user from user;

create user app_admin identified by '123456';

grant all on `rawa-cloud`.* to app_admin@'%' identified by '123456' with grant option;

set global time_zone = '+8:00';

flush privileges;