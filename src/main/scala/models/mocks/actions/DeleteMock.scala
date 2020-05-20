package models.mocks.actions

import io.circe.Decoder
import io.tabmo.circe.extra.rules.StringRules
import models.mocks.Constants

final case class DeleteMock(secret: String)

object DeleteMock {
  import io.tabmo.json.rules._

  implicit val deleteMockDecoder: Decoder[DeleteMock] = Decoder.instance[DeleteMock] { c =>
    for {
      secret <- c.downField("secret").read(StringRules.maxLength(Constants.SECRET_MAX_LENGTH))
    } yield DeleteMock(secret)
  }
}
