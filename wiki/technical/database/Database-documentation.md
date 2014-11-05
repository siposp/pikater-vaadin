<!-- --- title: Technical Documentation - Part I: Documentation of Database -->

[[_TOC_]]

## Preface

As can be seen from the main goals of this project, some parts of the system are required to work "offline" and as a standalone component so a database system is necessary to store all data. This document gives an overview of our database solution as well as alternatives and additional information regarding the subject.

## Terminology

**Database related:**
* __DBMS__ (Database management system)
* __ACID__ (Atomicity, Consistency, Isolation, Durability)
* __SQL__ (Structured Query Language)
* __JPQL__ (Java Persistence Query Language)
* __JDBC__ (Java Database Connectivity)
* [__JPA__](http://docs.oracle.com/javaee/6/tutorial/doc/bnbpz.html) (Java Persistence API)

**JPA:**
* __DAO__ (Data Access Object)
* __Entity__  
A Java class that defines a relational-database table schema and whose instances represent a row of the said table.

**File formats:**
* __CSV__ (Comma-Separated Values)  
A "standard" file format representing data as a table.
* __ARFF__ (Attribute-Relation File Format)  
A widely used file format representing data for machine learning, specifically the WEKA library which is discussed briefly in core documentation.

**Pikater:**
* __Dataset__  
Alias for ARFF files used as input data for experiments.

## DBMS design

### Original Pikater

The original Pikater project used an embedded database system (MySQL) that only worked locally on the same machine. Individual core system agents dynamically constructed SQL queries and directly queried the database with them, via a JDBC driver. Much of the application logic was massively hardcoded.

### Extended Pikater

Specification of the extended Pikater project made it clear that the above approach will no longer be sufficient. Successful implementation of our project more or less advocated to double the amount of code and, subsequently, introduce a high level of modularity and modifiability. Pikater could no longer be bound to a specific DBMS and allow individual classes to directly communicate with a database on a low-level. The worst case scenario would have been an alteration in database schema which, during development of our project, was bound to happen very frequently.

#### JPA

Therefore, the first step towards the new goals was introduction of a DBMS-independent interface/framework that works with JDBC, i.e. JPA. Main advantages of JPA:
* As already stated, it provides a sort of "portable" database schema.
* Allows for mapping database records to Java objects.
* Works with practically all mainstream/major database systems and is built upon "standard" and well documented JDBC.
* Provides a higher-level interface to manipulate with database state (higher than JDBC).

#### JPA Connector

Once we decided to use JPA, we also needed to pick a JPA connector - low-level JPA implementation that actually handles all of the SQL and object mapping under the hood (something that connects the world of Java with the world of databases).

The most widespread JPA connectors are Hibernate and Eclipselink. Picking either of them wouldn't have deep development impacts because they are quite comparable. Both of them have some special features, however, and we decided to use Eclipselink because it allows runtime configuration of database connectivity. Currently, configuration files are used but the feature may still come in handy in future.

#### PostgreSQL

The final step in DBMS design was to pick a particular DBMS system to store all our data.

We considered the following three DBMSs:
* MySQL
* PostgreSQL
* HyperSQL DB (HSQLDB)

All of them can be obtained and used for free in some form. Our chosen DBMS must have satisfied several other criteria, however:
* support for multiple platforms,
* support for transactional queries,
* ability to store large files (hundreds of MiBs).

The last criteria was especially important because it directly affected efficiency of the data distribution problem within the core system.

Decision was to be made from the following table:

|               |   MySQL         |   HSQLDB         | PostgreSQL           |
|:-------------:|:---------------:|:----------------:|:--------------------:|
| transactions  | yes (InnoDB)    | yes              | yes                  |
| files (LOBs)  | yes             | yes (64TiB for all)| yes (4TiB for each file)|
| other         | used in original project, widespread | Java  | widespread usage     |

Despite the fact that MySQL supports transactional queries, we could not use it because they were only supported by the commercial InnoDB engine. The only two remaining options were HSQLDB and PostgreSQL. Both of them supported storing byte arrays (files) as items in table rows. This convenience would have been very useful for us.

PostgreSQL accesses byte arrays with streams, while HSQLDB uses getters/setters. HSQLDB seems like a more viable option now, but there is a problem - size limit. All in all, PostgreSQL was the better choice, also because it was a lot more widespread and maybe even documented - that alone can be sufficient reason to decide for it.

## DBMS solution

### Database Schema

#### Original

The following picture shows database schema of the original Pikater project:

[[original_db_schema.png|alt=Original DB schema]]

#### Current

The following picture shows database schema of the extended Pikater project:

[[schemaspy_output/diagrams/summary/relationships.real.large.png|alt=Current DB schema]]

#### Differences

We tried to keep the original schema as much as possible but many parts of it had to be changed for some reason. The most notable difference is storing files, however. If it's a small file, entities define a byte array field for it. If it's a large file, they declare an "OID" (Object ID) that points to the file being stored separately in PostgreSQL.

### Static database framework 

This is the "source code result" of all our efforts to provide a nice, clear, concise and statically accessed framework to manipulate with database. All of it can be found in the following package:  
`org.pikater.shared.database`

Subpackages then contain parts of this framework and should be self-explanatory to some degree. We have to comment the `org.pikater.shared.database.jpa` package a little more, however, and the following chapters will give a nice overview of what's in it and how to use it.

#### Entities

Entities are Java classes whose names (in our project) are prefixed with "JPA" (e.g. `JPAUser`) and which are specially annotated/configured with JPA.

Currently we use 16 entities in Pikater, all are located in the `org.pikater.shared.database.jpa` package. Each entity inherits from `JPAAbstractEntity` which defines some commons fields/interface for all entities.

##### User related

1. **JPAUser**  
Entity representing a user-account. Other entities reference this entity if they are directly linked to a single user in a "parent-child" relationship. User accounts contain basic information about a user (e.g. login, password hash, email etc.) and may be active or suspended. Currently, newly created accounts are suspended until an administrator activates them.

2. **JPAUserPrivilege**  
Entity representing an access right to a particular feature. Despite the fact that all privileges are hardcoded in the `org.pikater.shared.database.jpa.PikaterPrivilege` enumeration, we also wrapped them into an entity (see below) to provide a base for any future modifications/enhancements. While it was not necessary to do so because user entities might contain a list of privileges, it was a compromise from our part to provide more flexibility to future development teams.

3. **JPARole**  
Entity representing a set of privileges for a particular user. Currently, there are only 2 instances of this entity in database - "admin role" and "regular user role" but it can be easily modified to represent a user-group. Mind you, though, a proper and robust implementation of user groups is not as simple as it seems to be and this class alone should not be enough.

##### Dataset related

1. **JPADataSetLO**  
Entity representing datasets (mainly input data for experiments). Beside an OID discussed above, it also contains an MD5 hash of the dataset. While multiple instances of this entity with identical hash may be stored in database, the system will consider the underlying datasets as equal to each other (it just copies OID of the original instance to the new one), so application logic may in some cases use an unintended instance of `JPADataSetLO`. Metadata are also very important as they allow seamless similarity comparison for two given datasets without inspecting actual content.

2. **JPAGlobalMetaData**  
Entity representing metadata common for all dataset types, e.g. how many entries (data rows) they contain and what kinds of attributes (columns) they define.

3. **JPAAttributeMetadata**  
Entity representing metadata for an attribute (column) of a dataset, for instance:
	* ratio of missing values,
	* class of entropy.

4. **JPAAttributeCategoricalMetadata**  
Entity representing metadata for a categorical attribute (column) - in other words, values of this column are items from a finite and enumerated set.

5. **JPAAttributeNumericalMetadata**  
Entity representing metadata for a numerical attribute (column). Values of this column are floating point numbers.

6. **JPATaskType**  
Entity representing an experiment that performed well on the associated dataset.  
_Experiments can not perform the same (in terms of speed) on all datasets. Some datasets are more suited for certain types of experiments and it is difficult to determine the optimal one._

##### Experiment related

Experiment refers to an instance of a machine learning problem.

1. **JPABatch**  
Entity representing an experiment. When a user creates a new experiment using the web interface and queues it for execution, it is converted into a special object and that object is then serialized into a XML document. The XML document is stored in this entity. Convenience data regarding experiments is also provided - owner, current status, priority, results etc.

2. **JPAExperiment**  
Entity representing a part of an experiment. Experiments, when queued for execution, are divided into subexperiments  that are executed independently, hence a new entity is needed.
`JPABatch` actually contains a list of `JPAExperiment` entities that hold the computed results.

3. **JPAModel**  
Entity representing a machine-learning model (agent) trained within a given subexperiment. Models have expiration dates and are deleted automatically, unless they are marked for permanent storage. Users will typically only want to keep the best models so that they can be used in further experiments.

4. **JPAResult**  
Entity representing statistics about a given subexperiment. The statistics are mainly various "errors" associated to a certain model, whether stored in database or not.

5. **JPAExternalAgent**  
Entity representing a user-defined machine learning algorithm, method and so on in the form of a compiled JADE agent (".jar" file). All user defined agents need to be approved by an administrator prior to being used in an experiment.

6. **JPAAgentInfo**  
Entity representing core system agents (and their "settings"), whether system defined or user defined, that can be used in experiments. Without this entity, graphical experiment editor could not be made.

##### Core-system related

1. **JPAFilemapping**  
Entity representing a pair of dataset names - the original source file name and internal name (the file's MD5 hash). More information about this concept can be found in core documentation.

#### Data Access Objects (DAO)

Java classes with names with a suffix of "DAO". All are defined in the following package:  
`org.pikater.shared.database.jpa.daos`

Their purpose is to manage entities. Each DAO:
* can be accessed via a static field of the `org.pikater.shared.database.jpa.daos.DAOs` class,
* primarily manages exactly 1 entity,
* provides interface to retrieve, store, update or delete entity instances,
* may, as a side effect, sometimes store, update or delete instances of another entity.

Example of usage:
```java:/src/org/pikater/shared/database/jpa/Showcase.java```

Designs identical or similar to this one have become very common, boast many advantages while having almost no disadvantages.

Normally, one of the biggest problems is keeping entities in "persistence context" so that they can still be used for updates, without querying database for the same entity again. On the other hand, it's a good practice to keep the persistence context as small as possible.

Introduction of DAOs is one of the solutions to tackle the problem of persistence context. Their functions, when backed by a transaction, don't violate ACID constraints and because all entity-related operations are done through these objects, we don't have to care about persistence context.

#### Querying

The main issue with querying is to make complex queries simple, fast and generic if possible. We focused on the first two. All queries are defined within DAOs (see above).

In Pikater, two querying interfaces are used and each is explained below.

##### JPQL Queries

JPQL is a query language based on SQL and its queries can be created dynamically at runtime. This approach is very flexible, but can be more confusing than other alternatives supported by JPA.

**Named queries:**  
Named queries are pre-defined JPQL queries, stored in the source code of entities (marked by the `@NamedQuery` annotations) or in an external configuration file. A large portion of querying in Pikater is done via named queries constructed in runtime with proper arguments.

The following code snippet is an example of how to use named queries:
```java
@NamedQuery(name="DataSetLO.getByOwner",query="select dslo from JPADataSetLO dslo where dslo.owner=:owner")
```

This query returns all `JPADataSetLO` entity instances that have been stored by user `:owner` and is used by the `DataSetDAO` object in `AbstractDAO.getByTypedNamedQuery("DataSetLO.getByOwner", "owner", user)` method call. The last argument is the parameter to be injected into the query.

##### Criteria Queries

Criteria API is an alternative to JPQL queries. Unlike named queries that must have all parameters filled, Criteria API queries are very flexible. They can be generated dynamically at runtime and unlike JPQL queries, also provide much better type safety.

The following code snippet is an example of Criteria API query that returns all datasets with a hash that is not included in the `hashesToBeExcluded` list:

```java
	CriteriaBuilder cb= em.getCriteriaBuilder();
	CriteriaQuery<JPADataSetLO> cq=cb.createQuery(JPADataSetLO.class);
	Root<JPADataSetLO> r=cq.from(JPADataSetLO.class);
	
	Predicate p=cb.conjunction();
	for(String exHash:hashesToBeExcluded){
		p=cb.and(p,cb.equal(r.get("hash"),exHash).not());
	}
	p=cb.and(p,cb.isNotNull(r.get("globalMetaData")));
	cq=cq.where(p);
```

We use Criteria API queries mainly for web interface where native (database-side) sorting of entities is required.

### Configuration

#### Original

Original Pikater used Spring framework to provide database credentials/connectivity. Everything was stored in the `Beans.xml` file from which the object representing database connection was injected into the application:
```xml
...
<bean id="defaultConnection" class="org.pikater.shared.database.connection.PostgreSQLConnectionProvider" scope="singleton">
	<constructor-arg index="0">
		<value><!-- url --></value>
	</constructor-arg>
	<constructor-arg index="1">
		<value><!-- database username --></value>
	</constructor-arg>
	<constructor-arg index="2">
		<value><!-- password for database user--></value>
	</constructor-arg>
</bean>
...
```

#### Current

Since Spring framework is not needed for the application at all, we considered removing it. Eventually, we decided to keep it, because the original Pikater's authors use it. The aforementioned `Beans.xml` file is located in the source root directory.

For us, however, Spring framework was useless because we needed to develop a central statically accessed database framework built on JPA, more specifically JPA entity manager. For that reason, we implemented an object-based native access to PostgreSQL.

Also, JPA has a configuration of its own so we needed to define a custom `persistence.xml` within `META-INF` directory, located in the source root folder. This file should at least contain a declaration of used JPA entities (java objects), e.g.:  
```xml
<persistence-unit name="pikaterDataModel" transaction-type="RESOURCE_LOCAL">
	<provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
	...
	<class>org.pikater.shared.database.jpa.JPADataSetLO</class>
</persistence-unit>
```

As for database credentials/connectivity, we could use either of the following: Spring framework, JPA configuration or dedicated custom Java objects. We decided to only use JPA configuration because:
* When creating `EntityManagerFactory` (JPA), we can set credentials and other properties dynamically.
* We have more freedom at managing entities.

Injecting an instance of `EntityManagerFactory` class using `Beans.xml` was a challenge because of Spring's internal usage of `Proxy` classes when creating bean instances.  
To be more precise, for every interface implemented by a particular bean, new `Proxy` object is created which must eventually be cast to the type of the interface. `EntityManagerFactory` has many interfaces implemented and naturally, none of them covers the class's full functionality.

For the very same reason we decided not to inject `EntityManager` (created by `EntityManagerFactory`) instances neither.

To create and initialize both of the aforementioned configuration files, you may use the following command line utility:  
`org.pikater.shared.database.util.initialisation.DatabaseInitialisation`

### PostgreSQL specifics

#### Large Objects

As state above, it is PostgreSQL's way of storing LARGE files. Once we decided to use PostgreSQL, implementing a framework dealing with large objects was a question of time.

Large Objects (PgLOBs or simply LOBs) are saved as records (byte arrays) in the `pg_largeobject` system table of a particular database schema. Each LOB consists of a sequence of such records and is identified by an ID (OID).

PostgreSQL offers the `@Lob` annotation for entity fields but it is not recommended for LOBs. Should the worst happen, the whole file is copied to memory before it is stored to database - if memory issues don't rise before that even happens of course.

The complete interface working with LOBs can be found in package:
`org.pikater.shared.database.postgre.largeobject`.

LOBs are referenced in entities via the above mentioned OID. Should a need arise to port the framework to support a different DBMS, an alternative LOBs implementation/handling should be provided and OID references changed accordingly.

### Notes & tips

While it might be best to consult the official JPA documentation, we add some notes in hope that the reader may not have to go through it.
* `@Lob`  
As stated before, we solved storage of large files using PostgreSQL Large Objects. Do note, however, that smaller files can be stored using this JPA annotation - the annotated variable's content is serialized and saved to a byte array (or other compatible type) in the database. We also use this approach to store some small files.
* `@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS` or `InheritanceType.JOINED`)  
This annotation determines the way tables are created for entities in regard to inheritance. `TABLE_PER_CLASS` means that the table the class will be mapped to will contain a column for each of the class's (or its ancestor's) field. On the other hand, `JOINED` means that only ONE table will be created for the given entity AND its descendants.
* `@Enumerated(EnumType.STRING)`  
If we declare a `java.lang.Enum` field inside an entity, we can specify what type will be used to store it in the database - possible values are `EnumType.STRING` and (`EnumType.ORDINAL`). String representation is less time-and-space efficient but increases modifiability - should we reorder the enum's constants or add new ones, previously stored entities will still be valid. All in all, it seems that ordinal representation should only be used if you really know what you're doing.
