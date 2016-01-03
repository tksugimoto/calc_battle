package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{Json, JsValue}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(new UserActor(uid, FieldActor(), out))
}

class UserActor(uid: String, field: ActorRef, out: ActorRef) extends Actor {
  override def preStart() = {
    println("Log: UserActor#preStart")
    FieldActor() ! Subscribe
  }

  def receive = {
    case Result(uid, isCollect) if sender == field => {
      println("Log: UserActor#receive Result")
      val js = Json.obj("type" -> "message", "uid" -> uid, "isCollect" -> isCollect)
      out ! js
    }
    case js: JsValue => {
      println("Log: UserActor#receive JsValue")
      println(js \ "result")
      println((js \ "result").validate[String])
      println((js \ "result").validate[String] map {_.toBoolean})
      (js \ "result").validate[String] map {_.toBoolean} map { field ! Result(uid, _) }
    }
    case other => {
      println("Log: UserActor#receive other")
    }
  }
}
