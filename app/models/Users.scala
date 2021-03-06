package models

import anorm._
import anorm.SqlParser._
import java.util.Date
import org.joda.time._
import play.api.db._
import play.api.Play._

// Userモデルクラスの構造
case class User(
	id: Pk[Long]=NotAssigned
	,twitterId: Long
	,name: String
	,token: String
	,secret: String
	,screenName: String
	,createdAt: Date
	)

// Usersモデルクラスの振る舞い
object Users {
  val simple = {
    get[Pk[Long]]("id") ~
    get[Long]("twitter_id") ~
    get[String]("name") ~
    get[String]("token") ~
    get[String]("secret") ~
    get[String]("screen_name") ~
    get[Date]("created_at") map {
      case id ~ twitterId ~ name ~ token ~ secret ~ screenName ~ createdAt =>
        User(id, twitterId, name, token, secret, screenName, createdAt)
    }
  }

  /**
   * idよりUserオブジェクトを取得
   */
  def findUserBy(id:Long): Option[User] = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |SELECT
          | * 
          |FROM
          | users 
          |WHERE
          | id = {id} ;
          """.stripMargin
      ).on('id -> id).as(simple.singleOpt)
    }
  }
  
  /**
   * twittre id で取得
   */
  def findTwitterUserBy(twitterId:Long): Option[User] = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |SELECT
          | * 
          |FROM
          | users 
          |WHERE
          | users.twitter_id = {id} ;
          """.stripMargin
      ).on('id -> twitterId).as(simple.singleOpt)
    }
  }
  /**
   * ユーザ登録
   */
  def create(user: User) = {
    DB.withTransaction{ implicit c =>
      SQL(
        """
        |INSERT INTO users (
        | twitter_id, name, token, secret, screen_name, created_at
        |) VALUES (
        | {twitterId}, {name}, {token}, {secret}, {screenName}, {createdAt}
        |) ;
        """.stripMargin
      ).on(
          'twitterId -> user.twitterId
          ,'name -> user.name
          ,'token -> user.token
          ,'secret -> user.secret
          ,'screenName -> user.screenName
          ,'createdAt -> DateTime.now().toDate()
      ).executeUpdate
    }
  }
  
  /**
   * ユーザ更新
   */
  def updateUserBy(user: User) = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |UPDATE
          | users
          |SET
          | users.twitter_id = {twitterId}
          | ,users.name = {name}
          | ,users.token = {token}
          | ,users.secret = {secret}
          | ,users.screen_name = {screenName}
          |WHERE
          | users.id = {id} ;
          """.stripMargin
      ).on(
          'twitterId -> user.twitterId
          ,'name -> user.name
          ,'token -> user.token
          ,'secret -> user.secret
          ,'screenName -> user.screenName
          ,'id -> user.id
      ).executeUpdate
    }
  }

  /**
   * ユーザ削除
   */
  def deleteUserBy(id: Long) = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |DELETE FROM
          | users
          |WHERE
          | users.id = {id};
          """.stripMargin
      ).on(
          'id -> id
      ).executeUpdate
    }
  }
  
  def authentication(token:String, secret: String) = {
    DB.withTransaction{ implicit c =>
      SQL("""
          |SELECT
          | *
          |FROM
          | users
          |WHERE
          | token = {token}
          |AND
          | secret = {secret};
          """.stripMargin
      ).on(
          'token -> token, 'secret -> secret
      ).as(simple.singleOpt)
    }
  }
}
