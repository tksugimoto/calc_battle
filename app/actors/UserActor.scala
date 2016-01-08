package actors

import akka.actor.{Actor, ActorRef, Props}
import models.Question
import play.api.libs.json.{Writes, Json, JsValue}

object UserActor {
  def props(uid: UID)(out: ActorRef) = Props(new UserActor(uid, FieldActor.field, out))
  
  case class UpdateUsers(users: Set[User])
  case class UpdateUser(user: User, finish: Boolean)
  class UID(val id: String) extends AnyVal
  case class User(uid: UID, continuationCorrect: Int)
  
  implicit val userWrites = new Writes[User] {
    def writes(user: User): JsValue = {
      Json.obj(user.uid.id -> user.continuationCorrect)
    }
  }
  implicit val usersWrites = new Writes[Set[User]] {
    def writes(users: Set[User]): JsValue = {
      Json.toJson(users.map { user: User =>
        user.uid.id -> user.continuationCorrect
      }.toMap)
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
    case UpdateUsers(users) if sender == field => {
      val js = Json.obj("type" -> "updateUsers", "users" -> users)
      out ! js
    }
  }
}
