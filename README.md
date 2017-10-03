# Java Extensions

Utility classes for common Java stuff.

## Adding to Maven

You can use `java-extensions` via maven:

```xml
<dependency>
    <groupId>com.terheyden</groupId>
    <artifactId>java-extensions</artifactId>
    <version>0.1.8</version>
</dependency>
```

_TODO: Add JAR releases here for non-maven folks._

## What's included

### Val

Short for "value functions." Better methods than `Optional<>` to deal with nulls.
_TODO - write more about this._

```java
isEmpty(var);              // Better empty checking with support for Strings, Collections, and Maps.
notEmpty(var);             // Better not-empty checking. More readable than !isEmpty();
anyEmpty(v1, v2);          //
allEmpty(v1, v2);          //
orIfEmpty(var, def);       // Return [var] if not empty, else return [def].
throwIfEmpty(var, errMsg); // Throw [errMsg] if [var] is empty.
safe(myVar);               // For avoiding nulls - returns [myVar] or a non-null empty value.
```

Val also has a comprehensive set of empty values.

```java
emptyString();
emptyInt();
...
emptyCollection();
emptySet();
emptyList();
emptyObject();
emptyFile();
emptyPath();
...
```

### RegexBuilder

Regular expression management, simplified. Builds normal regular expressions, and also offers some optional niceties.

```java
// This example is a nicer way to create the regex: "I\s+have\s+a\s+pet\s+(?:dog|cat)".

RegexBuilder.regex("I have a pet dog|cat")
    .simpleSpaces()
    .simpleOrs()
    .buildPattern();

// Supports transitive variables!
// Reads so much nicer than: "Email:\s*(?<email>[A-Za-z0-9._-]+@[A-Za-z0-9._-]+)"

RegexBuilder.regex("Email: {email}")
    .simpleSpaces()
    .var("{word}", "[A-Za-z0-9._-]+")
    .var("{email}", "{word}@{word}", "email")
    .buildPattern();

// That third argument, "email", up above is a group name.
// It allows us to later do:

String email = matcher.group("email"); // Instead of matcher.group(1);

```

### Func

Utils for methods and functional programming. Easier exception handling, especially for Java 8 lambdas. _TODO - write about functional programming and checked exceptions in Java._

```java
// This example builds a Supplier<> that wraps checked exception methods,
// ignores thrown exceptions, and instead returns a default value.
// Fantastic for lambdas.

Func.supplier(suppFunc(y))
    .ignoreExceptions()
    .defaultValue(z)
    .get();
```
