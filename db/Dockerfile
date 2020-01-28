# This Dockerfile adds a startup script to PostgreSQL for creating the users

FROM postgres
WORKDIR /docker-entrypoint-initdb.d
COPY ./create-databases.sh ./
RUN chown postgres ./create-databases.sh
