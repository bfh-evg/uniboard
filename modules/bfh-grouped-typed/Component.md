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
The keys define the names of the groups. The values is a string representation of the json schema defining the type.
If only the grouped property is needed the value can be left empty.
If only the typed property is needed create only one entry with group name set to "singleType".

By default the linking is auto wired between grouped and typed.
If you need only one property you can change the linking in the EJB-Descriptor.

## Error Codes

BGT-001 - Attribute missing
BGT-002 - Attribute is not a StringValue
BGT-003 - Configuration for section-bfh is missing
BGT-004 - Specified section is not known on this UniBoard
BGT-005 - Message is not valid in typed only modus
BGT-006 - Message is not valid for the selected group

## Example EJB-Descriptor
###Descriptor for both properties. Map to GroupedService from the predecessor:
	<enterprise-beans>
		<session>
			<ejb-name>TypedService</ejb-name>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.typed.TypedService/postSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.PostService</local>
				<ejb-link>POSTSERVICE_TO_USE</ejb-link>
			</ejb-local-ref>
		</session>
	</enterprise-beans>
###Descriptor for typed only. Map to TypedService from the predecessor:
	<enterprise-beans>
		<session>
			<ejb-name>TypedService</ejb-name>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.typed.TypedService/postSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.PostService</local>
				<ejb-link>POSTSERVICE_TO_USE</ejb-link>
			</ejb-local-ref>
		</session>
	</enterprise-beans>
###Descriptor for grouped only. Map to GroupedService from the predecessor:
	<enterprise-beans>
		<session>
			<ejb-name>GroupedService</ejb-name>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.grouped.GroupedService/postSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.PostService</local>
				<ejb-link>POSTSERVICE_TO_USE</ejb-link>
			</ejb-local-ref>
		</session>
	</enterprise-beans>