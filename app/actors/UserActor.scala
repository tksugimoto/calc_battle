package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{Json, JsValue}

object UserActor {
  def props(out: ActorRef) = Props(new UserActor(FieldActor(), out))
}

class UserActor(field: ActorRef, out: ActorRef) extends Actor {
  override def preStart() = {
    println("Log: UserActor#preStart")
    FieldActor() ! Subscribe
  }

  def receive = {
    case Result(isCollect) if sender == field => {
      println("Log: UserActor#receive Result")
      val js = Json.obj("type" -> "message", "result" -> isCollect)
      out ! js
    }
    case js: JsValue => {
      println("Log: UserActor#receive JsValue")
      (js \ "result").validate[Boolean] map { field ! Result(_) }
    }
    case other => {
      println("Log: UserActor#receive other")
      //log.error("unhandled" + other)
    }
  }
}
