# BFH-SECTIONED

## Basic Description

This component provides the "sectioned" property for UniBoard.

## Services

This component implements the PostService to validate, that every incoming message  
has a valid section defined.

## Attributes

This component requires that in the alpha attributes is an attribute called "section". The value  
of this attribute has to be in the set of configured sections.

## Configuration

This component uses the ConfigurationManager. It requests the properties for the "bfh-sectioned" key.
In this properties all values are expected to be valid sections. The key values are ignored.

## Error Codes

BSE-001 - Attribute missing
BSE-002 - Attribute is not a StringValue
BSE-003 - Configuration for section-bfh is missing
BSE-004 - Specified section is not known on this UniBoard

## Example EJB-Descriptor

	<enterprise-beans>
		<session>
			<ejb-name>SectionedService</ejb-name>
			<ejb-local-ref>
				<ejb-ref-name>ch.bfh.uniboard.sectioned.SectionedService/postSuccessor</ejb-ref-name>
				<local>ch.bfh.uniboard.service.PostService</local>
				<ejb-link>PostServiceToLink</ejb-link>
			</ejb-local-ref>
		</session>
	</enterprise-beans>
