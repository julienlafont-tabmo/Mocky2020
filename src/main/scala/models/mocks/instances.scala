package models.mocks

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import models.mocks.feedbacks.MockCreated

object instances {
  implicit val mockCreatedEncoder: Encoder.AsObject[MockCreated] = deriveEncoder[MockCreated]
}
