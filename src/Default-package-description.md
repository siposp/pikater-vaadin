<!-- --- title: Default package -->

1. `net.edzard` package  
Contains KineticGWT code. See [[technical documentation|Technical-documentation#web]].
2. `org.pikater` package  
Contains application code. See [[application code description|Application-package-description]].
3. `Beans.xml` file  
Contains resources for [Spring framework](http://projects.spring.io/spring-framework/) used in core system.
4. `log4j.xml` file  
[Log4j](http://logging.apache.org/log4j/1.2/) configuration used by various application frameworks & components (e.g. [[Quartz|Web-documentation]]). At the moment, standard configuration is used.
5. `logging.properties` file  
A special file defining logging configuration for standard Java logging framework that redirects logged messages to a file.  
It was intended as a means to make the logs accessible from browser but is not used after all. The file is kept for potential future reference. If interested in this topic, also see [tclogview](https://github.com/happygiraffe/tclogview).