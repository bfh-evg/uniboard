# BFH-CHRONOLOGICAL

## Basic Description

Provides the chronological property for UniBoard.

## Services

This component implements the PostService to provide the chronological property.

### Attributes

Adds to every post a beta attribute "timestamp" containing the current time.

## Configuration

This component doesn't have any configuration possibilities.

## Example EJB-Descriptor

<enterprise-beans>
	<session>
		<ejb-name>ChronologicalService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.chronological.ChronologicalService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
