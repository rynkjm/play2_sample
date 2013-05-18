package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._

object Application extends Controller {
  
  val tweetForm = Form(
    "content" -> nonEmptyText(maxLength = 140)
  )

  def index = TODO /*Action {
    Ok(views.html.index( tweetForm, Tweet.all() ))
  }*/


  def tweet = TODO /*Action { implicit request =>
    tweetForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(errors, Tweet.all())) ,
      content => {
        Tweet.create(content)
        Redirect(routes.Application.index)
      }
    )
  }*/

  def signin = Action {
    Ok(views.html.signin())
  }
}
