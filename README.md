##Trail Booking Application

Booking application will help Hikers to view trails and view, create, modify their booking. 
Admins will be able to create, update, delete trails and view all bookings.

Pre-requisite: 

<li>docker and docker-compose should be available and docker daemon running
<li>Please make sure no other app is running on port 8080 and 5432.</li>
<li> Active internet connection required to download dependencies like JDK and Postgres. </li>

To start the application do the following:
1. Open a terminal and cd to the root directory for this project
2. run `docker-compose up` (To run as daemon `docker-compose up -d`)
3. Use Create trails API to create some trails
```
   curl -i -X POST  -H "Accept: Application/json" -H "Content-Type: application/json" localhost:8080/trails -d '[{"name":"test","startAt":"12:00","endAt":"14:00","minimumAge":10,"maximumAge":50,"unitPrice":199}]'
```

The application will be available to on `http://localhost:8080/`



##API's 

Note: API marked as 'only for Admin use' should not be used to test Hiker related functionality.

##Create a trail  
only for Admin use

`POST`  `/trails`

Request Json:

```
[
    {
        "name" : "test_trail",
        "startAt" : "12:00",
        "endAt" : "14:00",
        "minimumAge" : 10,
        "maximumAge" : 50,
        "unitPrice" : 199
    }
]
```
   
#####Responses 

Status: `200` 
Response Body : `None`
           
Status: ``400``               
Response Body :         
```
{
    "errorMessage": "Trail name is required!",
    "validationErrors": null
}
```

#####Example
```
curl -i -X POST  -H "Accept: Application/json" -H "Content-Type: application/json" localhost:8080/trails -d '[{"name":"test","startAt":"12:00","endAt":"14:00","minimumAge":10,"maximumAge":50,"unitPrice":199}]'
```
<hr>


##Update a trail
only for Admin use

`PUT`  `/trails`

Request Json:

```

    {
        "name" : "test_trail",
        "startAt" : "12:00",
        "endAt" : "14:00",
        "minimumAge" : 10,
        "maximumAge" : 50,
        "unitPrice" : 199
    }

```

#####Responses

Status: `200`
Response Body : 
```
    {
        "name" : "test_trail",
        "startAt" : "12:00",
        "endAt" : "14:00",
        "minimumAge" : 10,
        "maximumAge" : 50,
        "unitPrice" : 199
    }
```

Status: ``400``               
Response Body :
```
{
    "errorMessage": "Invalid Request!",
    "validationErrors": null
}
```

#####Example
```
curl -i -X PUT  -H "Accept: Application/json" -H "Content-Type: application/json" localhost:8080/trails -d '{"name":"test","startAt":"12:00","endAt":"14:00","minimumAge":10,"maximumAge":50,"unitPrice":199}'
```
<hr>

##Get all trails

`GET`  `/trails`


#####Response

Status: `200`

Response Body : 
```
[
    {
        "name" : "test_trail",
        "startAt" : "12:00",
        "endAt" : "14:00",
        "minimumAge" : 10,
        "maximumAge" : 20,
        "unitPrice" : 2.2
    }
]
```


#####Example
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:8080/trails
```
<hr>

<br>


##Get a specific trail

`GET`  `/trail/{name}`

#####Path variables:

`trailName` (Required)

#####Response:

Status: `200`

Response Body :
```
    {
        "name" : "test",
        "startAt" : "12:00",
        "endAt" : "14:00",
        "minimumAge" : 10,
        "maximumAge" : 20,
        "unitPrice" : 2.2
    }
```
#####Example
``` 
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:8080/trail/test
```
<hr>

##Delete a trail
only for Admin use

`DELETE`  `/trail/name`

#####Path variables:

`name` (Required)

#####Responses

Status: `200`
Response Body :
```
    {
        "successMessage": "Trail 'name' deleted successfully"
    }
```

Status: ``400``               
Response Body :
```
{
    "errorMessage": "Trail 'name' not found!",
    "validationErrors": null
}
```

#####Example
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X DELETE localhost:8080/trail/test
```
<hr>
<br>

##Create a booking

`POST`  `/booking`

Request Json:

```
{
  "trail" : "test_trail",
  "email" : "abs@abs.com",
  "totalHiker" : [
    {
      "name": "John Doe",
      "age": 10,
      "contact": 1212,
      "ID": "12345678"
      
    },
    {
      "name": "lav",
      "age": 27,
      "contact": 987654321,
      "ID": "12121212"
      
    }
  ]
}
```

#####Responses

Status: `200`
<br>
Response Body : 

