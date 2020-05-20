package models.mocks

import java.nio.charset.Charset

import io.circe.{ Encoder, Json }
import io.circe.generic.semiauto._

final case class Mock(content: Array[Byte], status: Int, contentType: String, charset: String, headers: Option[Json])

object Mock {
  private val UTF8 = Charset.forName("UTF8")

  implicit private val contentEncoder = Encoder.encodeString.contramap[Array[Byte]](bytes => new String(bytes, UTF8))
  implicit val mockEncoder: Encoder.AsObject[Mock] = deriveEncoder[Mock]
}
