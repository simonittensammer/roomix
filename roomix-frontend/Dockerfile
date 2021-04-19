FROM node:13-alpine as build
WORKDIR /app
COPY package.json /app/
COPY ./nginx.conf /app/
RUN npm install -g ionic
RUN npm install
COPY ./ /app/
RUN npm run-script build
FROM nginx:alpine
COPY /app/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/www/ /usr/share/nginx/html/

