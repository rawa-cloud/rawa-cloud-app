FROM mysql:5.7

COPY setup.sh /mysql/setup.sh
COPY schema.sql /mysql/schema.sql
COPY privileges.sql /mysql/privileges.sql

CMD ["sh", "/mysql/setup.sh"]