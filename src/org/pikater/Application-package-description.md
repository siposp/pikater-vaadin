<!-- --- title: Application package -->

`org.pikater` - contains all application code.

## Core package
Core system is defined in this package, which means all Jade-related code that executes experiments and manages agents and their infrastructure. Documentation of core system can be found [[here|Technical-documentation#core]].

## Shared

Various shared classes and frameworks are contained in this package. "Shared" actually means shared between `core` and `web`.

Main functionalities of this package include:
* Database framework  
Connection definition & providers, entities, queries and so on. See [[database documentation|Database-documentation]].
* Quartz scheduler  
See [[web application documentation|Web-documentation]].
* Universal experiment format  
See [[technical documentation|Technical-documentation]].
* Various utilities and logging interfaces/implementations

## Web

All web application related code is concentrated in this package, with the exception of shared code.

More detailed information about this package can be found [[here|Web-package-description]].

## Package dependencies

Dependencies are represented by the following diagram:

{{{{{{ blue-modern
	Core->Shared: requires
	Web->Shared: requires
	alt web-app is in online mode
		Web->Core: requires
	end
}}}}}}

`Core` and `shared` packages are and aim to be completely independent of `web`, but as a standalone unit, they are also very limited in usage and user-experience.

`Web` is only dependent on `core` when it is in "online mode" (see [[web application documentation|Web-documentation#conf]]). To be precise, `web` only calls methods defined in the following class:

```java:/src/org/pikater/core/agents/gateway/WebToCoreEntryPoint.java```