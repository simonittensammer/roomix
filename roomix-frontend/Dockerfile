FROM node:13-alpine as build
WORKDIR /app
COPY package.json /app/
RUN npm install -g ionic
RUN npm install
COPY ./ /app/
RUN npm run-script build
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/
COPY --from=build /app/www/ /usr/share/nginx/html/
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
