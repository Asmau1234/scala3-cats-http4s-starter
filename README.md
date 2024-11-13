## Scala 3, Cats Effect, Http4s, Azure Notification Service project


The only thing it does is defining an API endpoint that translates from https://freecurrencyapi.com/ format to an arbitrary another format, but including validation, etc, etc. If you wish to run it to see how it works, then you'll need to grab an API key from Free Currency API and then run the app as follows: `FREE_CURRENCY_API_KEY=<the-api-key> sbt run`.
 You set register a device on Azure Notification Hubs and Send Notifications from the server.

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

Packaging is done via `sbt Docker/publishLocal` or, to build a native image for GraalVM, `sbt GraalVMNativeImage/packageBin`.Before generating a native image, you should run the app in tracing mode: `sbt runTraced`.

There's also `sbt scalafmt`, `sbt scalafixAll` and `sbt coverage test coverageReport` available.
