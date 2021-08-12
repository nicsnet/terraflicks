FROM clojure

WORKDIR /build
COPY ./project.clj /build

RUN lein with-profile uberjar deps

COPY . /build
RUN lein uberjar

FROM java:8-alpine
RUN apk add --no-cache curl

COPY --from=0 /build/target/uberjar/terraflicks.jar /terraflicks/app.jar
COPY --from=0 /build/docker-start.sh /terraflicks/docker-start.sh

EXPOSE 3000

WORKDIR /terraflicks

CMD ["./docker-start.sh"]
