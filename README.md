# AlpsLib

## Modules

You find the latest version of the AlpsLib modules on the AlpsBTE Maven Repository:  
https://mvn.alps-bte.com/#browse/browse:alps-bte:com%2Falpsbte%2Falpslib
Currently there is no wiki how to use the modules, but you can look up the source code of the modules.

Repository
```xml
<repositories>
    <repository>
        <id>alpsbte-repo</id>
        <url>https://mvn.alps-bte.com/repository/alps-bte/</url>
    </repository>
</repositories>
```

Replace ```latest``` with the version of the module you want to use. You can find a list of all versions by clicking the link above.

### [AlpsLib-Geo](https://mvn.alps-bte.com/#browse/browse:alps-bte:com%2Falpsbte%2Falpslib%2Falpslib-geo)

This [module](./geo/README.md) can be used to get geo information (like country or city) of a location identified by latitude and longitude. This is possible both offline and over https using Nominatim.

```xml
<dependencies>
    <dependency>
        <groupId>com.alpsbte.alpslib</groupId>
        <artifactId>alpslib-geo</artifactId>
        <version>latest</version>
    </dependency>
</dependencies>
```

### [AlpsLib-Hologram](https://mvn.alps-bte.com/#browse/browse:alps-bte:com%2Falpsbte%2Falpslib%2Falpslib-hologram)

Includes an abstract HologramDisplay which can be used to create custom holograms.
We currently support DecentHolograms - look up the api version in th modules [pom.xml](./hologram/pom.xml).
Newer versions may work or may not work, depending on breaking api changes.

```xml
<repositories>
        <repository> <!-- DecentHolograms -->>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
        <groupId>com.alpsbte.alpslib</groupId>
        <artifactId>alpslib-hologram</artifactId>
        <version>latest</version>
        <scope>compile</scope>
    </dependency>
    
    <!-- HolographicDisplays -->
    <dependency>
        <groupId>com.github.decentsoftware-eu</groupId>
        <artifactId>decentholograms</artifactId>
        <version>latest</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### [AlpsLib-IO](https://mvn.alps-bte.com/#browse/browse:alps-bte:com%2Falpsbte%2Falpslib%2Falpslib-io)

Includes Config-Manager, Language-Manager and Database-System.   
The language system is replaced by [LangLibs](https://github.com/Cinnazeyy/LangLibs).

```xml
<dependencies>
    <dependency>
        <groupId>com.alpsbte.alpslib</groupId>
        <artifactId>alpslib-io</artifactId>
        <version>latest</version>
    </dependency>
</dependencies>
```

If you want tu use our database system, you also need to add the following dependencies:

```xml

<dependencies>
    <!-- MariaDB Connector - https://central.sonatype.com/artifact/org.mariadb.jdbc/mariadb-java-client-->
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <version>latest</version>
        <scope>compile</scope>
    </dependency>
</dependencies>    
```

### [AlpsLib-Utils](https://mvn.alps-bte.com/#browse/browse:alps-bte:com%2Falpsbte%2Falpslib%2Falpslib-utils)

Includes ItemBuilder & LoreBuilder, CustomHeads and other useful utilities.
```Important: This module needs to be included when using the AlpsLib-IO module.```

```xml
<dependencies>
    <dependency>
        <groupId>com.alpsbte.alpslib</groupId>
        <artifactId>alpslib-utils</artifactId>
        <version>latest</version>
    </dependency>
</dependencies>
```