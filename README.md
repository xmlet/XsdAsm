[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdAsm.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdAsm%7C1.0.8%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdAsm)
[![Vulnerabilities](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Bugs](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)

# XsdAsm

XsdAsm is a library dedicated to generate a fluent java API based on a XSD file. It uses XsdParser library to parse the xsd file into a list of java elements that 
XsdAsm will use in order to obtain the information needed to generate the correspondent classes. In order to generate classes this library also uses the ASM library, 
which is a library that provides a java interface to bytecode manipulation, which allows for the creation of classes, methods, etc. (More information about 
ASM at http://asm.ow2.org/).

## Installation

First, in order to include it to your Maven project, simply add this dependency:

``` xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdAsm</artifactId>
    <version>1.0.8</version>
</dependency>
``` 

## How XsdAsm works?

In order to provide a better understanding of this library we need a quick information about XSD language. XSD files are based in two different type of values,
elements and attributes. How those two different values work? Elements are the more complex value type, they can have attributes and contain other elements. 
Attributes on the other hand are defined by a type and a value, which can have restrictions. With that in mind XsdAsm created a base infrastructure 
that supports every generated API as shown below:

(Image here)

The Attribute and Element interfaces serve as a base to all elements and attributes that will be present in any given API, with AbstractElement as an abstract class
from which the Elements will derive. This abstract class contains a list of attributes and elements present in the concrete element and other shared features from elements.
BaseAttribute serves as a base to every Attribute that validates their restrictions.
In the diagram the Html and AttrManifestString classes are shown as concrete implementations of AbstractElement and BaseAttribute.

### Examples

Using the Html element from the HTML5 specification a simple example will be explained, which can be extrapolated to other elements. Some simplification will be made in this example
for easier understanding.

``` xml
<xs:element name="html">
    <xs:complexType>
        <xs:choice>
            <xs:element ref="body"/>
            <xs:element ref="head"/>
        </xs:choice>
        <xs:attributeGroup ref="commonAttributeGroup" />
        <xs:attribute name="manifest" type="xsd:anyURI" />
    </xs:complexType>
</xs:element>
```

In this example an element, html, is described. This html element will originate a class, Html, which will extend from AbstractElement.  
In the example the xsd:attribute says that an attribute, manifest, can be present in the Html element. This requires two things, first, the creation of a class named AttrManifest
that extends BaseAttribute and second a method should be created in the Html class that allows the insertion of an instance of AttrManifest with a given value. 
Regarding attributes there is also present a reference to an attributeGroup named commonAttributeGroup. An attributeGroup is, as the name indicates, an element that contains multiple
attributes. 
In this case instead of adding all the members of the commonAttributeGroup in the Html element a interface is created, with default methods that 
add attributes to the concrete element. This addition works exactly as the example with AttrManifest, but saves the code repetition in all the elements that share that 
attributeGroup.  
Having all the attribute related solved, the elements body and head remain. Similar to the situation with attributes two things need to happen, the creation of the respective 
element classes and the creation of methods in order to support the addition of body and head elements as children of this Html element.

``` java
public class Html extends AbstractElement implements CommonAttributeGroup {
    public Html() { }
    
    public Html attrManifest(String attrManifest) {
        this.addAttr(new AttrManifest(attrManifest));
    }
    
    public Body body() {
        this.addChild(new Body());
    }
        
    public Head head() {
        this.addChild(new Head());
    }
}
```