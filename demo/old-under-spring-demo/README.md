# Booking Demo
This demo simulates a booking application including three services:
* booking
* car
* hotel

## Prerequisites
You will need:
1. [JDK 1.8][jdk]
2. [Maven 3.x][maven]

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[maven]: https://maven.apache.org/install.html

### via executable files
1. run the following command to generate
   ```
   mvn toolkit:generate
   ```

2. follow the instructions in the [How to run](https://github.com/apache/servicecomb-saga/blob/master/docs/user_guide.md#how-to-run) section in User Guide to run postgreSQL and alpha server.

3. start application up
   1. start hotel service. The executable jar file should be in `saga-demo/booking/hotel/target/saga`.
   ```bash
   java -Dserver.port=8081 -jar hotel-${saga_version}-exec.jar
   ```

   2. start car service. The executable jar file should be in `saga-demo/booking/car/target/saga`.
   ```bash
   java -Dserver.port=8082 -jar car-${saga_version}-exec.jar
   ```

   3. start booking service. The executable jar file should be in `saga-demo/booking/booking/target/saga`.
   ```bash
   java -Dserver.port=8083 -Dcar.service.address=${host_address}:8082 -Dhotel.service.address=${host_address}:8081  -jar booking-${saga_version}-exec.jar
   ```

## User Requests by command line tools
1. Booking 2 rooms and 2 cars, this booking will be OK.
```
curl -X POST http://${host_address}:8083/booking/test/2/2
```
If everything is OK, "test booking 2 rooms and 2 cars OK" will be returned and it means everything is OK

Check the hotel booking status with
```
curl http://${host_address}:8081/bookings
```
The hotel booking result is:
[{"id":1,"name":"test","amount":2,"confirmed":true,"cancelled":false}]
**"cancelled":false** means this booking is finished and confirmed status is true.

Check the car booking status with
```
curl http://${host_address}:8082/bookings
```
the card booking result is:
[{"id":1,"name":"test","amount":2,"confirmed":true,"cancelled":false}]
**"confirmed":true,"cancelled":false** means everything is OK too.


2. Booking 3 rooms and 2 cars, this booking will cause the hotel order failed and trigger the compensate operation with car booking.
```
curl -X POST http://${host_address}:8083/booking/test/3/2
```
Error message will returned because the room count is more than 2:
{"timestamp":"2019-01-22T08:41:57.251+0000","status":500,"error":"Internal Server Error","message":"500 null","path":"/booking/test/3/2"}

Check the hotel booking status with
```
curl http://${host_address}:8081/bookings
```
There is no more records because the room booking was failed.

Check the car booking status with
```
curl http://${host_address}:8082/bookings
```
The second car booking will be marked with **cancel:true**:
[{"id":1,"name":"test","amount":2,"confirmed":true,"cancelled":false},
{"id":2,"name":"test","amount":2,"confirmed":false,"cancelled":true}]

## User Requests by html page

Open a browser with URL http://127.0.0.1:8083, You will get a html page. You can use this page to invoke test cases, and then get results.

**Note** transactions and compensations implemented by services must be idempotent.

## Debugging

To debug the services of the demo, just add debug parameter to JVM through the environment field in docker-compose configs. Let's take alpha-server as an example:

```yaml
alpha:
  image: "alpha-server:${TAG}"
  environment:
    - JAVA_OPTS=-Dspring.profiles.active=prd -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
  ports:
    - "6006:5005"
...
```

We append the debug parameter `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005` to the JAVA_OPTS environment variable, the Java process inside the container will listen on port 5005. With the port forwarding rule `6006:5005`, when alpha-server is ready, we can connect to port 6006 on the host and start debugging alpha-server.

If  you're using [IntelliJ](https://www.jetbrains.com/idea/), open the saga project, create a new debug configuration with template 'Remote', fill "Host" and "Port" input with "localhost" and "6006", then select "alpha" in the drop-down list of "Use classpath of module". When alpha-server is running, hit shift+f9 to debug the remote application.
