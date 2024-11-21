FROM node:19.4-bullseye AS build

WORKDIR /usr/src/app

COPY package*.json ./

RUN --mount=type=cache,target=/usr/src/app/.npm \
  npm set cache /usr/src/app/.npm && \
  npm ci --prefer-offline

COPY . .

RUN npm run build

FROM nginxinc/nginx-unprivileged:1.23-alpine-perl AS production

USER root

RUN apk add --no-cache ca-certificates \
    && update-ca-certificates \
    && wget -O /etc/ssl/certs/ca-certificates.crt https://curl.se/ca/cacert.pem

USER 101

COPY --link default.conf.template /etc/nginx/conf.d/default.conf.template
COPY --from=build /usr/src/app/dist /usr/share/nginx/html

EXPOSE $PORT

CMD /bin/sh -c "envsubst '\$PORT' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf" && nginx -g 'daemon off;'