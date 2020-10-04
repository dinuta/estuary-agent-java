<h1 align="center"><img src="./docs/images/banner_agent.png" alt="Estuary Agent"></h1>  

Support project: <a href="https://paypal.me/catalindinuta?locale.x=en_US"><img src="https://lh3.googleusercontent.com/Y2_nyEd0zJftXnlhQrWoweEvAy4RzbpDah_65JGQDKo9zCcBxHVpajYgXWFZcXdKS_o=s180-rw" height="40" width="40" align="center"></a>   

# This project has moved to: https://github.com/estuaryoss/estuary-agent-java

# About
Agent written in Java (SpringBoot) as part of **estuary** stack. 

The advantage of this implementation is that java libraries can be integrated within, rather than executing the logic through cli commands pointing to a main class in a jar, as per the original python implementation.

## Artifact
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/com.github.dinuta.estuary/agent/4.0.8)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.dinuta.estuary/agent?server=https%3A%2F%2Foss.sonatype.org)

## Build status
[![CircleCI](https://circleci.com/gh/estuaryoss/estuary-agent-java.svg?style=svg&circle-token=2036f4d0e07fadce8101e00e790970fcfb43e03f)](https://circleci.com/gh/dinuta/estuary-agent-java)

## Code quality
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/20bec8d5bf1b4197b6447b9f926c32ad)](https://www.codacy.com/gh/estuaryoss/estuary-agent-java?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=estuaryoss/estuary-agent-java&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/5600efff46a8f385a221/maintainability)](https://codeclimate.com/github/estuaryoss/estuary-agent-java/maintainability)
## Eureka client registration
Set the following env vars:  
-   APP_IP -> the ip which this service binds to
-   PORT  -> the port which this service binds to

Example:  
 ```bash
export APP_IP=192.168.0.4
export PORT=8081
java -jar \
-Deureka.client.serviceUrl.defaultZone=http://192.168.0.100:8080/eureka/v2 \
-Deureka.client.enabled=true agent-4.0.9-SNAPSHOT-exec.jar 
```

## Fluentd logging
-   FLUENTD_IP_PORT  -> This env var sets the fluentd ip:port connection. Example: localhost:24224  

## Token Authentication
-   HTTP_AUTH_TOKEN -> This env var sets the auth token for the service. Will be matched with the header 'Token'

## Command timeout
-   COMMAND_TIMEOUT -> This env var sets the command timeout for the system commands. Default is **1800** seconds.  

## Environment variables injection
User defined environment variables will be stored in a 'virtual' environment. The extra env vars will be used by the process that executes system commands.  
There are two ways to inject user defined environment variables.    
-   call POST on **/env** endpoint. The body will contain the env vars in JSON format. E.g. {"FOO1":"BAR1"}  
-   create an **environment.properties** file with the extra env vars needed and place it in the same path as the JAR. Example in this repo.  

*! All environment variables described above can also be set using **environment.properties**. However, the vars set through **application.yml** can't be set: PORT, APP_IP, EUREKA_SERVER.*

## More information
This service acts with small differences as the original [python implementation](https://github.com/dinuta/estuary-agent).  
All the documentation should be matched, minus some differences in terms how this service registers to eureka.

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
    <version>4.0.8</version>
</dependency>
```
## Maven devendency snapshot
```xml
<dependency>
    <groupId>com.github.dinuta.estuary</groupId>
    <artifactId>agent</artifactId>
    <version>4.0.8-SNAPSHOT</version>
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
