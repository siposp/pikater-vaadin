pikater (fork)
=======

pikantni agentni teritorium (project in jade)
dočasný fork


### Parametry JVM ke spuštění:

-Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl

Tj. např.:

java -Djavax.xml.parsers.DocumentBuilderFactory=com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl -cp jade.jar jade.Boot -local-host localhost -gui  -agents mailAgent:pikater.agents.utility.MailAgent;mailTester:pikater.agents.utility.MailAgentTester
