package sw

import cats.data.ValidatedNec
import cats.effect.IO
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.client.UnexpectedStatus
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import sw.domain.{ConversionRate, DeviceRegistration, NotificationRequest}
import sw.domain.ConversionRate.given
import sw.service.{AzureNotificationHubsService, FreeCurrencyApi}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder

case class BadValue(message: String) extends Throwable(message)

object Server:
  val message = "Hello World"
  implicit val ConversionRateDecoder: Decoder[NotificationRequest] = deriveDecoder[NotificationRequest]
  implicit val ConversionRateEncoder: Encoder[NotificationRequest] = deriveEncoder[NotificationRequest]
  implicit val DeviceRegistrationDecoder: Decoder[DeviceRegistration] = deriveDecoder[DeviceRegistration]
  implicit val DeviceRegistrationEncoder: Encoder[DeviceRegistration] = deriveEncoder[DeviceRegistration]
  given EntityDecoder[IO, ValidatedNec[String, ConversionRate]] = accumulatingJsonOf[IO, ValidatedNec[String, ConversionRate]]

  def routes(service: FreeCurrencyApi, azureNotificationHubsService: AzureNotificationHubsService) = HttpRoutes.of[IO] {

    case GET -> Root / "ping" =>
      Ok(message)

    /*case GET -> Root / "convert" / source / destination =>
      val response = for {
        latest <- service.latest(source, List(destination))
        value <- IO.fromOption(latest.get(destination))(BadValue(s"Did not receive the expected value for: $destination"))
        result <- ConversionRate
          .create(source, destination, value)
          .fold(e => BadRequest(e.foldLeft("")(_ ++ _)), c => Ok(c.asJson))
      } yield result

      response.handleErrorWith {
        // NOTE Fetching an unsupported currency.
        case UnexpectedStatus(Status.UnprocessableEntity, _, _) => UnprocessableEntity()
        // NOTE So that we get better error messages.
        case other => InternalServerError(other.getMessage())
      }*/

    case req @ POST -> Root / "register-device" =>
      for {
        deviceRegistration <- req.as[DeviceRegistration]
        outcome <- azureNotificationHubsService.registerDevice(deviceRegistration)
        response <- Ok(s"Device registered: $outcome")
      } yield response

    case req @ POST -> Root / "send-notification" =>
      for {
        notificationRequest <- req.as[NotificationRequest]
        outcome <- azureNotificationHubsService.sendAppleNotification(notificationRequest)
        response <- Ok(s"Notification sent: $outcome")
      } yield response
      
    case unknown =>
      NotFound()
  }
