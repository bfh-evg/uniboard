# BFH-ORDERED

## Basic Description

Ensures an absolut order for every section.

## Services

This component implements the PostService

### Attributes

This component adds the "rank" attribute to beta.

## Configuration

This component has no configuration but saves its current state in "bfh-ordered".

## Example EJB-Descriptor

<enterprise-beans>
	<session>
		<ejb-name>OrderedService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.ordered.OrderedService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans
