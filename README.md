## Introduction
This repository demonstrates a simple [Eclipse Vert.x](http://vertx.io)  cluster, built on Hazelcast, that comprises
two members, here called "nodes":
1. An API node that listens for web request on TCP/8080
1. A JPA node that manages an embedded H2 database 

Nodes communicate via Vert.x's distributed event bus. Excepting this distributed nature, the example
is functionally equivalent to [VertXJpa](https://github.com/jasonhallford/VertXJpa).

## Requirements
To build this example, you will need the following:
1. A version of Git capable of cloning this repository from Git Hub
1. Apache Maven v3.5 or greater
1. The latest patch release of OpenJDK 11 (build produced by the [AdoptOpenJDK Project](https://adoptopenjdk.net/) work
nicely)

## Building the Project
You may build the example in one of two ways, as a JAR or a Docker image. 
### Maven Build
You may build JAR from source using [Apache Maven](http://maven.apache.org). Assuming a version >= 3.5.0 you can build it  by
executing `mvn package` at the command line (assuming `mvn` is in the path, of course). As this is a multi-module
project, it produces two separate sets of artifacts:
* JARs named __jpa-node-1.2.jar__ and __jpa-node-1.2-fat.jar__ in <span style="font-family: monospace;">/jpa-node/target</span> 
for the JPA node
* JARs named __api-node-1.2.jar__ and __api-node-1.2-fat.jar__ in <span style="font-family: monospace;">/api-node/target</span>
for the API node

### Building as a Docker Image
You may use the included Dockerfile to create a deployable images, one for each node. To do this, run `docker build` from
the _parent_ directory so that the entire source tree is included in the build context. For example, to build the
API image you would run the following:
```shell script
../vertx-jpa-cluster$ docker build -t vertx-api-node:1.2 --file ./api-node/Dockerfile .
``` 
You must do this for _both_ nodes.

You may create a container using the following command: 
```shell script
../vertx-jpa-cluster$ docker run -p 8080:8080 --name vertx-api-node vertx-api-node:1.2
```  

You will be able to connect to the API node at http://localhost:8080. As currently configured, the two containers must
run in the same Podman or Kubernetes pod; the default bridge is sufficient for Docker.

## Configuring the Example
The example includes a default configuration that creates 
* One API verticle bound to port TCP/8080
* One JPA verticle

The TCP port and number of JPA verticles may be customized by setting the following properties either as OS environment
variables or JRE system proprties (i.e. "-D" properties). The latter have a higher priority than the former.
| Property          | Notes                                                        |
| ----------------- | ------------------------------------------------------------ |
| bind-port     | An integer value that sets the API verticle's TCP bind port. |
| jpa-verticle-count | An integer value that speicified the number of JPA verticles to create; defaults to 2. | 

## Running the Example
Unless configured otherwise, the sample application presents a basic RESTful API on port TCP/8080 for a generic "Person" 
resource. Encoded as JSON, a Person looks like this:

```json
{
	"name" : "John Smith",
	"age" : "35"
}
```

The API supports three methods:
1. You may POST a JSON document in the format above to http://localhost:8080/api/people to create a new person. The new 
entity may be found at the address provided in the response's `location` header
1. You may list all people by sending a GET to http://localhost:8080/api/people
1. You may list a specific person by sending a GET to http://localhost:8080/api/people/[person id], where [person id] is
a value returned by a previous POST

I recommend [Postman](https://www.postman.com/) to exercise the example, although any tool capable of generating the
necessary HTTP requests will suffice.
