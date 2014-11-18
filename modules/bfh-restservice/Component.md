# BFH-RESTSERVICE

## Basic Description

This component implements a RESTful interface of the UniBoard.

## Services

This component implements the GetService and the PostService by delegating the requests to the successor services.

## Attributes

This component does not add any attributes.

## Configuration

This component does not have any configuration possibilities.

## Example EJB-Descriptor

<ejb-jar ...>
	<enterprise-beans>
		<session>
			<ejb-name>UniBoardRestService</ejb-name>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.restservice.UniBoardRestServiceImpl/postSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.PostService</local>
				<ejb-link>SectionedService</ejb-link>
			</ejb-local-ref>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.restservice.UniBoardRestServiceImpl/getSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.GetService</local>
				<ejb-link>PersistenceService</ejb-link>
			</ejb-local-ref>
		</session>
	</enterprise-beans>
</ejb-jar>

## Example Post

POST /uniboard/messages/post HTTP/1.1
Content-Type: application/json
Accept: application/json

{
	"message": "HelloSpringfield",
	"alpha": {
		"attribute": [
			{
				"key": "name",
				"value": { "type": "stringValue", "value": "Homer Simpson" }
			},
			{
				"key": "age",
				"value": { "type": "integerValue", "value": 35 }
			}
		]
	}
}

## Example Query

POST /uniboard/messages/query HTTP/1.1
Content-Type: application/json
Accept: application/json

{
	"constraint": [
		{
			"type": "equal",
			"identifier": { "type": "alphaIdentifier", "part": [ "age" ] },
			"value": { "type": "integerValue", "value": 35 }
		}
	],
	"order": [
		{
			"identifier": { "type": "alphaIdentifier", "part": [ "name" ] },
			"ascDesc": true
		}
	],
	"limit": 0
}
