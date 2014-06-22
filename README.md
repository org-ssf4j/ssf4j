# SSF4J

**Simple Serialization Facade for Java**

SSF4J is a wrapper around other serialization frameworks.  It doesn't do any serialization itself; instead, it allows you to write code that takes advantage of serialization without having to know the details of the serialization engine in question.  It is written in the spirit of the (unrelated) project [http://www.slf4j.org SLF4J], which provides a facade for logging rather than serialization.

SSF4J is intended to be used as a supporting library for library development.  In your library you rely on SSF4J for serialization, so that users of your library can specify the serialization mechanism appropriate for their use case.  For example, if your library needs to buffer data to disk, it may not be appropriate to choose a specific serialization mechanism.  Instead, you could use SSF4J and let users of your library provide the appropriate serialization adapter, such as `ssf4j-avro-json` or `ssf4j-kryo`.

To use SSF4J in your library, include the `ssf4j-api` artifact in your `pom.xml`.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-api</artifactId>
     <version>0.2.4</version>
    </dependency>

## Usage

Serializing an object of type Foo:

    Serialization serde = Serializations.get(Serializations.KRYO);
    Serializer<Foo> sfoo = serde.newSerializer(out, Foo.class);
    sfoo.write(someFoo);

Deserializing an object of type Foo:

    Serialization serde = Serializations.get(Serializations.KRYO);
    Deserializer<Foo> dfoo = serde.newDeserializer(in, Foo.class);
    someFoo = dfoo.read();


## Supported Serialization Mechanisms

### JDK

JDK serialization is the JRE's built-in serialization mechanism.  It can only serialize objects that implement java.io.Serializable

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-jdk</artifactId>
     <version>0.2.4</version>
    </dependency>

### [Kryo](https://github.com/EsotericSoftware/kryo)

Kryo can serialize pretty much anything, and fast.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-kryo</artifactId>
     <version>0.2.4</version>
    </dependency>

### [avro](http://avro.apache.org) (binary/json output)

Avro can only serialize objects generated from avro schemas, enums, and primitive types.

For binary encoding:

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-avro-binary</artifactId>
     <version>0.2.4</version>
    </dependency>

For JSON encoding:

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-avro-json</artifactId>
     <version>0.2.4</version>
    </dependency>

### [Jackson](http://wiki.fasterxml.com/JacksonHome)

Jackson can serialize most things, but fails on a few cases.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-jackson</artifactId>
     <version>0.2.4</version>
    </dependency>

### [Purple Jrank](http://www.purplejrank.org)

Purple Jrank can serialize anything the JDK can serialize.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-purplejrank</artifactId>
     <version>0.2.4</version>
    </dependency>

### [XStream](http://xstream.codehaus.org)

XStream can serialize pretty much anything.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-xstream</artifactId>
     <version>0.2.4</version>
    </dependency>
