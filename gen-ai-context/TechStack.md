# Technology Stack

The technologies and frameworks described in this document are used to build the system.

## Languages

Java 21 is the primary language for this system.

## Frameworks

Spring Boot 3.4.3 is used for general structure and configuration.
Jackson 2.19.0 is used for serialization and deserialization.

## Project root package

The root package for the project is `com.intermancer.gaiaf.core`.

## Web Server

The root context path for the web server is `\gaia-f`

All output should be formatted using indented, pretty JSON. 

The com.intermancer.gaiaf.core.GaiaFCoreApplication class is the Spring Application class, and it also contains any beans required for global configuration.

### JSON Output

All output is pretty JSON.  Even simple collections, such as List<String>, should be output with indentations and on multiple lines.  The configuration should be included in the GaiaFCoreApplication class, since this is a global configuration.