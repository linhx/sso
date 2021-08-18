FROM adoptopenjdk/openjdk11:alpine
# fix bug NPE fontconfig
RUN apk add --no-cache fontconfig ttf-dejavu
ENV LD_LIBRARY_PATH /usr/lib
ARG USER=linhx
ARG GROUP=linhx.com
RUN set -x && \
    addgroup -g 1000 ${GROUP} && \
    adduser -u 1000 -D -G ${GROUP} ${USER}
USER ${USER}:${GROUP}
