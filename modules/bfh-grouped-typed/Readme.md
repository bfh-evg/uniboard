# BFH-GROUPED-TYPED

## Basic Description

This component provides the "grouped" and/or "typed" property for UniBoard.
They can be used solely or combined.
Note that for the "typed" property this component expects the messages to be JSON strings.

## Services

This component implements the PostService twice. First to guarantee the "grouped" property for 
incoming messages and then again to guarantee the "typed" property.

### Attributes

For the "grouped" property it's required that an attribute "group" is added to the alpha attributes.

## Configuration

This component uses the ConfigurationManager. It requests the properties for the "bfh-grouped-typed" key.
The keys define the names of the groups. The values represents the filepath to the json schema defining the type.
If only the grouped property is needed the value can be left empty.
If only the typed property is needed create only one entry with group name set to "singleType".

By default the linking is auto wired between grouped and typed.
If you need only one property you can change the linking in the EJB-Descriptor.

Example configurations:

Only Groups
```json
{
"config_key": "bfh-grouped-typed",
"entries": {
	"group1":"",
	"group2":""
	}
}
```
Both
```json
{
"config_key": "bfh-grouped-typed",
"entries": {
	"group1":"/opt/jsonSchemas/group1.jsd",
	"group2":"/opt/jsonSchemas/group2.jsd"
	}
}
```
Only Typed
```json
{
"config_key": "bfh-grouped-typed",
"entries": {
	"singleType":"/opt/jsonSchemas/type.jsd"
	}
}
```
## Error Codes

- BGT-001 - Attribute missing
- BGT-002 - Attribute is not a StringValue
- BGT-003 - Configuration for section-bfh is missing
- BGT-004 - Specified section is not known on this UniBoard
- BGT-005 - Message is not valid in typed only modus
- BGT-006 - Message is not valid for the selected group

## Example EJB-Descriptor
###Descriptor for both properties. Map to GroupedService from the predecessor:
```xml
<enterprise-beans>
	<session>
		<ejb-name>TypedService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.typed.TypedService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
###Descriptor for typed only. Map to TypedService from the predecessor:
```xml
<enterprise-beans>
	<session>
		<ejb-name>TypedService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.typed.TypedService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
###Descriptor for grouped only. Map to GroupedService from the predecessor:
```xml
<enterprise-beans>
	<session>
		<ejb-name>GroupedService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.grouped.GroupedService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
