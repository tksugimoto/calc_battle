package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.JsValue

import scala.{Left, Right}
import scala.concurrent.Future

import actors.UserActor
import models.Calc

class Application extends Controller {
  val UID = "uid"
  var counter = 0;

  def index = Action { implicit request =>
    println("Log: Applications#index")
    val uid = request.session.get(UID).getOrElse {
      counter += 1
      counter.toString
    }
    Ok(views.html.index(uid, Calc.question)).withSession {
      request.session + (UID -> uid)
    }
  }

  def ws = WebSocket.tryAcceptWithActor[JsValue, JsValue] { implicit request =>
    println("Log: Applications#ws")
    Future.successful(request.session.get(UID) match {
      case None => Left(Forbidden)
      case Some(uid) => Right(UserActor.props(uid))
    })
  }
}
