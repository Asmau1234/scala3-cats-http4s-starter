package sw

import cats.effect.*
import org.http4s.implicits.*
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.blaze.client.BlazeClientBuilder

import sw.service.*

object Main extends ResourceApp.Forever:

  override def run(args: List[String]): Resource[IO, Unit] =
    for
      config <- Config.load()
      client <- BlazeClientBuilder[IO].resource

      service = FreeCurrencyApi(client, config.freeCurrencyApi.apiKey)
      notificationService = AzureNotificationHubsService(config.azureNotificationHub.connectionString, config.azureNotificationHub.hubName)
      routes = Server.routes(service, notificationService).orNotFound

      _ <- BlazeServerBuilder[IO]
          .bindHttp(config.rest.port, config.rest.host)
          .withHttpApp(routes)
          .resource
    yield ()
