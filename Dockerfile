FROM sbtscala/scala-sbt:eclipse-temurin-alpine-21.0.2_1.10.0_3.3.1
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6

WORKDIR /blackjack
ADD . /blackjack
CMD ["sbt", "run"]