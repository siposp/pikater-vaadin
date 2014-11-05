# General

1. Classes used in GWT RPC have to implement `isSerializable` or `Serializable`.
2. Default constructor of any visibility makes GWT happy.
3. `Final` fields are not serialized in GWT.

# Specific

1. `String.format` method is not translatable to GWT.

For more information:  
<http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideSerializableTypes>