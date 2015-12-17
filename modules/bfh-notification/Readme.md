# BFH-NOTIFICATION

## Basic Description

This component provides the "notifying" property for UniBoard. The registration/unregister is implemented as webservice.
Every post that got accepted is checked if it fits for one of the registered queries and the user if necessary gets
notified.
To check if the post matches a registred query this component requires a beta attribute that assigns a unique value to
each post(e.g. ordered, history).
## Services

This component implements the PostService to check accepted posts for matching notifications.

### Attributes

For this property are no additional properties needed.

## Configuration

This component uses the ConfigurationManager. It requires the configuration "bfh-notification", which only has one option "unique" which allows to define the beta attribute that
provides the unique identifier of the post.
```json
{
"config_key": "bfh-notification",
"entries": {
	"unique":"uniqueBetaAttribute"
	}
}
```
It also perists its state under "bfh-notification-observer".

## Example EJB-Descriptor
```xml
<enterprise-beans>
	<session>
		<ejb-name>NotificationService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.notification.NotificationService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
