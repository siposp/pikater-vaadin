This file refers to custom classes that:
* inherit from `AbstractComponentState` and are used in custom Vaadin components,
* are referenced (transitively) from the above classes.

The shared state is automatically kept in sync with the client and Vaadin uses what seems to be a quite complex serialization/deserialization mechanism for this purpose. The official "documentation" describes several restrictions, which are required for the synchronization to work. However, these restrictions are likely only the most basic ones. More of them can easily be observed and they impose some very restrictive limitations (which are not required by anything else, not even GWT) on the classes being shared, namely:

1. They must implement `java.io.Serializable`.
2. They must not be generic (e.g. `public class BidiMap<K,V>` is not allowed). __Subject to a fix in future Vaadin releases__.
3. They must not be abstract.
4. Static fields seem to only be synchronized, if they're of primitive type. In other words, not an instance of your own class.
5. They must have a default (zero-arg) constructor of any visibility. This constructor can contain some code.
6. Even though no errors are generated, `private` and `final` fields are not synchronized. Default values are used instead.
7. They must not use or throw Exceptions... this is very probably a bug of the mechanism.

My own experience (tips and tricks) using this mechanism:  

1. Should you wish for some field not to be serialized, mark it as `transient`. Along with that, visibility can be reduced to `private` if desirable. Do note, however, that it will still be initialized (if initialized in your default constructor for instance) on the client and potentially used some time later so its type must be compatible with GWT.

For more information:  
<http://dev.vaadin.com/wiki/Vaadin7/SharedState>