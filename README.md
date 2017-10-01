# Java Extensions

Utility classes for common Java stuff.

### Val

Better methods than `Optional<>` to deal with nulls. TODO - write more about this.

```java
isEmpty(var);              // Better empty checking with support for Strings, Collections, and Maps.
notEmpty(var);             // Better not-empty checking. More readable than !isEmpty();
anyEmpty(v1, v2);          //
allEmpty(v1, v2);          //
orIfEmpty(var, default);   //
throwIfEmpty(var, errMsg); //
safe(myVar);               // For avoiding nulls - returns [myVar] or a non-null empty value.
```

### RegexBuilder

Regular expression management, simplified.

```java
// This example is a nicer way to create the regex: "I\s+have\s+a\s+pet\s+(?:dog|cat)".
RegexBuilder.regex("I have a pet dog|cat")
    .simpleSpaces()
    .simpleOrs()
    .buildPattern();

// Supports transitive variables!
// Reads so much nicer than: "Email: [A-Za-z0-9._-]+@[A-Za-z0-9._-]+"
RegexBuilder.regex("Email: {email}")
    .var("{word}", "[A-Za-z0-9._-]+")
    .var("{email}", "{word}@{word}")
    .buildPattern();
```

### Func

Easier exception handling, especially for Java 8 lambdas. TODO - write about functional programming and checked exceptions in Java.

```java
// This example builds a Supplier<> that wraps checked exception methods,
// ignores thrown exceptions, and instead returns a default value.
// Fantastic for lambdas.
Func.supplier(x -> suppFunc(y))
    .ignoreExceptions()
    .defaultValue(z)
    .get();
```
