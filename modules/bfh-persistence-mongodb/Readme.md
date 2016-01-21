# BFH-PERSISTENCE-MONGODB

## Basic Description

This component provides a persistence service for UniBoard based on a running MongoDB installation.


## Services

This component implements the Post- and GetService.

## Attributes

This component doesn't modify the attributes.

## Configuration

This component uses the mongodb-connector and therefore needs no configuration of the database connection.
The collection used defaults to "uniboard" and can be changed over an enviroment entry "collection" in the ejb-jar.xml

## Example EJB-Descriptor

This component doesn't require a EJB-Descriptor

## Example ejb-jar.xml
```xml
<ejb-jar xmlns="http://xmlns.jcp.org/xml/ns/javaee"
		 version="3.2"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/ejb-jar_3_2.xsd">
	<enterprise-beans>
		<session>
			<ejb-name>PersistenceService</ejb-name>
			<env-entry>
				<env-entry-name>collection</env-entry-name>
				<env-entry-type>java.lang.String</env-entry-type>
				<env-entry-value>uniboard-collection-name</env-entry-value>
			</env-entry>
		</session>
	</enterprise-beans>
</ejb-jar>
```
