<a href="https://github.com/jakimli/pandaria">
  <img src="doc/pandaria.png?raw=true" width="100px">
</a>

Pandaria
========

简介
---
Pandaria是基于Cucumber JVM, 尝试简化HTTP API自动化测试的DSL。Pandaria与Cucumber完全兼容。使用Pandaria不要求编码能力。


例子
---

你可以调用API并验证返回结果:

```gherkin
* uri: http://localhost:10080/users/me
* send: GET
* status: 200
* verify: '$.username'='jakim'
* verify: '$.age'=18
```

你也可以查询数据库，并验证查询结果:

```gherkin
* query:
"""
SELECT NAME, AGE FROM USERS
"""
* verify: '$[0].name'='jakim'
* verify: '$[0].age'=18
```

或者将查询条件放到文件中:

```gherkin
* query: select.sql
* verify: '$[0].name'='jakim'
* verify: '$[0].age'=18
```
[支持Mongo DB](doc/usage.md#mongodb-operations)

等待并重试:

```gherkin
* wait: 1000ms times: 3
* uri: /sequence
* send: GET
* response body:
"""
3
"""
```
上述代码发送GET请求到`/sequence`并期望返回结果报文等于文本3, 如果失败，则等待1000毫秒并重试，直到成功通过，或者超过最大重试次数3, 然后失败。

```gherkin
* wait: 1000ms times: 3
* query: select.sql
* verify: '$[0].name'='jakim'
* verify: '$[0].age'=18
```

验证返回结果是否遵循Json schema:
```gherkin
* uri: /products/1
* send: get
* verify: '$' conform to:
"""
{
"$schema": "http://json-schema.org/draft-07/schema#",
"$id": "http://example.com/product.schema.json",
"title": "Product",
"description": "A product in the catalog",
"type": "object"
}
"""

* verify: '$' conform to: schema/product.schema.json
```

[更多用法](doc/usage.md)

快速开始
-------

如果你不需要验证数据库或者mongodb, 删除`pandaria-db`或`pandaria-mongo`

### Gradle
```groovy
dependencies {
    testCompile(
            "io.cucumber:cucumber-junit:4.0.0",
            'com.github.jakimli.pandaria:pandaria-core:0.2.4',
            'com.github.jakimli.pandaria:pandaria-db:0.2.4',
            'com.github.jakimli.pandaria:pandaria-mongo:0.2.4'
    )
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>com.github.jakimli.pandaria</groupId>
    <artifactId>pandaria-core</artifactId>
    <version>0.2.4</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.github.jakimli.pandaria</groupId>
    <artifactId>pandaria-db</artifactId>
    <version>0.2.4</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>com.github.jakimli.pandaria</groupId>
    <artifactId>pandaria-mongo</artifactId>
    <version>0.2.4</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

如果你需要验证数据库，则必须在`build.gradle`或者`pom.xml`中添加数据库驱动依赖，并在`application.properties`中添加数据库连接信息

application.properties
```
spring.datasource.url=jdbc:mysql://localhost:3307/pandaria?useSSL=false&allowMultiQueries=true
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

如果你需要与Mongo db交互, 添加如下连接配置
```
mongo.db.name=test
mongo.db.connection=mongodb://root:password@localhost:27017
```

如果你使用的JUnit, 添加如下的Java类
```java
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {
        "pretty",
        "junit:build/cucumber-reports/cucumber.xml",
        "json:build/cucumber-reports/cucumber.json",
        "html:build/cucumber-reports",
},
        features = "classpath:features/",
        glue = {"com.github.jakimli.pandaria"},
        tags = "not @ignore")
public class RunCucumberTest {
}
```
**确保cucumber glue包含`com.github.jakimli.pandaria`**

上述代码配置了json, xml和html格式的报告. 同时忽略有标记了@ignore的场景.

现在就可以写第一个场景了
```gherkin
Feature: hello world
  This is the first feature for pandaria

  Scenario: hello world
    * uri: https://github.com
    * send: GET
    * status: 200
```
