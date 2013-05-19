package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import com.typesafe.config.ConfigFactory
import play.api.Logger
import play.api.mvc.Action
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.OAuth
import play.api.libs.oauth.ServiceInfo
import play.api.mvc.RequestHeader
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.RequestToken
import play.api.libs.ws.WS
import play.api.libs.json.JsValue
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import models.Users
import org.joda.time.DateTime
import models.User
import models.User

object Twitter extends Controller {
  // twitter oauth
  val oauthConfig = ConfigFactory.load("oauth.conf")
  val consumerKey = oauthConfig.getString("consumer.key")
  val consumerSecret = oauthConfig.getString("consumer.secret")
  
  val callbackUrl = oauthConfig.getString("callback")
  
  val KEY = ConsumerKey(consumerKey,consumerSecret)
  
  val TWITTER = OAuth(
      ServiceInfo(
          "https://api.twitter.com/oauth/request_token"
    	, "https://api.twitter.com/oauth/access_token"
        , "https://api.twitter.com/oauth/authorize"
        , KEY
      )
      , false
  )
  
  /**
   * OAuth認証
   */
  def authenticate = Action { request =>
    request.queryString.get("oauth_verifier").flatMap(_.headOption).map{
      verifier =>
        val tokenPair = sessionTokenPair(request).get
        
        TWITTER.retrieveAccessToken(tokenPair, verifier) match {
          case Right(t) => 
            
            val oauthCalculator = OAuthCalculator(
                ConsumerKey(consumerKey,consumerSecret)
                , RequestToken(t.token, t.secret)
            )
            
            val result = WS.url("http://api.twitter.com/1/account/verify_credentials.json")
            	.sign(oauthCalculator)
            	.get
            	.map( f => f.json)
            	
            val json = Await.result(result,Duration.Inf)
            val twitterId = (json \ "id").asOpt[Long]
            val name = (json \ "name").asOpt[String]
            val screenName = (json \ "screen_name").asOpt[String]
            
            val user = Users.findTwitterUserBy(twitterId.get)
            user match {
              case Some(user) => //更新処理
                Logger.info("user update")
                Users.updateUserBy(
                    User(twitterId=user.twitterId
                        ,name=name.get
                        ,token=t.token
                        ,secret=t.secret
                        ,screenName=screenName.get
                        ,createdAt=DateTime.now().toDate()
                        )
                )
              case None => //新規登録
                Logger.info("user insert")
                Users.create(
                    User(twitterId=twitterId.get
                        ,name=name.get
                        ,token=t.token
                        ,secret=t.secret
                        ,screenName=screenName.get
                        ,createdAt=DateTime.now().toDate()
                        )
                )
            }

            
            
            Redirect(routes.Application.index).withSession(
                "token" -> t.token, "secret" -> t.secret
            )
          case Left(e) => throw e
        }
    }.getOrElse(
        TWITTER.retrieveRequestToken(callbackUrl) match {
          case Right(t) => {
            Redirect(TWITTER.redirectUrl(t.token)).withSession("token" -> t.token, "secret" -> t.secret)
          }
          case Left(e) => throw e
        }
    )
    
    
    /*Logger.info("consumer key = " + consumerKey)
    Logger.info("consumer Secret = " + consumerSecret)
    Logger.info("callbackUrl = " + callbackUrl)*/
    
  }
  
  /**
   * セッショントークンペア
   */
  def sessionTokenPair(implicit request: RequestHeader): Option[RequestToken] = {
    for {
      token <- request.session.get("token")
      secret <- request.session.get("secret")
    } yield {
      RequestToken(token, secret)
    }
  }
}
