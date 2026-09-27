# Maven Coordinates: groupId, artifactId, version

Every Maven project, and every library it depends on, is identified by three values
called its **coordinates**. They show up all over `pom.xml`, so it helps to know what
each one means.

## The three values

| Field        | What it is                                  | ClubHub value  |
|--------------|---------------------------------------------|----------------|
| `groupId`    | Who owns the project (like a namespace)     | `com.clubhub`  |
| `artifactId` | The name of this specific project           | `clubhub`      |
| `version`    | Which release of it this is                 | `1.0-SNAPSHOT` |

Written together they look like `com.clubhub:clubhub:1.0-SNAPSHOT`.

- **groupId** is usually a reversed domain name (`org.springframework.boot`,
  `com.h2database`). It groups related projects by the same owner.
- **artifactId** names the one thing being built. One group can own many artifacts,
  e.g. Spring's group has `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, ...
- **version** ending in `-SNAPSHOT` means "work in progress, not a final release."

## What the artifactId actually affects

It names the file Maven builds. When you run `./mvnw package`, you get:

```
target/clubhub-1.0-SNAPSHOT.jar
        └─artifactId─┘└version─┘
```

That's it. It's a label for the build output.

## What it does NOT affect

The coordinates are **not** connected to your Java packages or folders. These are
two separate systems that just happen to look similar:

| Thing                        | Controlled by                           |
|------------------------------|-----------------------------------------|
| Jar file name                | `artifactId` + `version` in `pom.xml`   |
| Java package of a class      | the `package com.clubhub...;` line      |
| Where the `.java` file lives | folders under `src/main/java/`          |

The package line and the folder path must match each other
(`package com.clubhub.controller;` lives in `src/main/java/com/clubhub/controller/`).
The groupId/artifactId don't have to match either of them. ClubHub keeps them
similar (`com.clubhub`) only because it's easier to read.

So renaming the artifactId from `backend` to `clubhub` changed the jar name and
nothing else. No Java files needed to change.

## Same coordinates, used for dependencies

The `<dependency>` blocks in `pom.xml` use the exact same system to say *which*
library to download:

```xml
<dependency>
    <groupId>com.h2database</groupId>   <!-- who publishes it -->
    <artifactId>h2</artifactId>         <!-- which library -->
    <scope>runtime</scope>
</dependency>
```

Maven looks these up on Maven Central (https://central.sonatype.com), a public
registry of libraries. Search a name there and you'll see its groupId/artifactId.

**Why no `<version>` on our dependencies?** The `<parent>` block
(`spring-boot-starter-parent`) comes with a list of versions that Spring Boot has
tested together, so Maven fills them in. For a library Spring Boot doesn't know
about, you'd add a `<version>` yourself.

## The `<parent>` block

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

This is also just coordinates, pointing at another pom that ours inherits settings
from: the dependency versions mentioned above, Java compiler settings, and plugin
configuration. Upgrading Spring Boot mostly means changing this one version number.

## Quick reference

- Change the jar name → edit `artifactId`
- Move a class to another package → change its `package` line **and** move the file
- Add a library → add a `<dependency>` with its groupId + artifactId (find them on Maven Central)
- Upgrade Spring Boot → change the `<parent>` version

More: https://maven.apache.org/guides/mini/guide-naming-conventions.html
