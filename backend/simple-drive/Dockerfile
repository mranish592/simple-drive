FROM maven:3.9.9-eclipse-temurin-21
COPY . /app
WORKDIR /app
RUN mvn clean install -T 10

FROM openjdk:21-jdk
RUN microdnf install unzip -y
COPY --from=0 /app/target/simple-drive-1.0-SNAPSHOT.zip /
# COPY ./target/simple-drive-1.0-SNAPSHOT.zip /

WORKDIR /
RUN unzip simple-drive-1.0-SNAPSHOT.zip
WORKDIR /simple-drive-1.0-SNAPSHOT
RUN chmod +x entrypoint.sh startSimpleDrive.sh
RUN mkdir -p /local_data/filstore
CMD ["./entrypoint.sh"]
# CMD ["tail", "-f", "/dev/null"]