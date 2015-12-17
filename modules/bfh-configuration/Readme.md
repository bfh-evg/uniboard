# BFH-CONFIGURATION

## Basic Description

This component provides a ConfigurationManager, which allows to load the configurations and states
from the mongodb.

## Services

This component implements the ConfigurationManager.

## Attributes

This component doesn't modify the attributes.

## Configuration

This component does not require a configuration if it can use the default collection
"uniboard-configuration". To change the collection use define the ressource "ConfigurationCollection" in the ejb-descriptor. An example is below.

## Example EJB-Descriptor

<enterprise-beans>
	<session>
		<ejb-name>ConfigurationManagerImpl</ejb-name>
		<env-entry>
			<env-entry-name>ConfigurationCollection</env-entry-name>
			<env-entry-type>java.lang.String</env-entry-type>
			<env-entry-value>CollectionName</env-entry-value>
		</env-entry>
	</session>
</enterprise-beans>
