# package quarkus
mvn package -f ./roomix-backend

# build angular image
docker build -t roomix-frontend-image ./roomix-frontend

# run in docker
docker-compose up --build -d
