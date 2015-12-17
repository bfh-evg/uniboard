# BFH-CERTIFIED-GET

## Basic Description

Provides the certified-get property by signing every query and the corresponding response.

## Services

Uses the GetService to sign all outgoing data.

### Attributes

Adds the attribute boardSignature to gamma.

## Error Codes

- BCG-001 Internal server error

## Configuration
```json
{
"config_key": "bfh-certified-get",
"entries": {
	"keystore-path":"/path/to/keystore",
	"keystore-pass":"passForKeystore",
	"id":"aliasOfTheEntry",
	"privatekey-pass":"passForThePrivateKey"
	}
}
```

## Example EJB-Descriptor
```xml
<enterprise-beans>
	<session>
		<ejb-name>CertifiedGetService</ejb-name>
		<ejb-local-ref>
			<ejb-ref-name>ch.bfh.uniboard.certifiedget.CertifiedGetService/getSuccessor</ejb-ref-name>
			<local>ch.bfh.uniboard.service.GetService</local>
			<ejb-link>GetServiceToLink</ejb-link>
		</ejb-local-ref>
	</session>
</enterprise-beans>
```
