package io.mocky.models.mocks

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.mocky.models.mocks.feedbacks.MockCreated

object instances {
  implicit val mockCreatedEncoder: Encoder.AsObject[MockCreated] = deriveEncoder[MockCreated]
}
