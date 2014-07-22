# BFH-PERSISTENCE-MONGODB

## Basic Description

This component provides a persistence service for UniBoard based on a running MongoDB installation.


## Services

This component implements the Post- and GetService.

## Attributes

This component doesn't modify the attributes.

## Configuration

This component uses the ConfigurationManager. It requests the properties for "bfh-mongodb".
Following configurations can be made. If not specified defaults apply:
| Key      | Meaning        | default  |
| ------------- |-------------| -----|
| host | hostname of the machine running the MongoDB server | localhost |
| dbname | name of the database | uniboard |
| collection | name of the collection to use | default |
| port | connection port of MongoDB | 27017 |
| username | username to authenticate at MongoDB | admin |
| password | password for user authentication | password |
| authentication | Boolean indicating if authentication is required by the MongoDB | false |

## Example EJB-Descriptor

This component doesn't require a EJB-Descriptor