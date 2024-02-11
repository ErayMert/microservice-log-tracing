
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

## Mimari diagram

![architecture](Images/arch.png)

## Bilgisayarınızda Çalıştırmak için

Projeyi klonlayın

```bash
https://github.com/ErayMert/microservice-log-tracing.git
```

Terminal ile proje dizinine gidin

```bash
  cd microservice-log-tracing
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

## application.yml dosyasındaki configürasyonlar

* Aşağıdaki ayar izleme verisinin tamamının toplanacağını belirtir. Yani, her bir isteğin izlenmesi için %100 olasılıkla izlenir ve kaydedilir.
``` yml
management:
  tracing:
    sampling:
      probability: 1.0
```
* Bu ayar ise zipkin configürasyonunu sağlar ve spring boot 3 de bu şekilde uzak sunucu adresine bağlanabilmesini sağlıyoruz
* Default olarak "http://localhost:9411/api/v2/spans" bu endpoint ayarlı yazmamıza gerek yok fakat başla bir sunucu için yazmamız gerekir

``` yml
management:
  zipkin:
    tracing:
      endpoint: "http://localhost:9411/api/v2/spans"
```
* Aşağıdaki config ile çıktı olarak traceId ve spanId nin nasıl yansıyacağını belirtir.

``` yml
logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
```

* Console daki çıktı aşağıdaki gibidir. 

![trace_span](Images/trace_span.png)

## Kafka için micrometer configüsrasyonu

* Producer için  
    * `kafkaTemplate.setMicrometerEnabled(true);`
    * `kafkaTemplate.setObservationEnabled(true);` ayarı yapılır.
  
```java
@RequiredArgsConstructor
@Configuration
public class ProducerConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getAddress());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {

        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setMicrometerEnabled(true);
        kafkaTemplate.setObservationEnabled(true);

        return kafkaTemplate;
    }
}
```

* Consumer config class ında ise
    * `factory.getContainerProperties().setObservationEnabled(true);`
    * `factory.getContainerProperties().setMicrometerEnabled(true);`
    * `factory.getContainerProperties().setLogContainerConfig(true);`
    * `factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.INFO);` ayarı yapılır.

```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.getContainerProperties().setObservationEnabled(true);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.getContainerProperties().setLogContainerConfig(true);
        factory.getContainerProperties().setCommitLogLevel(LogIfLevelEnabled.Level.INFO);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        return factory;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getAddress());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

}
```

## Uygulama üzerinde deneme yapalım

* Swagger üzerinden ilk olarak bir customer ve bir product yaratalım.

![create_customer](Images/create_customer.png)
![create_product](Images/product_create.png)

* Daha sonra customer bir order create edecek ve customer service burada kafka üzerinden order servis ile haberleşerek bir order oluşturur.

![create_order](Images/create_order.png)

### Grafana ile log tracing

* Grafanaya bağlanmak için <http://localhost:3000> adresini kullanıyoruz.
* logback-spring.xml configürasyonun loki datasource üzerindeki görünümü aşağıdaki gibidir.

![loki label](Images/loki_label.png)

* Yukarıda denediğimiz order create işlemine grafana üzerinde gözlemleyelim.
  * İlk olarak customer servisinden bir traceId alacağız ve daha sonra bu traceId ile loki üzerinde arama yapıp bu traceId ye ait tüm logları gözlemleyeceğiz.

![create_order_log](Images/create_order_log.png)
  * Buradan alacağımız traceId ile arama yapalım ve bu traceId ye sahip tüm servis loglarını görmüş olacağız.

![create_order_trace_log](Images/grafana_trace_log.png)

### Zipkin
* Zipkine bağlanmak için <http://localhost:9411> adresini kullanıyoruz. 

* Yukarıdaki order create isteğini zipkinde gözlemlersek aşağıdaki gibi bir çıktı verir

![zipkin_trace_log](Images/zipkin_trace_log.png)










  