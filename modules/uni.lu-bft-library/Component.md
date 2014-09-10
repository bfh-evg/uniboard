# BFH-CONFIGURATION

## Basic Description

This component provides a ConfigurationManager, which allows to load the configurations
from either JNDI or from a file.

## Services

This component implements the ConfigurationManager.

## Attributes

This component doesn't modify the attributes.

## Configuration

This component loads at startup the lists of all configurations to load from the jndi resource "/uniboard/configuration".
Every key is expected to be of the syntax NAME.{jndi,external}
Based on the key ending the corresponding configuration is loaded either from JNDI or from a file.

## Example EJB-Descriptor

This component doesn't require a EJB-Descriptor