# BFH-TIMED-GET
## Basic Description

Provides the chronological property.

## Services

This component implements the PostService.

### Attributes

This adds the "timestamp" attribute to beta.

## Configuration

This component does not need any configuration.

## Example EJB-Descriptor

```xml
<enterprise-beans>
	<session>
		<ejb-name>TimedGetService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.timedget.TimedGetService/getSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.GetService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
