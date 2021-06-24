FROM adoptopenjdk/openjdk11:alpine
ARG USER=linhx
ARG GROUP=linhx.com
RUN set -x && \
    addgroup -g 1000 ${GROUP} && \
    adduser -u 1000 -D -G ${GROUP} ${USER}
USER ${USER}:${GROUP}
