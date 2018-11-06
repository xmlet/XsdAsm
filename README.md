[![Maven Central](https://img.shields.io/maven-central/v/com.github.xmlet/xsdAsm.svg)](https://search.maven.org/#artifactdetails%7Ccom.github.xmlet%7CxsdAsm%7C1.0.8%7Cjar)
[![Build](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=coverage)](https://sonarcloud.io/component_measures/domain/Coverage?id=com.github.xmlet%3AxsdAsm)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.github.xmlet%3AxsdAsm&metric=bugs)](https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm)

# XsdAsm

<div align="justify"> 
    XsdAsm is a library dedicated to generate a fluent java DSL based on a XSD file. It uses 
    <a href="https://github.com/xmlet/XsdParser">XsdParser</a> library to parse the XSD file into a list of Java classes 
    that XsdAsm will use in order to obtain the information needed to generate the correspondent classes. In order to 
    generate classes this library uses the <a href="http://asm.ow2.org/">ASM library</a>,  which is a library that provides a Java interface to perform 
    bytecode manipulation, which allows for the creation of classes, methods, etc. 
    <br />
    <br />
    This library main objective is to generate a fluent Java DSL based on an existing XSD DSL. It aims 
    to verify the largest number of the restrictions defined in the XSD DSL. It uses the Java compiler to perform most 
    validations and in some cases where such isn't possible it performs run time validations, throwing exceptions 
    if the rules of the used language are violated.
</div>

## Installation

<div align="justify"> 
    First, in order to include it to your Maven project, simply add this dependency:
    <br />
    <br />
</div>

```xml
<dependency>
    <groupId>com.github.xmlet</groupId>
    <artifactId>xsdAsm</artifactId>
    <version>1.0.14</version>
</dependency>
``` 

## How does XsdAsm works?

<div align="justify"> 
    The XSD language uses two main types of values: elements and attributes. Elements are complex value types, they can 
    have attributes and contain other elements. Attributes are defined by a type and a value, which can have restrictions. 
    With that in mind XsdAsm created a common set of classes that supports every generated DSL as shown below:
    <br />
    <br />
</div>
<p align="center">
    <img src="https://raw.githubusercontent.com/xmlet/HtmlApiTest/master/src/test/resources/infrastructure.png"/>
</p>
<div align="justify"> 
    <br />
    The <i>Attribute</i> and <i>Element</i> interfaces serve as a base to all attribute and element classes that will be generated in any 
    generated DSL, with <i>AbstractElement</i> as an abstract class from which the concrete element classes will derive. This abstract 
    class contains a list of attributes and elements present in the concrete element and other shared features from elements.
    <i>BaseAttribute</i> serves as a base to every <i>Attribute</i> that validates their restrictions.
    In the diagram the <i>Html</i> and <i>AttrManifestString</i> classes are shown as concrete implementations of 
    <i>AbstractElement</i> and <i>BaseAttribute</i>, respectively.
</div>

### Concrete Usage

<div align="justify"> 
    XsdAsm provides a <i>XsdAsmMain</i> class that receives two arguments, the first one being the XSD file path and the second 
    one is the name of the DSL to be generated. All the generated DSLs are placed in the same base package, <i>org.xmlet</i>, 
    the difference being the chosen DSL name, for example, if the DSL name is <i>htmlapi</i>, the resulting package name 
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

<div align="justify"> 
    The generated classes will be written in the target folder of the invoking project. For example, the 
    <a href="https://github.com/xmlet/HtmlApi/blob/master/create_class_binaries.bat">HtmlApi</a> project 
    invokes the <i>XsdAsmMain</i>, generating all the HmlApi classes and writing them in the HtmlApi target folder, this 
    way when HtmlApi is used as a dependency those classes appear as normal classes as if they were manually created.
</div>

### Examples

<div align="justify"> 
    Using the <i>Html</i> element from the HTML5 specification a simple example will be presented, which can be extrapolated to 
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

<div align="justify"> 
    With this example in mind what classes will need to be generated?
    <br />
    <br />
    <i>Html Class</i> - A class that represents the <i>Html</i> element, represented in XSD by the <i>xs:element name="html"</i>, deriving from <i>AbstractElement</i>.  <br />
    <i>body and head Methods</i> - Both methods present in the <i>Html</i> class that add <i>Body</i> and <i>Head</i> instances to <i>Html</i> children. This methods are created 
                                    due to their presence in the <i>xs:choice</i> XSD element. <br />  
    <i>attrManifest Method</i> - A method present in <i>Html</i> class that adds an instance of the <i>AttrManifestString</i> attribute to the <i>Html</i> attribute list. This 
                                    method is created because the XSD <i>html</i> element contains a <i>xs:attribute name="manifest"</i> with a <i>xsd:anyURI</i> type, which maps
                                    to <i>String</i> in Java. 
    <br />
    <br />
</div>  

```java
public class Html extends AbstractElement implements CommonAttributeGroup {
    public Html() { }
    
    public Html attrManifest(String attrManifest) {
        this.addAttr(new AttrManifestString(attrManifest));
    }
    
    public Body body() {
        this.addChild(new Body());
    }
        
    public Head head() {
        this.addChild(new Head());
    }
}
```

<div align="justify"> 
    <i>Body and Head classes</i> - Classes for both <i>Body</i> and <i>Head</i> elements, created based on their respective XSD <i>xsd:element</i>.
    <br />
    <br />
</div> 

```java
public class Body extends AbstractElement {
    // Contents based on the respective xsd:element name="body"
}
```

```java
public class Head extends AbstractElement {
    // Contents based on the respective xsd:element name="head"
}
```
<div align="justify"> 
    <i>AttrManifestString Attribute</i> - A class that represents the <i>Manifest</i> attribute, deriving from <i>BaseAttribute</i>. Its type is 
                                        <i>String</i> because the XSD type <i>xsd:anyURI</i> maps to the type <i>String</i> in Java.
    <br />
    <br />
</div> 

```java
public class AttrManifestString extends BaseAttribute<String> {
   public AttrManifestString(String attrValue) {
      super(attrValue);
   }
}
```

<div align="justify"> 
    <i>CommonAttributeGroup Interface</i> - An interface with default methods that add the group attributes to the element which implements this interface.
    <br />
    <br />
</div> 

```java
public interface CommonAttributeGroup extends Element {

   //Assuming CommonAttribute is an attribute group with a single 
   //attribute named SomeAttribute with the type String.
   default Html attrSomeAttribute(String attributeValue) {
      this.addAttr(new SomeAttribute(attributeValue));
      return this;
   }
}
```

### Type Arguments

<div align="justify">
    As we've stated previously, the DSLs generated by this project aim to guarantee the validation of the set of rules associated
    with the language. To achieve this we heavily rely on Java types, as shown above, i.e. the <i>Html</i> class can only 
    contain <i>Body</i> and <i>Head</i> instances as children and attributes such as <i>AttrManifest</i> or any attribute 
    belonging to <i>CommonAttributeGroup</i>. This solves our problem, but since we are using a fluent approach to the generated
    DSLs another important aspect is to always mantain type information. To guarantee this we use type parameters, also known
    as generics.
    <br />
    <br />
</div>

```java
class Example{
    void example(){
        Html<Element> html = new Html<>();
        Body<Html<Element>> body = html.body();
        
        P<Header<Body<Html<Element>>>> p1 = body.header().p();
        P<Div<Body<Html<Element>>>> p2 = body.div().p();
        
        Header<Body<Html<Element>>> header = p1.__();
        Div<Body<Html<Element>>> div = p2.__();
    }        
}
```

<div align="justify">
    In this example we can see how the type information is mantained. When each element is created it receives the parent
    type information, which allows to keep the type information even when we navigate to the parent of the current element.
    A good example of this are both <i>P</i> element instances, <i>p1</i> and <i>p2</i>. Both share their type, but each 
    one of them have diferent parent information, <i>p1</i> is a child of an <i>Header</i> instance, while <i>p2</i> is
    a child of a <i>Div</i> instance. When the method that navigates to the parent element is called, the <i>__()</i> method,
    each one returns its respective parent, with the correct type.
</div>

### Restriction Validation

<div align="justify"> 
    In the description of any given XSD file there are many restrictions in the way the elements are contained in each 
    other and which attributes are allowed. Reflecting those same restrictions to the Java language we have two ways of 
    ensure those same restrictions, either at runtime or in compile time. This library tries to validate most of the 
    restrictions in compile time, as shown in the example above. But in some restrictions it isn't possible to validate 
    in compile time, an example of this is the following restriction:
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

<div align="justify"> 
    In this example we have an element that has an attribute called <i>valueList</i>. This attribute has some restrictions, it 
    is represented by a <i>xsd:list</i> and its element count should be between 1 and 5. Transporting this example to the Java 
    language it will result in the following class:
    <br />
    <br />
</div>

```java
public class AttrIntList extends BaseAttribute<List<Integer>> {
   public AttrIntList(List<Integer> attrValue) {
      super(attrValue, "intList");
   }
}
```

<div align="justify"> 
    But with this solution the <i>xsd:maxLength</i> and <i>xsd:minLength</i> restrictions are ignored. To solve this problem the 
    existing restrictions of any given attribute are hardcoded in the class constructor. This will result in method calls 
    to validation methods, which verify the attribute restrictions whenever an instance is created. If the instances fails
    any validation the result is an exception thrown by the validation methods.
</div>

```java
public class AttrIntList extends BaseAttribute<List<Integer>> {
   public AttrIntList(List<Integer> attrValue) {
      super(attrValue, "intList");
      RestrictionValidator.validateMaxLength(5, attrValue);
      RestrictionValidator.validateMinLength(1, attrValue);
   }
}
```

#### Enumerations

<div align="justify"> 
    In regard to the restrictions there is a special restriction that can be enforced at compile time, the <i>xsd:enumeration</i>. 
    In order to obtain that validation at compile time the XsdAsm library generates <i>Enum</i> classes that contain all the 
    values indicated in the <i>xsd:enumeration</i> tags. In the following example we have an attribute with three possible 
    values: command, checkbox and radio. 
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

<div align="justify"> 
    This results in the creation of an <i>Enum</i>, <i>EnumTypeCommand</i>, as shown below. This means that any attribute that uses 
    this type will receive an instance of <i>EnumTypeCommand</i> instead of receiving a <i>String</i>. This guarantees at 
    compile time that only the allowed set of values are passed to the respective attribute.
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

<div align="justify"> 
    This library also uses the Visitor pattern. Using this pattern allows different uses for the same DSL, given that 
    different Visitors are implemented. Each generated DSL will have one <i>ElementVisitor</i>, this class is 
    an abstract class which contains four main <i>visit</i> methods:
    <br />
    <br />
    <ul>
        <li>
            <i>sharedVisit(Element<T, ?> element)</i> - This method is called whenever a class generated based on a XSD <i>xsd:element</i> has its
            <i>accept</i> method called. By receiving the <i>Element</i> we have access to the element children and attributes.
        </li>
        <li>
            <i>visit(Text text)</i> - This method is called when the <i>accept</i> method of the special <i>Text</i> element is invoked.
        </li>
        <li>
            <i>visit(Comment comment)</i> - This method is called when the <i>accept</i> method of the special <i>Comment</i> element is invoked.
        </li>
        <li>
            <i>visit(TextFuction<R, U, ?> textFunction)</i> - This method is called when the <i>accept</i> method of the special 
                                                        <i>TextFunction</i> element is invoked.
        </li>
    </ul>
    <br />
    <br />
    Apart from this four methods we have create specific methods for each element class created, e.g. the <i>Html</i> class.
    This introduces a greater level of control, since the concrete <i>ElementVisitor</i> implementation can manipulate 
    each <i>visit</i> method in a different way. These specific methods invoke the <i>sharedVisit</i> as their default behaviour, 
    as shown below.
</div>

```java
public class ElementVisitor {
    // (...)
    
    default void visit(Html html) {
        this.sharedVisit(html);
    }
}
```

### Element Binding

<div align="justify">  
    To support the definition of reusable templates the <i>Element</i> and <i>AbstractElement</i> classes were changed to support binders. 
    This allows programmers to postpone the addition of information to the defined element tree. An example is shown below.
    <br />
    <br />
</div>

```java
public class BinderExample{
    public void bindExample(){
        Html<Element> root = new Html<>()
            .body()
                .table()
                    .tr()
                        .th()
                            .text("Title")
                        .__()
                    .__()
                    .<List<String>>binder((elem, list) ->
                        list.forEach(tdValue ->
                            elem.tr().td().text(tdValue)
                        )
                    )
                .__()
            .__()
        .__();
    }
 }
```

<div align="justify"> 
    In this example a <i>Table</i> instance is created, and a <i>Title</i> is added in the first row as a title header, i.e. <i>th</i>. 
    After defining the table header of the table we can see that we invoke a <i>binder</i> method. This method bounds the <i>Table</i>
    instance with a function, which defines the behaviour to be performed when this instance receives the information.
    This way a template can be defined and reused with different values. A full example of how this works is available at 
    the method <a href="https://github.com/xmlet/HtmlApiTest/blob/master/src/test/java/org/xmlet/htmlapitest/HtmlApiTest.java">testBinderUsage</a>.
</div>

## Code Quality

<div align="justify"> 
    There are some tests available using the HTML5 schema and the Android layouts schema, you can give a look at that 
    examples and tweak them in order to gain a better understanding of how the class generation works. The tests also 
    cover most of the code, if you are interested in verifying the code quality, vulnerabilities and other various 
    metrics, check the following link:
    <br />
    <br />
    <a href="https://sonarcloud.io/dashboard?id=com.github.xmlet%3AxsdAsm">Sonarcloud Statistics</a>
</div>

## Final remarks

<div align="justify"> 
    Some examples presented here are simplified in order to give a better understanding of how this library works. In 
    order to allow a better usage for the generated API end user there are multiple improvements made using type arguments.
</div>
 
