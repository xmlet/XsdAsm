[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdAsm.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdAsm%7C1.0.8%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Coverage](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdAsm)
[![Vulnerabilities](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Bugs](https://sonarcloud.io/api/badges/measure?key=com.github.xmlet%3AxsdAsm&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)

# XsdAsm

<div style="text-align:right"> 
    XsdAsm is a library dedicated to generate a fluent java API based on a XSD file. It uses 
    <a href="https://github.com/xmlet/XsdParser">XsdParser</a> library to parse the xsd file into a list of java elements 
    that XsdAsm will use in order to obtain the information needed to generate the correspondent classes. In order to 
    generate classes this library also uses the ASM library,  which is a library that provides a java interface to 
    bytecode manipulation, which allows for the creation of classes, methods, etc.
    <br />
    <br />
    More information at <a href="http://asm.ow2.org/">ASM Website</a>.
</div>

## Installation

<div style="text-align:right"> 
    First, in order to include it to your Maven project, simply add this dependency:
    <br />
    <br />
</div>

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdAsm</artifactId>
    <version>1.0.9</version>
</dependency>
``` 

## How does XsdAsm works?

<div style="text-align:right"> 
    In order to provide a better understanding of this library we need a quick information about XSD language. XSD files 
    are based in two different type of values, elements and attributes. How those two different values work? Elements 
    are the more complex value type, they can have attributes and contain other elements. Attributes on the other hand 
    are defined by a type and a value, which can have restrictions. With that in mind XsdAsm created a base infrastructure 
    that supports every generated API as shown below:
    <br />
    <br />
</div>
<p align="center">
    <img src="https://raw.githubusercontent.com/xmlet/HtmlApiTest/master/src/test/resources/infrastructure.png"/>
</p>
<div style="text-align:right"> 
    <br />
    The <i>Attribute</i> and <i>Element</i> interfaces serve as a base to all attributes and elements that will be present in any 
    given API, with <i>AbstractElement</i> as an abstract class from which the concrete elements will derive. This abstract 
    class contains a list of attributes and elements present in the concrete element and other shared features from elements.
    <i>BaseAttribute</i> serves as a base to every Attribute that validates their restrictions.
    In the diagram the <i>Html</i> and <i>AttrManifestString</i> classes are shown as concrete implementations of <i>AbstractElement</i> and <i>BaseAttribute</i>.
</div>

### Concrete Usage

<div style="text-align:right"> 
    XsdAsm provides a <i>XsdAsmMain</i> class that receives two arguments, the first one being the xsd file path and the second 
    the name of the API to be generated. All the generated APIs are placed in the same base package, <i>org.xmlet</i>, 
    the difference being the chosen API name, for example, if the api name is <i>htmlapi</i>, the resulting package name 
    is <i>org.xmlet.htmlapi</i>.
    <br />
    <br />
</div>

```java
public class Example{
    void generateApi(String filePath, String apiName){
        XsdAsmMain.main(new String[] {filePath, apiName} );    
    }
}
```

<div style="text-align:right"> 
    The generated classes will be written in the target folder of the invoking project. For example, the 
    <a href="https://github.com/xmlet/HtmlApi/blob/master/create_class_binaries.bat">HtmlApi</a> project 
    invokes the <i>XsdAsmMain</i>, generating all the HmlApi needed classes and writes them in the HtmlApi target folder, this 
    way when HtmlApi is used as a dependency those classes appear as normal classes as if they were manually created.
</div>

### Examples

<div style="text-align:right"> 
    Using the <i>Html</i> element from the HTML5 specification a simple example will be explained, which can be extrapolated to 
    other elements. Some simplification will be made in this example for easier understanding.
    <br />
    <br />
</div>

```xml
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

<div style="text-align:right"> 
    With this example in my what classes will need to be generated?
    <br />
    <br />
    <b>Html Element</b> - A class that represents the <i>Html</i> element, deriving from <i>AbstractElement</i>.  <br />
    <b>Body and Head Methods</b> - Both methods present in the <i>Html</i> class that add <i>Body</i> and <i>Head</i> instances to <i>Html</i> children. <br />  
    <b>Manifest Method</b> - A method present in <i>Html</i> class that adds an instance of the <i>Manifest</i> attribute to the <i>Html</i> attribute list.
    <br />
    <br />
</div>  

```java
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

<div style="text-align:right"> 
    <b>Body and Head classes</b> - Classes for both <i>Body</i> and <i>Head</i> elements.
    <br />
    <br />
</div> 

``` java
public class Body extends AbstractElement {
    (...)
}
```

``` java
public class Head extends AbstractElement {
    (...)
}
```
<div style="text-align:right"> 
    <b>Manifest Attribute</b> - A class that represents the <i>Manifest</i> attribute, deriving from <i>BaseAttribute</i>.
    <br />
    <br />
</div> 

```java
public class AttrManifest extends BaseAttribute<String> {
   public AttrManifest(String attrValue) {
      super(attrValue);
   }
}
```

<div style="text-align:right"> 
    <b>CommonAttributeGroup Interface</b> - An interface with default methods that add the group attributes to the concrete element.
    <br />
    <br />
</div> 

```java
public interface CommonAttributeGroup extends Element {

   //Assuming CommonAttribute is an attribute group with a single attribute named SomeAttribute with the type String.
   default Html attrSomeAttribute(String attributeValue) {
      this.addAttr(new SomeAttribute(attributeValue));
      return this;
   }
}
```

### Restriction Validation

<div style="text-align:right"> 
    In the description of any given xsd file there are many restrictions in the way the elements are contained in each 
    other and which attributes are allowed. Reflecting those same restrictions to the Java language we have two ways of 
    ensure those same restrictions, either at runtime or in compile time. This library tries to validate most of the 
    restrictions in compile time, as shown in the example above. But in some restrictions it isn't possible to validate 
    in compile time, examples of this is the following restriction:
    <br />
    <br />
</div>

```xml
<xs:schema>
    <xs:element name="testElement">
        <xs:complexType>
            <xs:attribute name="intList" type="valuelist"/>
        </xs:complexType>
    </xs:element>
    
    <xs:simpleType name="valuelist">
        <xs:restriction>
            <xs:maxLength value="5"/>
            <xs:minLength value="1"/>
        </xs:restriction>
        <xs:list itemType="xsd:int"/>
    </xs:simpleType>
</xs:schema>
```

<div style="text-align:right"> 
    In this example we have an element that has an attribute called valueList. This attribute has some restrictions, it 
    is represented by a <i>xsd:list</i> and its element count should be between 1 and 5. Transporting this example to the Java 
    language it will result in the following class:
    <br />
    <br />
</div>

```java
public class AttrIntList extends BaseAttribute<List> {
   public AttrManifest(List<Integer> list) {
      super(list);
   }
}
```

<div style="text-align:right"> 
    But with this solution the <i>xsd:maxLength</i> and <i>xsd:minLength</i> are ignored. To solve this problem the existing 
    restrictions existing in any given attribute are hardcoded in the class static constructor, which stores the 
    restrictions in a Map object. This way, whenever an instance is created a validation function is called
    in the <i>BaseAttribute</i> constructor and will throw an exception if any restriction present in the Map is violated.
    This way the generated API ensures that any sucessful usage follows the rules previously defined.
</div>

#### Enumerations

<div style="text-align:right"> 
    In regard to the restrictions there is a special restriction that can be enforced at compile time, the <i>xsd:enumeration</i>. 
    In order to obtain that validation at compile time the XsdAsm library generates Enum classes that contain all the 
    values indicated in the <i>xsd:enumeration</i> tags. In the following example we have an attribute with three possible 
    values, command, checkbox and radio. 
    <br />
    <br />
</div>

```xml
<xs:attribute name="type">
    <xs:simpleType>
        <xs:restriction base="xsd:string">
            <xs:enumeration value="command" />
            <xs:enumeration value="checkbox" />
            <xs:enumeration value="radio" />
        </xs:restriction>
    </xs:simpleType>
</xs:attribute>
```

<div style="text-align:right"> 
    This results in the creation of an Enum, <i>EnumTypeCommand</i>, as shown and the attribute will then receive an instance 
    of <i>EnumTypeCommand</i>, ensuring only allowed values are used.
    <br />
    <br />
</div>

```java
public enum EnumTypeCommand {
   COMMAND(String.valueOf("command")),
   CHECKBOX(String.valueOf("checkbox")),
   RADIO(String.valueOf("radio"))
}
```

```java
public class AttrTypeEnumTypeCommand extends BaseAttribute<String> {
   public AttrTypeEnumTypeCommand(EnumTypeCommand attrValue) {
      super(attrValue.getValue());
   }
}
```

### Visitors

<div style="text-align:right"> 
    This library also uses the Visitor pattern. Using this pattern allows different uses for the same API, given that 
    different Visitors are implemented. In the generation of the API two classes are created:
    <br />
    <br />
    <b>ElementVisitor</b> - A interface that contains visit methods for every element present in the generated API. <br />
    <b>AbstractElementVisitor</b> - An abstract class that reroutes all visit method calls to a single one, which 
    allows the concrete Visitor implementation to only override that single visit method or if needed override only the 
    methods with a different implementation.
    <br />
    <br />
    An example of a concrete visitor can be a visitor that writes indented HTML based on the elements received.
</div>

### Element Binding

<div style="text-align:right"> 
    In order to support repetitive tasks over an element binders were implemented. This allows for users to define, 
    for example, templates for a given element. An example is presented below, it uses the Html5 API as example.
    <br />
    <br />
</div>

```java
public class BinderExample{
    public void bindExample(){
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();
        
        Table<Body<Html<Html>>> table = body.table();
        table.tr().th().text("Title");
        table.<List<String>>binder((elem, list) ->
                        list.forEach(tdValue ->
                            elem.tr().td().text(tdValue)
                        )
                    );
        
        //Keep adding elements to the body of the document.
    }
}
```

<div style="text-align:right">
    In this example a table is created, and a title is added in the first row as a title header. In regard to the values 
    present in the table instead of having them inserted right away it is possible delay that insertion by indicating 
    what will the element do when the information is received. This way a template can be defined and reused with 
    different values. A full example of how this works is available at the method <a href="https://github.com/xmlet/HtmlApiTest/blob/master/src/test/java/org/xmlet/htmlapitest/HtmlApiTest.java">testBinderUsage</a>.
</div>

## Code Quality

<div style="text-align:right"> 
    There are some tests available using the HTML5 schema and the Android layouts schema, you can give a look at that 
    examples and tweak them in order to gain a better understanding of how the class generation works. The tests also 
    cover most of the code, if you are interested in verifying the code quality, vulnerabilities and other various 
    metrics, check the following link:
    <br />
    <br />
    <a href="https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm">Sonarcloud Statistics</a>
</div>

## Final remarks

<div style="text-align:right"> 
    Some examples presented here are simplified in order to give a better understanding of how this library works. In 
    order to allow a better usage for the generated API end user there are multiple improvements made using type arguments.
</div>
 