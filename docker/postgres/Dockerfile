FROM postgres:alpine

ADD /postgres/init/schema.sql /docker-entrypoint-initdb.d

RUN chmod a+r /docker-entrypoint-initdb.d/*