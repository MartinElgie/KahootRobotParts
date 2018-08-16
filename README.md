# Inventory server and client demo

## Original specification

Each​ ​robot​ ​part​ ​has​ ​a​ ​name,​ ​serial​ ​number,​ ​manufacturer,​ ​weight​ ​and​ ​a​ ​list​ ​of​ ​other
components​ ​it​ ​is​ ​compatible​ ​with​ ​within​ ​the​ ​system.

The service should have the following endpoints:

* add
* read
* update
* delete
* list​ ​all​ ​parts
* list​ ​X​ ​unique​ ​robot​ ​parts​ ​that​ ​are​ ​compatible

## Outline of Robot Part Inventory

Each robot part has a name, serial number, manufacturer, weight and compatible parts.

Name, serial number and weight are simple enough to be stored against a single 'Robot Part' entry in a single database table. Unless otherwise specified it's probably best to generate serial numbers as UUIDs. This field should have a unique constraint and there should also be a separate identify field on the table for internal database joins.

It's likely that parts will share manufacturers so to prevent data duplication and to allow for possible selection by this field, there should be a separate manufacturer table, and the manufacturer ID can be stored on the Part table.

Compatible parts are trickier since if these were stored on each part it would mean duplicating data because each 'compatibility' is a 2 way relationship. Maintaining this would also be difficult in the event of orphaned records. Instead, compatibility should be stored in a separate database. This will only need 2 columns to define a compatibility relationship between any two parts, but it present the problem of distinguishing between the columns. They will both be foreign keys pointing to the same table and the same column, but for different records. A possible way to deal with this is to use a constraint on the table to make sure one column, linked to a part table primary key, is bigger than the other. If both columns are also set to 'NOT NULL' this prevents duplicates and defines a consistent way to insert new relationships. It still means having to select from the same value being one either one or the other column to get all of a single part's compatible parts, but this isn't much of a problem.

(Database schema design can be found in RobotPartsServer/src/sql/RobotInventorySchema.sql)

## Scaling Issues
One of the biggest issues with scaling would be with a very large number of compatible parts combined with a very large number of compatible records, this would potentially mean querying far greater amounts of data than a single record when a user retrieves just one: the requested part is retrieved from the database, then the compatibility table is queried with the retrieved database ID of the part for compatible parts. All compatible parts are then queried for their details.

In schema design one way to reduce the load here is to only return serial numbers for compatible parts instead of full records, which reduces the data being sent in each HTTP request. If the serial number itself (which is what is presented to the user, rather than the internal database ID) was stored on the relationship table as well as or instead of the database ID this would then eliminate the need to query all the compatible parts.

At the web service level, since there's an endpoint to return all compatible parts for a given part, the 'compatible_parts' array field could also be dropped from the 'read' endpoint. To view all the related parts a user instead only uses the 'compatible list' endpoint, which probably returns more useful information overall.

In the event of a large number of records the two list endpoints would be the biggest cause of problems so these would need to be changed to user a pagination parameter and only return a limited number of records per request, along with a token for the next page. The client can then be designed to only present the next page of results to the user when triggered to, preventing everything from being retrieved and displayed in bulk to all users.  

Load balancing can also be used if there are a large number of requests to deal with, splitting requests between multiple servers. The database(s) could also be sharded to reduce the index size for each query. Deploying the web service and database on a managed service such as AWS would make these options considerably easier if high availability was likely to be an issue. 

# Security issues
The web service and client are currently both unsecured, in a production environment some security would need to be put in place such as creating a token, such as a JWT token on the client, or having the client send a request for one to the web service. This token could then be inserted into the header of every request to confirm it came from an authorised source.

If the client is used by a specific user that has to log in at some point the token could also be associated with the user. The authorisation token could then be used to restrict access to records based on some for of user rights.

# Deploying the project
To deploy the project first clone the repository. This is a multi-module gradle project with a separate build.gradle for the server and client applications, they share the common project with its part entity model.

### Server
The server can be run as a standalone application, it will by default run on port 3700. The endpoints are:

* http://localhost:3700/robots/parts/read/[part ID]
    * [GET] Returns an existing part with the supplied UUID
* http://localhost:3700/robots/parts/all
    * [GET] Returns a list of all robot parts in the inventory
* http://localhost:3700/robots/parts/add
    * [POST] For the supplied part entity, returns the entity with its UUID after inserting it in the inventory
* http://localhost:3700/robots/parts/compatible/[part ID]
    * [GET] Returns a list of parts compatible with part with the supplied UUID
* http://localhost:3700/robots/parts/delete/[part ID]
    * [GET] Deletes the part with the supplied UUID from the inventory
* http://localhost:3700/robots/parts/update/[part ID]
    * [POST] Updates the part with the supplied UUID with the details in the post body
    
### Client
The client is deployed by default on port 3701. It can be run without the server running but every inventory command will fail. Once launched the client can be used through the command line interface. The following commands are available:
* add [ --name, --manufacturer, --weight_grams ]
* exit 
* help 
* compatible [ --serial_number ]
* read [ --serial_number ]
* update [ --serial_number ]
* list 
* delete [ --serial_number ]

### Pending changes
Due to a time constraints when originally creating this project some features haven't yet been implemented. These are as follows.
* Unit tests on the RobotPartService class in the server module
* Integration tests for the Controller endpoints
* Fix known bug when adding a robot part with an existing manufacturer name where a new manufacturer is always created
* Unit tests covering client input parameters