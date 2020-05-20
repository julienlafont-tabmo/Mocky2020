package repositories

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.jsonb.implicits.jsonbGet
import models.errors.MockNotFoundError
import models.mocks.{ Mock, MockResponse }
import utils.PostgresUtil

/**
  * Load legacy mocks from PG, that have been migrated from MongoDB
  */
class MockV2Repository(transactor: Transactor[IO]) {

  private object SQL {
    private val TABLE = Fragment.const("mocks_v2")

    def GET(id: String): Fragment =
      fr"SELECT content, status, content_type, charset, headers FROM $TABLE WHERE id = $id"

    def UPDATE_STATS(id: String): Fragment =
      fr"UPDATE $TABLE SET last_access_at = ${PostgresUtil.now}, total_access = total_access + 1 WHERE id = $id"
  }

  /**
    * Fetch a legacy mock by its primary key, and update its stats
    */
  def touchAndGetMockResponse(id: String): IO[Either[MockNotFoundError.type, MockResponse]] = {
    val queries = for {
      mock <- SQL.GET(id).query[Mock].option
      _ <- SQL.UPDATE_STATS(id).update.run
    } yield mock

    queries.transact(transactor).map {
      case Some(mock) => Right(MockResponse(mock))
      case None => Left(MockNotFoundError)
    }
  }
}
