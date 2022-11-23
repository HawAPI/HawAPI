FROM postgres:alpine

ADD /postgres/migration/schema.sql /docker-entrypoint-initdb.d
ADD /postgres/migration/data.sql /docker-entrypoint-initdb.d

RUN chmod a+r /docker-entrypoint-initdb.d/*