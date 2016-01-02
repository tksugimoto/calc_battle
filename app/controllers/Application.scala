package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current

import scala.concurrent.Future

import actors.UserActor

class Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def ws = WebSocket.acceptWithActor[String, String] { request => out =>
    println("Log: Applications#ws")
    UserActor.props(out)
  }
}
