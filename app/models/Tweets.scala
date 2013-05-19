package models

import anorm._
import anorm.SqlParser._
import java.util.Date
import org.joda.time._
import play.api.db._
import play.api.Play._

// Tweetモデルクラスの構造
case class Tweet(
    id: Pk[Long] = NotAssigned
    ,usersId:Long
    ,content: String
    ,createdAt: Date)

// Tweetモデルクラスの振る舞い
object Tweets{
  val simple = {
    get[Pk[Long]]("id") ~
    get[Long]("users_id") ~
    get[String]("content") ~
    get[Date]("created_at") map {
      case id ~ usersId ~ content ~ createdAt =>
        Tweet(id, usersId, content, createdAt)
    }
  }

  /**
   * ユーザのツイートを取得
   */
  def findAllBy(userId:Long): List[Tweet] = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |SELECT
          | *
          |FROM
          | tweets
          |WHERE
          | tweets.users_id = {usersId}
          |ORDER BY
          | tweets.id DESC;
          """.stripMargin
      ).on(
          'usersId -> userId
      ).as(simple *)
    }
  }

  /**
   * ツイート登録
   */
  def create(content:String , userId: Long) = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |INSERT INTO tweets (
          | users_id, content, created_at
          |) VALUES (
          | {usersId}, {content}, {createdAt}
          |);
          """.stripMargin
      ).on(
          'usersId -> userId.toString, 'content -> content, 'createdAt -> DateTime.now().toDate()
      ).executeUpdate
    }
  }

  /**
   * ツイートを削除
   */
  def deleteBy(tweetId: Long) = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |DELETE FROM
          | tweets
          |WHERE
          | tweets.id = {id} ;
          """.stripMargin
      ).on(
          'id -> tweetId
      ).executeUpdate
    }
  }
}
