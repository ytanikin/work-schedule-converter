<h1 align="center">Work Schedule converter</h1>

[//]: # (## Table of Contents)

## Usage
## Requirements

For building and running the application you need:

     Java 11 or higher

## Running the application locally

There are several ways to run the application on a local machine.

1. Execute the `main` method in the `com.yeldos.scheduleconverter.ScheduleConverterApplication.kt` class from your IDE.
2. Execute `./mvnw spring-boot:run`.
3. Execute `mvn spring-boot:run`. Requires maven installed locally.


## Testing the application locally
The only resource is the `/schedule/format` endpoint.
```
POST http://localhost:8080/schedule/format
```
To test the endpoint, you can use the following curl command:

<details>
  <summary>Click to expand!</summary>

```
curl -X POST --location "http://localhost:8080/schedule/format" \
    -H "Content-Type: application/json" \
    -d "{
          \"monday\" : [],
          \"tuesday\" : [
            {
              \"type\" : \"open\",
              \"value\" : 36000
            },
            {
              \"type\" : \"close\",
              \"value\" : 64800
            }
          ],
          \"wednesday\" : [],
          \"thursday\" : [
            {
              \"type\" : \"open\",
              \"value\" : 37800
            },
            {
              \"type\" : \"close\",
              \"value\" : 64800
            }
          ],
          \"friday\" : [
            {
              \"type\" : \"open\",
              \"value\" : 36000
            }
          ],
          \"saturday\" : [
            {
              \"type\" : \"close\",
              \"value\" : 3600
            },
            {
              \"type\" : \"open\",
              \"value\" : 36000
            }
          ],
          \"sunday\" : [
            {
              \"type\" : \"close\",
              \"value\" : 3600
            },
            {
              \"type\" : \"open\",
              \"value\" : 43200
            },
            {
              \"type\" : \"close\",
              \"value\" : 75600
            }
          ]
        }"
```
</details>

Alternatively, you can use the file `test-requests.http` in the root of the project
using IntelliJ IDEA Ultimate to test the endpoint. There are some examples in the file with 
valid(including corner cases) and invalid requests with expected responses.

Or run end-to-end tests `infrastructure.controller.OpenHoursControllerIT`

There 20 tests in total is displayed in the test report, indeed it doesn't count parametrized tests.
___
## Description

The application converts a work schedule from input JSON file to human-readable format.

Input JSON format is in the following format:
<details>
  <summary>Click to expand!</summary>

```{
      "monday" : [],
      "tuesday" : [
        {
          "type" : "open",
          "value" : 36000
        },
        {
          "type" : "close",
          "value" : 64800
        }
      ],
      "wednesday" : [],
      "thursday" : [
        {
          "type" : "open",
          "value" : 37800
        },
        {
          "type" : "close",
          "value" : 64800
        }
      ],
      "friday" : [
        {
          "type" : "open",
          "value" : 36000
        }
      ],
      "saturday" : [
        {
          "type" : "close",
          "value" : 3600
        },
        {
          "type" : "open",
          "value" : 36000
        }
      ],
      "sunday" : [
        {
          "type" : "close",
          "value" : 3600
        },
        {
          "type" : "open",
          "value" : 43200
    },
    {
      "type" : "close",
      "value" : 75600
    }
    ]
   }
```
</details>

Output text file is in the following format:
<details>
  <summary>Click to expand!</summary>

```
Monday: Closed
Tuesday: 10 AM - 6 PM
Wednesday: Closed
Thursday: 10:30 AM - 6 PM
Friday: 10 AM - 1 AM
Saturday: 10 AM - 1 AM
Sunday: 12 PM - 9 PM

```
</details>

## Structure

The code is divided into three parts:
1. `infrastructure` - currently contains only controller, but devoted as well
   for persistence.
2. `service` - the intermediate layer between infrastructure and domain layer. Ensures that
   the data is valid to be processed by the domain layer.
3. `domain layer` -  business logic.


```
    ├── infrastructure        
    |     └── controller
    |         ├── OpenHoursController
    |         ├── request
    |         |   ├── OpenHoursRequest
    |         |   └── WorkScheduleRequest
    |         └── response
    |             └── ErrorResponse
    ├── service               
    |     ├── ScheduleConverterService
    |     ├── RequestValidator
    |     └── RequestMapper
    └── domain
        ├── Week
        |── Day
        └── Shift 
```

## Architecture
A starting point for implementing `Hexagonal architecture`. You will also find it
named `Clean Architecture`, `Ports-And-Adapters`, or commonly `Onion Architecture`.

The basic idea is to make the domain layer independent of any library
and other layers such as service and infrastructure layers.
It is easily visible by looking at the import section, there are only Java SDK imports.
This can come in handy when you need to replace the repository layer (for instance, for scaling)
and remain the domain layer untouched.

Separation of concerns is also a key part of the domain entities themselves.
The domain entities(`Week`, `Day`, `Shift`) are autonomous and can be used separately,
for instance `Day.daySchedule` method doesn't add a new line separator to the result,
since it is the responsibility of the higher class to add the new line separator.
This brings us the possibility to use only Day's schedule independently,
such as adding one day's schedule to the middle of any text.

The same applies to the `Shift` class, the class doesn't append a comma to the result.

#### Notes
* All the logic of the requirements is fit in the domain entities,
  hence having `Manager`, Handler, `Service`, `Aggregate` or `Repository`
  inside domain layer is unnecessary.

* The domain entities are rich in logic, hence they are the only place
  where the business logic is implemented.
  No [Anemic Domain Model](https://martinfowler.com/bliki/AnemicDomainModel.html)
  are used, which is an anti-pattern.

* Jakarta Bean Validation, Basic Validation, and Business Validation have similar responses with a list
  of issues and the response code.

* Used technologies: `Spring Boot`, `Hexagonal Architecture`, `Factory Methods`, `Kotlin features`


### How corner cases are decided to be handled

There are following rules implemented:


1. Don't display `closing` time as `12 AM`, display `11:59 PM` instead.
2. Allow displaying `opening` time as `12 AM`
3. Be a little tolerant to the client's input, and allow input for `close` time
   as 0 or 59 seconds, but translate it to `11:59 AM` not `12:00 AM`
4. The minimal interval between `open` and next `close` is 60 seconds
5. The minimal interval between `close` and next `open` is 60 seconds
6. The minimal interval is 120 seconds when it comes to midnight, i.e.  11:59 AM - 11:59 AM, allowed values or 11:59 AM - 12:01 AM or 11:58 AM - 11:59 AM
7. Overlapping intervals are not allowed, e.g. 1 AM - 2 AM and 1:30 AM - 4:02 AM
8. No maximum interval is implemented, currently it is 24 hours


Consider the input values has only one shift in the entire week in the following examples:

| Monday Open   | Monday Close | Tuesday Open | Tuesday Close | Result              | Comments                                                                                                                                                           |
|---------------|--------------|--------------|---------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| from 0  to 60 |              |              | from 0 to 60  | 12 AM - 11:59 AM    | I have chosen a way where 11:59 is displayed instead of 12 AM when it is about closing time                                                                        |
| 100           |              |              | 0             | 12:01 AM - 11:59 AM | Is Tuesday considered as working day!?                                                                                                                             |
| 7200          |              |              | from 0 to 60  | 2 AM - 11:59 AM     |                                                                                                                                                                    |
| 86399         |              |              | from 0 to 60  | error               | since we never display closing time as 12 AM as closing time we display only 11:59, therefore opening time 11:59 AM is not allowed when closing time is 11:59 too. | 
| 86399         |              |              | 360           | 11:59 AM - 12:06 AM | Is Monday considered as working day!?                                                                                                                              |
| 86338         |              |              | from 0 to 60  | 11:58 PM - 11:59 PM |                                                                                                                                                                    |
| 3600          |              |              | 3600          | error               | same time is not allowed, minimal interval must be 60 seconds                                                                                                      |
| 3600          |              |              | 7200          | error               | closing time must not be greater than open time, this can mislead a user, e.g. 1 AM - 2 AM. Requirements need to be changed to display the mark of next day        |
|               |              | 7200         | 3600          | error               | closing time must be greater than that open time.                                                                                                                  |
| 7200          |              |              | 3600          | 2 AM - 1 AM         |                                                                                                                                                                    |
|               |              | 7200         | 7200          | error               | minimal interval must be 60 seconds                                                                                                                                |
|               |              | 7200         | 7250          | error               | minimal interval must be 60 seconds                                                                                                                                |
|               |              | 7200         | 7260          | 2 AM - 2:01 AM      |                                                                                                                                                                    |



___

### The input structure improvements.
#### Pros of current implementation
* Simple to understand.
* Makes validation easier especially when it comes to the shifts across days or midnight.
* By the current structure, we can be sure of a client's intention of schedule.
* Clients can't mix up weekdays, like `monday` instead of `tuesday`.

#### Cons
* Difficult to extend
<details>
  <summary>Click to expand!</summary>

```{
      "monday" : [],
      "tuesday" : [
        {
          "type" : "open",
          "value" : 36000
        },
        {
          "type" : "close",
          "value" : 64800
        }
      ],
      "wednesday" : [],
      "thursday" : [
        {
          "type" : "open",
          "value" : 37800
        },
        {
          "type" : "close",
          "value" : 64800
        }
      ],
      "friday" : [
        {
          "type" : "open",
          "value" : 36000
        }
      ],
      "saturday" : [
        {
          "type" : "close",
          "value" : 3600
        },
        {
          "type" : "open",
          "value" : 36000
        }
      ],
      "sunday" : [
        {
          "type" : "close",
          "value" : 3600
        },
        {
          "type" : "open",
          "value" : 43200
    },
    {
      "type" : "close",
      "value" : 75600
    }
    ]
   }
```
</details>

Therefore, there is only improvement in the input structure I can suggest.
* First, instead of using an `array` of `times` we can use the object with a key of
  `shifts`, this will allow us to be flexible in the future, so we are able to easily
  add additional information to the day schedule, e.g `break`, `lunch`,
  if `round the clock` during the day, and so on, while not forcing the client
  to even know about the changes.


```{
  "monday": {
    "shifts" : [
      {
        "type": "open",
        "value": "3600"
      }
    ],
    //optional fields added in the future
    "roundTheClock": true,
    "closed": false,
    "closedReason": "Independence Day",
    "breaks": [
        {
          "startTime": "09:00",
          "endTime": "17:00"
        }
    ]
    //more optional fields
    ...
  },
  "tuesday": {
    "shifts" :[]
  }, ...
```

___