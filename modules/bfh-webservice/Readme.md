# BFH-WEBSERVICE

## Basic Description

This component provides a WSDL-based webservice interface for clients. The WSDL is defined in bfh-wsdl.

## Services

This component doesn't implement any service, but requires a Post- and Get-Service to pass client requests.

## Attributes

This component doesn't change any attribute.

## Configuration

This component has no configuration options.

## Example EJB-Descriptor
```xml
<enterprise-beans>
	<session>
		<ejb-name>UniBoardServiceImpl</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.webservice.UniBoardServiceImpl/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.webservice.UniBoardServiceImpl/getSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.GetService</local>
			<ejb-link>GetServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
