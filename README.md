
# Microservice micrometer log tracing

Microservice mimarisiyle yazılmış projelerde log takibi yapabilmek için kullanılan kütüphaneleri ve bazı monitoring tool larını anlatmaya çalıştım.


## Kullanılan Teknolojiler

* Java 17
* Springboot 3
* Feign Client
* Apache Kafka
* Swagger
* H2 Database
* Zipkin
* Grafana
* Mapstruct
* Micrometer


## Bilgisayarınızda Çalıştırmak için

Projeyi klonlayın

```bash
  https://github.com/ErayMert/microservice-micrometer-tracing.git
```

Proje dizinine gidin

```bash
  cd microservice-micrometer-tracing
```

* docker-compose.yml dosyasında kafka, grafana, zipkin, zookeper, tempo, loki ayarları yapılmıştır.

```yml
version: '3.8'

services:
  grafana:
    image: grafana/grafana-enterprise:latest
    volumes:
      - ./docker/grafana/datasources.yml:/etc/grafana/provisioning/datasources.yaml
    environment:
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_DISABLE_LOGIN_FORM=true
    ports:
      - "3000:3000"

  zipkin:
    image: openzipkin/zipkin:latest
    ports:
      - "9411:9411"

  zookeeper:
    image: docker.io/bitnami/zookeeper:3.8
    restart: always
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ALLOW_ANONYMOUS_LOGIN: yes

  kafka:
    image: wurstmeister/kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

  loki:
    image: grafana/loki:main
    command: [ "-config.file=/etc/loki/local-config.yaml" ]
    ports:
      - "3100:3100"

  tempo:
    image: grafana/tempo:2.2.4
    command: [ "--target=all", "--storage.trace.backend=local", "--storage.trace.local.path=/var/tempo", "--auth.enabled=false" ]
    ports:
      - "14250:14250"
      - "4317:4317"
    depends_on:
      - loki
```

Aşağıdaki komut ile bu tool ları çalıştırabiliriz

```docker
  docker-compose up -d
```

## Eklenen kütüphaneler

* Brave ise dağıtılmış sistemlerde izleme yapmayı sağlayan bir kütüphanedir. Özellikle microservice mimarileri gibi karmaşık sistemlerde, bir isteğin izini sürmek ve izlemek için kullanılır. İsteklerin birbirleriyle etkileşimlerini ve performanslarını izlemek için kullanılan bu kütüphane, ağ geçitlerinden geçen istekleri takip eder ve bu isteklerin nasıl yanıtlandığını görmenize olanak tanır.

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
```

* Bu bağımlılık, OpenFeign kullanan bir uygulamada metriklerin toplanmasını ve Micrometer aracılığıyla izlenmesini sağlar.

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-micrometer</artifactId>
</dependency>
```

* Zipkin, dağıtılmış sistemlerde izleme ve hata ayıklama sağlayan bir izleme sistemi ve sunucusudur. zipkin-reporter-brave bağımlılığı, Brave kütüphanesi aracılığıyla Zipkin'e rapor göndermek için gerekli olan reporter işlevselliğini sağlar. Bu sayede uygulamanızdaki izlemeyi Zipkin üzerinden gerçekleştirebilir ve uygulamanızın performansını ve işlevselliğini daha iyi analiz edebilirsiniz.

```xml
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```
* Grafana Labs tarafından geliştirilmiştir. loki-logback-appender bağımlılığı, Logback üzerinden günlük mesajlarını Loki'ye yönlendirmenize olanak tanır. Böylece uygulamanızın günlüklerini Loki'de saklayabilir ve Grafana ile görselleştirebilirsiniz.

```xml
<dependency>
    <groupId>com.github.loki4j</groupId>
    <artifactId>loki-logback-appender</artifactId>
    <version>1.3.2</version>
</dependency>
```

## Logback-spring.xml dosyası

* Ben logları grafana da loki datasource üzerinde takip ettiğim için bu dosyayı aşağıdaki gibi düzenledim.
Bu dosya her projede olmalı
* Burada label kısımları aslında loki üzerinde arama yapmanız sağlayan kısımlara denk geliyor.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <springProperty scope="context" name="appName" source="spring.application.name"/>

    <appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
        <http>
            <url>http://localhost:3100/loki/api/v1/push</url>
        </http>
        <format>
            <label>
                <pattern>app=${appName},host=${HOSTNAME},traceID=%X{traceId:-NONE},level=%level</pattern>
            </label>
            <message>
                <pattern>${FILE_LOG_PATTERN}</pattern>
            </message>
            <sortByTime>true</sortByTime>
        </format>
    </appender>

    <root level="INFO">
        <appender-ref ref="LOKI"/>
    </root>

</configuration>
```
### Grafana

* Grafanaya bağlanmak için <http://localhost:3000> adresini kullanıyoruz.
* Yukarıdaki configürasyonun loki datasource üzerindeki görünümü aşağıdaki gibidir.

![loki label](Images/loki_label.png)

### Zipkin
Zipkine bağlanmak için <http://localhost:9411> adresini kullanıyoruz.


### Kafka için micrometer configüsrasyonu

* Hem producer da hem consumer da 







  