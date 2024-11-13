package sw.service

import cats.effect.IO
import com.windowsazure.messaging.{AppleRegistration, Notification, NotificationHub, NotificationHubClient, NotificationOutcome, Registration}
import sw.domain.{DeviceRegistration, NotificationRequest}

class AzureNotificationHubsService(connectionString: String, hubName: String) {
  private val notificationHubClient = new NotificationHub(connectionString, hubName)

  def registerDevice(deviceRegistration: DeviceRegistration): IO[Registration] = { // Changed return type
    val reg = new AppleRegistration(deviceRegistration.deviceToken)
    val tags = new java.util.HashSet[String]()
    tags.add("myTag")
    tags.add("myOtherTag")
    reg.setTags(tags)

    IO.blocking(notificationHubClient.createRegistration(reg))
  }

  def sendAppleNotification(notificationRequest: NotificationRequest): IO[Notification] = {
    val alert = "{\"aps\":{\"alert\":\"Hello from Java!\"}}"
    val n = Notification.createAppleNotification(alert)
    IO.pure(Notification.createAppleNotification(alert))
  }

  def sendAndroidNotification(notificationRequest: NotificationRequest): IO[Notification] = {
    val message = "{\"data\":{\"msg\":\"Hello from Java!\"}}"
    IO.pure(Notification.createFcmV1Notification(message))
  }
}