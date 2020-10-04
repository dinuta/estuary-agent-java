FROM azul/zulu-openjdk:11

ENV APP_DIR /app
ENV PORT 8080
ENV HTTP_AUTH_TOKEN None
ENV COMMAND_TIMEOUT 1200

RUN mkdir $APP_DIR
WORKDIR $APP_DIR

COPY target/agent-4.0.9-SNAPSHOT-exec.jar $APP_DIR

CMD ["java", "-jar", "/app/agent-4.0.9-SNAPSHOT-exec.jar"]
