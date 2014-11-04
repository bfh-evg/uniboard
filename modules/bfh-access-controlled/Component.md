# BFH-ACCESS-CONTROLLED

## Basic Description

Provides access-controlled for the board with time and amount based functionality. Therefore depends on 
chronological to specify the arrival time of a message.
Introduces the group "access-right" to manage the actual access rights

## Services

Uses the Post-Service to ensure that only authorised users can post messages

### Attributes

Requires a the alpha attributes "signature" and "key".

## Configuration

This component uses the ConfigurationManager. It requests the properties for the "bfh-access-controlled" key.

## Error Codes

BAC-001 There is no authorization for the provided key
BAC-002 The provided signature is not valid
BAC-003 Authorization is not active yet
BAC-004 Authorization expired
BAC-005 Amount of allowed posts used up
BAC-006 Internal server error

## Example EJB-Descriptor

<enterprise-beans>
	<session>
		<ejb-name>AccessControlledServiceSimply</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.accesscontrolled.AccessControlledService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceTestBean</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
