# BFH-CERTIFIED-POSTING

## Basic Description

With this component the board certifies every post received by signing it and returning the signature
in the beta attributes.

## Services

This component implements the PostService

### Attributes

Adds the beta attribute "boardSignature"

## Error Codes

BCP-001 Internal server error

## Configuration
```json
{
"config_key": "bfh-certified-posting",
"entries": {
	"keystore-path":"/path/to/keystore",
	"keystore-pass":"passForKeystore",
	"id":"aliasOfTheEntry",
	"privatekey-pass":"passForThePrivateKey"
	}
}
```
## Example EJB-Descriptor
```
<enterprise-beans>
	<session>
		<ejb-name>CertifiedPostingService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.certifiedposting.CertifiedPostingService/postSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.PostService</local>
			<ejb-link>PostServiceToLink</ejb-link>
			</ejb-local-ref>
	</session>
</enterprise-beans>
```
