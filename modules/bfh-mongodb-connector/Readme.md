# BFH-MONGODB-CONNECTOR

## Basic Description

This component handles the connection to the mongodb. It provides no direct service.
It allows to ask for a certain collection. If the requested collection does not exist
an exception is thrown. If a new collection should be created use the provided method.


## Services

This component does not implement either service. 

## Attributes

This component doesn't modify the attributes.

## Configuration

This component requires a JNDI entry to configure the database connection.
The default lookup name is "uniboard/mongodb-connector". This can be overruled in 
the ejb-jar.xml with an env-entry with the name "JNDI_URI".

The JNDI entry can have following entries:

| Key | Meaning | Default |
| ------------- | ----------- | ----------- |
| host | hostname of the machine running the MongoDB server | localhost |
| dbname | name of the database | uniboard |
| port | connection port of MongoDB | 27017 |
| username | username to authenticate at MongoDB | admin |
| password | password for user authentication | password |
| authentication | Boolean indicating if authentication is required by the MongoDB | false |

## Example ejb-jar.xml
```xml
<ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee"
		 version="3.2"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/ejb-jar_3_2.xsd">
	<enterprise-beans>
		<session>
			<ejb-name>ConnectionManagerImpl</ejb-name>
			<env-entry>
				<env-entry-name>JNDI_URI</env-entry-name>
				<env-entry-type>java.lang.String</env-entry-type>
				<env-entry-value>uniboard/mongodb-connector</env-entry-value>
			</env-entry>
		</session>
	</enterprise-beans>
</ejb-jar>
```
