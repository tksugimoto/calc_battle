package actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import play.libs.Akka

object FieldActor {
  lazy val field = Akka.system().actorOf(Props[FieldActor])
  def apply() = field
}

case class Result(uid: String, isCorrect: Boolean)
case class Subscribe(uid: String)
case class User(uid: String, continuationCorrect: Int, userActor: ActorRef)

class FieldActor extends Actor {
  var users = Map[String, User]()

  def receive = {
    case r:Result => {
      println("Log: FieldActor#receive Result")
      users.values map { _.userActor ! r }
    }
    case Subscribe(uid: String) => {
      println("Log: FieldActor#receive Subscribe")
      users += (uid -> new User(uid, 0, sender))
      context watch sender
      users.values map { _.userActor ! UpdateUsers(users.values) }
    }
    case Terminated(user) => {
      println("Log: FieldActor#receive Terminated")
      users.foreach { case(k, v) => if(v.userActor == user) users -= k }
      users.values map { _.userActor ! UpdateUsers(users.values) }
    }
  }
}
