package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views.html.defaultpages.unauthorized


object Application extends Controller {
  
  val tweetForm = Form(
    "content" -> nonEmptyText(maxLength = 140)
  )

  def index = Action { implicit request =>
    session.get("userId").map { userId =>
      Logger.info("ok = " + userId)
      Ok(views.html.index( tweetForm, Tweets.findAllBy(userId.toLong) ))
    }.getOrElse{
      Logger.info("ng")
      Redirect(routes.Application.signin)
    }
  }


  def tweet = Action { implicit request =>
    session.get("userId").map { userId =>
      tweetForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index(errors, Tweets.findAllBy(userId.toLong))) ,
        content => {
          Tweets.create(content, userId.toLong)
          Redirect(routes.Application.index)
        }
      )
    }.getOrElse{
      Redirect(routes.Application.signin)
    }

  }

  def signin = Action { implicit request =>
    session.get("userId").map{ userId =>
      Redirect(routes.Application.index)
    }.getOrElse{
      Ok(views.html.signin())
    }
  }
  
  def logout = Action {
    Redirect(routes.Application.signin).withNewSession
  }
}
