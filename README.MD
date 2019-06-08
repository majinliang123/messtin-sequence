# MESSTIN Sequence

### 用途
用来给分布式系统生成唯一性id，保证了线程安全以及可以在多个服务中同时获取id

### 例子
```java
public void test() {
    Sequence sequence = new Sequence(dataSource, "abc");
    for (int i = 0; i < 500; i++) {
        logger.info("Current id is {}.", sequence.nextId());
    }
}
```
你也可以在Sample.class中看到具体使用方法

你需要在数据中创建存储数据的表，具体的sql在目录doc/sequence.sql中。

### 使用的技术
在使用的过程中，你需要一个数据库做支持。
在本项目中的测试用例部分，使用H2内存数据库进行数据的存储。当然，你也可以通过更改源码来使用redis来用作数据存储。