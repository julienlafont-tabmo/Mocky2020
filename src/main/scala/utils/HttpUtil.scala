package utils

import org.http4s.Request
import org.http4s.util.CaseInsensitiveString

object HttpUtil {

  private val IP_1 = CaseInsensitiveString("X-Real-Ip")
  private val IP_2 = CaseInsensitiveString("X-Forwarded-For")
  private val IP_3 = CaseInsensitiveString("HTTP_CLIENT_IP")
  private val IP_4 = CaseInsensitiveString("REMOTE_ADDR")

  def getIP[F[_]](req: Request[F]): String = {
    val headers = req.headers
    headers.get(IP_1).map(_.value)
      .orElse(headers.get(IP_2).map(_.value))
      .orElse(headers.get(IP_3).map(_.value))
      .orElse(headers.get(IP_4).map(_.value))
      .getOrElse("0.0.0.0")
  }
}
