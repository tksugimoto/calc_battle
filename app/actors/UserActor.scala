package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{Json, JsValue}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(new UserActor(uid, FieldActor(), out))
}

class UserActor(uid: String, field: ActorRef, out: ActorRef) extends Actor {
  override def preStart() = {
    println("Log: UserActor#preStart")
    FieldActor() ! Subscribe(uid)
  }

  def receive = {
    case js: JsValue => {
      println("Log: UserActor#receive JsValue")
      (js \ "result").validate[Boolean] map { field ! Result(uid, _) }
    }
    case Result(uid, isCorrect) if sender == field => {
      println("Log: UserActor#receive Result")
      val js = Json.obj("type" -> "result", "uid" -> uid, "isCorrect" -> isCorrect)
      out ! js
    }
    case Subscribe(uid: String) if sender == field => {
      println("Log: UserActor#receive UserActor")
      println(uid)
      val js = Json.obj("type" -> "newUser", "uid" -> uid)
      out ! js
    }
    case other => {
      println("Log: UserActor#receive other")
    }
  }
}
