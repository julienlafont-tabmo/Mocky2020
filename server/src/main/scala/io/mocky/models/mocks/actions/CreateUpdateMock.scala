package io.mocky.models.mocks.actions

import io.circe.syntax._
import io.circe.{ Decoder, Json }
import io.tabmo.circe.extra.rules.{ IntRules, StringRules }

import io.mocky.config.MockSettings
import io.mocky.models.mocks.actions.CreateUpdateMock.CreateUpdateMockHeaders

final case class CreateUpdateMock(
  content: String,
  contentType: String,
  status: Int,
  charset: String,
  headers: Option[CreateUpdateMockHeaders],
  secret: String,
  ip: Option[String] = None) {
  def contentArrayBytes: Array[Byte] = content.getBytes("UTF-8")
  def headersJson: Option[Json] = headers.map(_.underlyingMap).flatMap { map => Option.when(map.nonEmpty)(map.asJson) }
  def withIp(ip: String): CreateUpdateMock = copy(ip = Some(ip))
}

object CreateUpdateMock {
  final case class CreateUpdateMockHeaders(underlyingMap: Map[String, String])

  implicit private val createMockHeadersDecoder: Decoder[CreateUpdateMockHeaders] = {
    Decoder.instance[CreateUpdateMockHeaders] { c =>
      c.as[Map[String, String]].map(CreateUpdateMockHeaders.apply)
    }
  }

  def decoder(settings: MockSettings): Decoder[CreateUpdateMock] =
    Decoder.instance[CreateUpdateMock] {
      c =>
        import io.tabmo.json.rules._
        for {
          content <- c.downField("content").read(StringRules.maxLength(settings.contentMaxLength))
          contentType <- c.downField("content_type").read(StringRules.maxLength(200))
          status <- c.downField("status").read(IntRules.min(100) |+| IntRules.max(999))
          charset <- c.downField("charset").as[String]
          headers <- c.downField("headers").as[Option[CreateUpdateMockHeaders]]
          secret <- c.downField("secret").read(StringRules.maxLength(settings.secretMaxLength))
        } yield CreateUpdateMock(content, contentType, status, charset, headers, secret)
    }

}
