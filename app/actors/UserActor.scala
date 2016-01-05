package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{Json, JsValue}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(new UserActor(uid, FieldActor(), out))
}

case class UpdateUsers(results: Map[String, Int])
case class UpdateUser(result: (String, Int))

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
    case UpdateUser(result) if sender == field => {
      println("Log: UserActor#receive UpdateUser")
      val js = Json.obj("type" -> "updateUser", "user" -> Map(result))
      out ! js
    }
    case UpdateUsers(results: Map[String, Int]) if sender == field => {
      println("Log: UserActor#receive UpdateUsers")
      val js = Json.obj("type" -> "updateUsers", "users" -> results)
      out ! js
    }
    case other => {
      println("Log: UserActor#receive other")
    }
  }
}