```
{
    "id": 2,
    "trail": "trail1",
    "email": "abs@abs.com",
    "totalAmount": 399.98,
    "status": "confirmed",
    "createdAt": "2022-02-23T23:42:44.478495976",
    "hikerList":[
        {   
            "id": 3, 
            "name": "asdfg", 
            "age": 10, 
            "contact": 1212, 
            "identificationNumber": "12345678"
        },
        {
            "id": 4, 
            "name": "lav", 
            "age": 10, "contact": 11111, 
            "identificationNumber": "12121212"
        }
    ]   
}
```

#####Example
```
curl -i -X POST  -H "Accept: Application/json" -H "Content-Type: application/json" localhost:8080/booking -d '{"trail":"trail1","email":"abs@abs.com","totalHiker":[{"name":"asdfg","age":11,"contact":1212,"ID":"12345678"},{"name":"lav","age":20,"contact":11111,"ID":"12121212"}]}'
```

<br>

Status: ``400``               
Response Body :
```
{
    "errorMessage": "Invalid request!",
    "validationErrors":[
        {
            "field": "Age",
            "errorMessage": "Hiker 'asdfg' : Age is invalid!"
        },
        {
            "field": "Age",
            "errorMessage": "Hiker 'asdfg' : Age should be in limits (10-20)"
        }
    ]
}
```

#####Example
```
curl -i -X POST  -H "Accept: Application/json" -H "Content-Type: application/json" localhost:8080/booking -d '{"trail":"trail1","email":"abs@abs.com","totalHiker":[{"name":"asdfg","age":-1,"contact":1212,"ID":"12345678"},{"name":"lav","age":10,"contact":11111,"ID":"12121212"}]}'
```
<hr>

<br>

##Get all bookings
only for Admin use


`GET`  `/bookings`


#####Response

Status: `200`
<br>
Response Body :
```
[
  {
    "id": 2,
    "trail": "trail1",
    "email": "abs@abs.com",
    "totalAmount": 399.98,
    "status": "confirmed",
    "createdAt": "2022-02-23T23:42:44.478496",
    "hikerList": [
      {
        "id": 3,
        "name": "asdfg",
        "age": 10,
        "contact": 1212,
        "identificationNumber": "12345678"
      },
      {
        "id": 4,
        "name": "lav",
        "age": 10,
        "contact": 11111,
        "identificationNumber": "12121212"
      }
    ]
  }
]
```
#####Example

```
 curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:8080/bookings
```
<hr>

<br>

##Get a specific booking

`GET`  `/booking/{bookingId}`

#####Path variables:

`bookingId`  (Required)

#####Response:

Status: `200`
<br>
Response Body :
```
{
  "id": 2,
  "trail": "trail1",
  "email": "abs@abs.com",
  "totalAmount": 399.98,
  "status": "confirmed",
  "createdAt": "2022-02-23T23:42:44.478496",
  "hikerList": [
    {
      "id": 3,
      "name": "asdfg",
      "age": 10,
      "contact": 1212,
      "identificationNumber": "12345678"
    },
    {
      "id": 4,
      "name": "lav",
      "age": 10,
      "contact": 11111,
      "identificationNumber": "12121212"
    }
  ]
}
```
<br>

Status: `404` 
<br>
Response Body: `None`

#####Example
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:8080/booking/2
```
<hr>
<br>

##Get bookings by email

`GET`  `/bookings/{email}`

#####Path Variable:

`email`  (Required)

#####Response:

Status: `200`
<br>
Response Body :
```
[{
  "id": 2,
  "trail": "trail1",
  "email": "abs@abs.com",
  "totalAmount": 399.98,
  "status": "confirmed",
  "createdAt": "2022-02-23T23:42:44.478496",
  "hikerList": [
    {
      "id": 3,
      "name": "asdfg",
      "age": 10,
      "contact": 1212,
      "identificationNumber": "12345678"
    },
    {
      "id": 4,
      "name": "lav",
      "age": 10,
      "contact": 11111,
      "identificationNumber": "12121212"
    }
  ]
}]
```
<br>

Status: `404`
<br>
Response Body: `None`

#####Example
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:8080/bookings/abs@abs.com
```
<hr>
<br>


##Cancel a booking

`PUT`  `/booking/{bookingId}`

#####Path variables:

`bookingId`  (Required)

#####Response:

Status: `200` 
<br>
Response Body : `Booking Cancelled` or `Booking is already Cancelled`
<br>

Status: `404`
<br>
Response Body: `None`

#####Example
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X PUT localhost:8080/booking/2
```
<hr>
<br>
   

##Improvements:

1. Return Paginated result when admin request data for all bookings which will be useful if there are thousands of bookings.
2. Versioned sql files to create/update tables and indexes using tool like Flyway.
3. Security and ROLE based api access
4. Adding booking limit on no of hikers for a trail on a daily basis. 
5. Also write database integration test instead of just mocking repository in unit test.
6. Add separate volumes in docker-compose for application and db logs.
7. Add index  ('email','created_at') in table 'booking' for faster retrieval. 
8. Using enums instead of hardcoded strings



