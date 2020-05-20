package repositories

import java.util.UUID

import cats.effect.IO
import doobie.{ Fragment, Transactor }
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.jsonb.implicits._
import doobie.postgres.implicits._
import doobie.util.log.LogHandler
import models.errors.MockNotFoundError
import models.mocks._
import models.mocks.actions.{ CreateUpdateMock, DeleteMock }
import models.mocks.feedbacks.MockCreated
import utils.PostgresUtil

class MockV3Repository(transactor: Transactor[IO]) {

  implicit val log: LogHandler = LogHandler.jdkLogHandler

  private object SQL {
    private val TABLE = Fragment.const("mocks_v3")
    private val BCRYPT_ITER = Fragment.const("14") // Higher iterations make the crypt method slower

    private def encode(content: String) = fr"encode(digest($content, 'sha256'), 'hex')"
    private def checkSecret(secret: String) = fr"secret_token = crypt($secret, secret_token)"

    def GET(id: UUID): Fragment =
      fr"SELECT content, status, content_type, charset, headers FROM $TABLE WHERE id = $id"

    def UPDATE_STATS(id: UUID): Fragment =
      fr"UPDATE $TABLE SET last_access_at = ${PostgresUtil.now}, total_access = total_access + 1 WHERE id = $id"

    // TODO: Fix 'ip'
    def INSERT(mock: CreateUpdateMock): Fragment =
      fr"""
          INSERT INTO $TABLE
            (content, content_type, status, charset, headers, created_at, total_access, hash_content, secret_token, hash_ip)
          VALUES (
            ${mock.contentArrayBytes},
            ${mock.contentType},
            ${mock.status},
            ${mock.charset},
            ${mock.headersJson},
            ${PostgresUtil.now},
            0,
            ${encode(mock.content)},
            crypt(${mock.secret}, gen_salt('bf', $BCRYPT_ITER)),
            'ip'
          )
          """

    def UPDATE(id: UUID, mock: CreateUpdateMock): Fragment =
      fr"""
          UPDATE $TABLE
          SET
            content = ${mock.contentArrayBytes},
            content_type = ${mock.contentType},
            status = ${mock.status},
            charset = ${mock.charset},
            headers = ${mock.headersJson},
            hash_content = ${encode(mock.content)}
          WHERE id = $id AND ${checkSecret(mock.secret)}
      """

    def DELETE(id: UUID, secret: String): Fragment =
      fr"DELETE FROM $TABLE WHERE id = $id and ${checkSecret(secret)}"
  }

  /**
    * Fetch a V3 mock by its primary key, update its stats and return the Mock response
    */
  def touchAndGetMockResponse(id: UUID): IO[Either[MockNotFoundError.type, MockResponse]] = {
    val queries = for {
      mock <- SQL.GET(id).query[Mock].option
      _ <- SQL.UPDATE_STATS(id).update.run
    } yield mock

    queries.transact(transactor).map {
      case Some(mock) => Right(MockResponse(mock))
      case None => Left(MockNotFoundError)
    }
  }

  /**
    * Fetch the raw V3 mock
    */
  def get(id: UUID): IO[Either[MockNotFoundError.type, Mock]] = {
    SQL.GET(id).query[Mock].option.transact(transactor).map {
      case Some(mock) => Right(mock)
      case None => Left(MockNotFoundError)
    }
  }

  /**
    * Insert a new mock
    * @return the uuid of the created mock
    */
  def insert(mock: CreateUpdateMock): IO[MockCreated] = {
    SQL.INSERT(mock).update.withUniqueGeneratedKeys[UUID]("id").transact(transactor)
      .map(MockCreated.apply)
  }

  /**
    * Update an existing mock if the secret is correct
    * @return true if one mock have been updated
    */
  def update(id: UUID, mock: CreateUpdateMock): IO[Boolean] = {
    SQL.UPDATE(id, mock).update.run.transact(transactor)
      .map(affectedRows => affectedRows > 0)
  }

  /**
    * Delete an existing mock if the secret is correct
    * @return true if one mock have been deleted
    */
  def delete(id: UUID, payload: DeleteMock): IO[Boolean] = {
    SQL.DELETE(id, payload.secret).update.run.transact(transactor)
      .map(affectedRows => affectedRows > 0)
  }

}
