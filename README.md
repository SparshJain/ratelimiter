### Configuration

``` yaml
server:
    port: 8888                                #port
start
    redis-limiter:                            #Limiter configuration
        redis-host: 127.0.0.1                 #redis server ip  
        check-action-timeout: 100             #check action will be executed asynchronous, this is the timeout support 
user-based-permits:						  
  userid.user1: 5						#Number of permits of user1 for all APIs
  userid.user2: 10				    #Used for only GenericAnnotation
```


### Start Redis server

Start Redis server on Docker.

``` bash
docker-compose up -d
```


### Run Application as 

mvn clean install test surefire-report:report

### Testing

Tests are well written and reports are generated in target/site.
A sample report is attached as well.


### Annotations

- `@GenericLimit`
- `@SpecificLimit`  



### specificConfig (This example is used in SpecificTests)

`@SpecificLimit` annotation makes configuration can be changed dynamically, we can change the configuraton by internal RESTful API.

If we want to update configuration, assign Content-Type as application/json, then excute PUT http://localhost:8888/specificConfig, the request body as below: 
``` json
{
	"controllerName" : "SpecificController",
	"methodName" : "developerAPI",
	"userId" : "user2",
	"permits" : 4
}


