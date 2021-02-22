<h1 align="center"><img src="./docs/images/banner_agent.png" alt="Estuary Agent"></h1>  

The agent is written in Java (SpringBoot), and it executes low-level commands.

It enables any use case which implies system commands:

- Controlling and configuring the machines (via REST API)
- Exposing CLI applications via REST API
- Testing support by enabling SUT control and automation framework control
- IoT
- Home control integrations

It supports command execution having several modes:

- Commands executed sequentially
- Commands executed in parallel
- Commands executed in background
- Commands executed synchronously

With the help of the agent the user can also do IO operations:

- File upload and download (binary / text)
- Folder download (as zip archive)

This code acts both as a microservice as well as a library:

a) Standalone microservice jar with the
extension: [exec.jar](https://search.maven.org/artifact/com.github.dinuta.estuary/agent/4.1.1/jar)

```bash
java -jar agent-4.1.1-exec.jar
```

b) Library as a Maven dependency:

```xml

<dependency>
    <groupId>com.github.dinuta.estuary</groupId>
    <artifactId>agent</artifactId>
    <version>4.1.1</version>
</dependency>
```

Integration of the library in a new custom microservice is
shown [in wiki](https://github.com/dinuta/estuary-agent-java/wiki)

## Artifact

![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.github.dinuta.estuary/agent/4.1.1)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.dinuta.estuary/agent?server=https%3A%2F%2Foss.sonatype.org)

## Build status

[![CircleCI](https://circleci.com/gh/dinuta/estuary-agent-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/dinuta/estuary-agent-java)

## Code quality

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/3a410087fa10428e89e925134c4e3988)](https://www.codacy.com/gh/dinuta/estuary-agent-java/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dinuta/estuary-agent-java&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/0f8230850df65ed9840f/maintainability)](https://codeclimate.com/github/dinuta/estuary-agent-java/maintainability)

## Postman collection

[API collection](https://documenter.getpostman.com/view/2360061/SVYrrdGe)

## Commands in background

For /commanddetached endpoint **runcmd** binary is needed. The binary implements the same object as **/command**
endpoint as per example found bellow.    
Please download the execs from here:

- [Windows](https://estuary-agent-go.s3.eu-central-1.amazonaws.com/4.1.0/runcmd.exe)
- [Linux](https://estuary-agent-go.s3.eu-central-1.amazonaws.com/4.1.0/runcmd-linux)
- [Alpine](https://estuary-agent-go.s3.eu-central-1.amazonaws.com/4.1.0/runcmd-alpine)

Place the platform-specific binary under the same folder from where the service started. The exec must have its name
as **runcmd**. E.g runcmd.exe (Windows) / runcmd (Linux)

## Eureka client registration

Set the following env vars:

- APP_IP -> the ip which this service binds to
- PORT -> the port which this service binds to

Example:

 ```bash
export APP_IP=192.168.0.4
export PORT=8081
java -jar \
-Deureka.client.serviceUrl.defaultZone=http://192.168.0.100:8080/eureka/v2 \
-Deureka.client.enabled=true agent-4.1.1-SNAPSHOT-exec.jar 
```

## Fluentd logging

- FLUENTD_IP_PORT -> This env var sets the fluentd ip:port connection. Example: localhost:24224

## Authentication

### Method 1 - Spring security

- HTTP_AUTH_USER
- HTTP_AUTH_PASSWORD

These env vars will be matched against basic authentication from your HttpClient.  
After user auth, set the received cookie (JSESSIONID) to communicate further with the agent.  
The same settings can be set through application properties: **app.user** & **app.password**.  
The env vars precedence is higher than the one set through the application properties.

### Method 2 - Token auth - No spring-security

- HTTP_AUTH_TOKEN -> This env var sets the auth token for the service. Will be matched with the header 'Token'  
  Note: The profile to be used is 'test'.

## Command timeout

- COMMAND_TIMEOUT -> This env var sets the command timeout for the system commands. Default is **1800** seconds.

## Enable HTTPS

Set **HTTPS_ENABLE** env var option to *true* or *false*.    
Set the certificate path (is relative!) with **HTTPS_KEYSTORE** and **HTTPS_KEYSTORE_PASSWORD** env variables. E.g.
HTTPS_KEYSTORE=file:https/keystore.p12  
If you do not set cert and keystore password env vars, it uses the ones from default *application.properties* in the
resource folder.  
!Obs: Change port number with env var **PORT** to 8443.

## Environment variables injection

User defined environment variables will be stored in a 'virtual' environment. The extra env vars will be used by the
process that executes system commands.  
There are two ways to inject user defined environment variables.

- call POST on **/env** endpoint. The body will contain the env vars in JSON format. E.g. {"FOO1":"BAR1"}
- create an **environment.properties** file with the extra env vars needed and place it in the same path as the JAR.
  Example in this repo.

*! All environment variables described above can also be set using **environment.properties**. However, the vars set
through **application.yml** can't be set: PORT, APP_IP, EUREKA_SERVER.*

## Example output

curl -X POST -d 'ls -lrt' http://localhost:8080/command

```json
{
  "code": 1000,
  "message": "Success",
  "description": {
    "finished": true,
    "started": false,
    "startedat": "2020-08-15 19:38:16.138962",
    "finishedat": "2020-08-15 19:38:16.151067",
    "duration": 0.012,
    "pid": 2315,
    "id": "none",
    "commands": {
      "ls -lrt": {
        "status": "finished",
        "details": {
          "out": "total 371436\n-rwxr-xr-x 1 dinuta qa  13258464 Jun 24 09:25 main-linux\ndrwxr-xr-x 4 dinuta qa        40 Jul  1 11:42 tmp\n-rw-r--r-- 1 dinuta qa  77707265 Jul 25 19:38 testrunner-linux.zip\n-rw------- 1 dinuta qa   4911271 Aug 14 10:00 nohup.out\n",
          "err": "",
          "code": 0,
          "pid": 6803,
          "args": [
            "/bin/sh",
            "-c",
            "ls -lrt"
          ]
        },
        "startedat": "2020-08-15 19:38:16.138970",
        "finishedat": "2020-08-15 19:38:16.150976",
        "duration": 0.012
      }
    }
  },
  "timestamp": "2020-08-15 19:38:16.151113",
  "path": "/command?",
  "name": "estuary-agent",
  "version": "4.0.8"
}
```

## Overview

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)

Start your server as an simple java application

You can view the api documentation in swagger-ui by pointing to  
http://localhost:8080/

Change default port value in application.properties

## Maven dependency && settings.xml

Get this dependency:

```xml

<dependency>
  <groupId>com.github.dinuta.estuary</groupId>
  <artifactId>agent</artifactId>
  <version>4.1.1</version>
</dependency>
```

## Maven devendency snapshot

```xml

<dependency>
    <groupId>com.github.dinuta.estuary</groupId>
    <artifactId>agent</artifactId>
    <version>4.1.2-SNAPSHOT</version>
</dependency>
```

For using a snapshot version, set the oss.sonatype.org repo in settings.xml:

```xml

<repository>
    <id>snaphosts4</id>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <releases>
        <enabled>false</enabled>
        <updatePolicy>always</updatePolicy>
    </releases>
    <name>all-external8</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
</repository>
```

Support
project: <a href="https://paypal.me/catalindinuta?locale.x=en_US"><img src="https://lh3.googleusercontent.com/Y2_nyEd0zJftXnlhQrWoweEvAy4RzbpDah_65JGQDKo9zCcBxHVpajYgXWFZcXdKS_o=s180-rw" height="40" width="40" align="center"></a>   
