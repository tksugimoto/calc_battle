package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{Json, JsValue}

object UserActor {
  def props(uid: String)(out: ActorRef) = Props(new UserActor(uid, FieldActor(), out))
}

case class UpdateUsers(user: Iterable[User])

class UserActor(uid: String, field: ActorRef, out: ActorRef) extends Actor {
  var results = Map[String, Int]()

  override def preStart() = {
    println("Log: UserActor#preStart")
    FieldActor() ! Subscribe(uid)
  }

  def receive = {
    case Result(uid, isCorrect) if sender == field => {
      println("Log: UserActor#receive Result")
      val js = Json.obj("type" -> "result", "uid" -> uid, "isCorrect" -> isCorrect)
      out ! js
    }
    case js: JsValue => {
      println("Log: UserActor#receive JsValue")
      (js \ "result").validate[Boolean] map { field ! Result(uid, _) }
    }
    case UpdateUsers(users: Iterable[User]) if sender == field => {
      println("Log: UserActor#receive UpdateUsers")
      users.map(u => results += (u.uid -> u.continuationCorrect))
      println(results)
      val js = Json.obj(
        "type" -> "updateUsers",
        "uids" -> users.map(_.uid),
        "users" -> results
      )
      out ! js
    }
    case other => {
      println("Log: UserActor#receive other")
    }
  }
}
