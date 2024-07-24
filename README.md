# API service for a TODO app

### APIs

The TODO application has the following APIs to create and manipulate tasks


#### ListAll

Lists all tasks


```
GET http://localhost:8080/tasks
```


**URI parameters:**


<table>
  <tr>
   <td>Name
   </td>
   <td>In
   </td>
   <td>Required
   </td>
   <td>Type
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>sortBy
   </td>
   <td>Query
   </td>
   <td>False
   </td>
   <td>String
   </td>
   <td>Field to sort by
   </td>
  </tr>
  <tr>
   <td>orderBy
   </td>
   <td>Query
   </td>
   <td>False
   </td>
   <td>String
   </td>
   <td>Order to sort by - can take the values <code>asc</code> or <code>desc</code>
   </td>
  </tr>
  <tr>
   <td>outdatedOnly
   </td>
   <td>Query
   </td>
   <td>False
   </td>
   <td>Boolean
   </td>
   <td>Lists outdated tasks
   </td>
  </tr>
  <tr>
   <td>priority
   </td>
   <td>Query
   </td>
   <td>False
   </td>
   <td>Integer
   </td>
   <td>Priority to filter by
   </td>
  </tr>
</table>


**Example:**


```
GET http://localhost:8080/tasks?sortBy=dueDate&orderBy=desc&priority=3&outdatedOnly=true
```



#### FindById

Finds a task, identified by the id field


```
GET http://localhost:8080/tasks/{id}
```


**URI parameters:**


<table>
  <tr>
   <td>Name
   </td>
   <td>In
   </td>
   <td>Required
   </td>
   <td>Type
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>id
   </td>
   <td>Path
   </td>
   <td>True
   </td>
   <td>Long
   </td>
   <td>Id of task to find
   </td>
  </tr>
</table>


Example:


```
GET http://localhost:8080/tasks/5
```



#### AddTask

Adds a task


```
POST http://localhost:8080/tasks
```


**URI parameters:**

None

**Request Body:**


<table>
  <tr>
   <td>Name
   </td>
   <td>Required
   </td>
   <td>MediaType
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>requestBody
   </td>
   <td>True
   </td>
   <td>JSON
   </td>
   <td>Task to add in JSON \
Other conditions:
<ul>

<li>Priority should be included

<li>Id should not be included
</li>
</ul>
   </td>
  </tr>
</table>


**Example:**


```
POST http://localhost:8080/tasks
```


Request Body: 


```
{
	"title": "Simple task 2",
	"priority": 4
}
```



#### UpdateTask

Updates a task, identified by the id field


```
PATCH http://localhost:8080/tasks/{id}
```


**URI parameters:**


<table>
  <tr>
   <td>Name
   </td>
   <td>In
   </td>
   <td>Required
   </td>
   <td>Type
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>id
   </td>
   <td>Path
   </td>
   <td>True
   </td>
   <td>Long
   </td>
   <td>Id of task to update
   </td>
  </tr>
</table>


**Request Body:**


<table>
  <tr>
   <td>Name
   </td>
   <td>Required
   </td>
   <td>MediaType
   </td>
   <td>Description
   </td>
  </tr>
  <tr>
   <td>requestBody
   </td>
   <td>True
   </td>
   <td>JSON
   </td>
   <td>Task to update in JSON \
Other conditions:
<ul>

<li>Id should not be included
</li>
</ul>
   </td>
  </tr>
</table>


**Example:**


```
PATCH http://localhost:8080/tasks/3
```


Request Body:


```
{
	"title": "Updated task",
	"priority": 4,
	"status": "Done"
}
```

## References
* [Getting Started | Building REST services with Spring](https://spring.io/guides/tutorials/rest)
* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [Spring Initializr](https://start.spring.io/)
* [Getting Started | Testing the Web Layer](https://spring.io/guides/gs/testing-web)
