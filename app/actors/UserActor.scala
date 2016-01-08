package actors

import akka.actor.{Actor, ActorRef, Props}
import models.Question
import play.api.libs.json.{Writes, Json, JsValue}

object UserActor {
  def props(uid: UID)(out: ActorRef) = Props(new UserActor(uid, FieldActor.field, out))
  
  case class UpdateUsers(results: Map[UID, Int])
  case class UpdateUser(user: User, finish: Boolean)
  class UID(val id: String) extends AnyVal
  case class User(uid: UID, continuationCorrect: Int)
  
  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = {
      Json.obj(user.uid.id -> user.continuationCorrect)
    }
  }
  implicit val updateUsersWrites = new Writes[Map[UID, Int]] {
    def writes(users: Map[UID, Int]): JsValue = {
      Json.toJson(users.map { arg: (UID, Int) =>
        // UpdateUsersの中身をMapではなくSet[User]等にすればarg._1のようにしなくて良くなります
        arg._1.id -> arg._2
      })
    }
  }
}

import UserActor._
class UserActor(uid: UID, field: ActorRef, out: ActorRef) extends Actor {
  override def preStart() = {
    FieldActor.field ! FieldActor.Subscribe(uid)
  }

  def receive = {
    case js: JsValue => {
      (js \ "result").validate[Boolean] foreach {
        field ! FieldActor.Result(_)
      }
      val question = Json.obj("type" -> "question", "question" -> Question.create())
      out ! question
    }
    case UpdateUser(user, finish) if sender == field => {
      val js = Json.obj("type" -> "updateUser", "user" -> user, "finish" -> finish)
      out ! js
    }
    case UpdateUsers(results: Map[UID, Int]) if sender == field => {
      val js = Json.obj("type" -> "updateUsers", "users" -> results)
      out ! js
    }
  }
}
