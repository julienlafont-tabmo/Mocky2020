package utils

import java.sql.Timestamp
import java.time.LocalDateTime

object PostgresUtil {
  def now: Timestamp = Timestamp.valueOf(LocalDateTime.now())
}
