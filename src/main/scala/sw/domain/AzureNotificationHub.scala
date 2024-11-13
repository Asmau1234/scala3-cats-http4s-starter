package sw.domain

case class DeviceRegistration(deviceToken: String, tags: Set[String])
case class NotificationRequest(message: String, tags: Set[String])
