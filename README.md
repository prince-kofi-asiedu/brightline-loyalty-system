# Brightline Loyalty — Points Service

Brightline runs a chain of shops with a loyalty scheme. This service works out how many loyalty points a member earns on a purchase. It applies the scheme's tier rules and its big-spender bonus.

## Running it

You need **JDK 21** and **Maven 3.6.3 or newer** (3.9.x recommended). Maven downloads everything else.

### Versions

All versions are pinned in `pom.xml`, so every machine builds with the same set.
| Purpose | Artifact | Version | Scope |
|---|---|---|---|
| Java language level | `maven.compiler.release` | 21 | — |
| Test framework | `org.junit:junit-bom` (manages the two below) | 5.13.4 | import |
| | `org.junit.jupiter:junit-jupiter` | 5.13.4 (from the BOM) | test |
| | `org.junit.platform:junit-platform-launcher` | from the BOM (lets PIT run JUnit 5 tests) | test |
| Mocking | `org.mockito:mockito-core` | 5.18.0 | test |
| | `org.mockito:mockito-junit-jupiter` | 5.18.0 | test |
| Test runner | `org.apache.maven.plugins:maven-surefire-plugin` | 3.2.5 | build plugin |
| Code coverage | `org.jacoco:jacoco-maven-plugin` | 0.8.14 | build plugin |
| Mutation testing | `org.pitest:pitest-maven` | 1.16.1 | build plugin |
| | `org.pitest:pitest-junit5-plugin` | 1.2.1 | PIT plugin dependency |
| Integration test runner | `org.apache.maven.plugins:maven-failsafe-plugin` | 3.2.5 (surefire.version) | build plugin |
The application itself has no runtime dependencies.

The versions live in `pom.xml` as properties:

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.version>5.13.4</junit.version>
    <mockito.version>5.18.0</mockito.version>
    <jacoco.version>0.8.14</jacoco.version>
    <pitest.version>1.16.1</pitest.version>
    <pitest.junit5.version>1.2.1</pitest.junit5.version>
    <surefire.version>3.2.5</surefire.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit</groupId>
            <artifactId>junit-bom</artifactId>
            <version>${junit.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-launcher</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**See the service run (a small demo, no tests):**

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.brightline.loyalty.Demo
```

**Run the existing tests:**

```bash
mvn test
```

**Measure your tests** (coverage and mutation testing)

```bash
mvn verify                                                  # tests + JaCoCo report: target/site/jacoco/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # PIT report: target/pit-reports/index.html
```


