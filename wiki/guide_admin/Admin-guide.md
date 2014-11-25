<!-- --- title: Administrator's handbook -->

[[_TOC_]]

## TODO

## Logging

The application doesn't use unified logging technology, implementation or interface at this moment. Individual application components use their own. They are all defined in the `org.pikater.shared.logging` package. Each application component has its own subpackage defined.

An attempt has been made to centralize logging into the servlet container but has not been fully implemented/used after all. For more information, refer to [[default package description|Default-package-description]].
