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
  var users = Set[User]()

  def receive = {
    case r:Result => {
      println("Log: FieldActor#receive Result")
      users map { _.userActor ! r }
    }
    case Subscribe(uid: String) => {
      println("Log: FieldActor#receive Subscribe")
      println(users)
      users += new User(uid, 0, sender)
      println(users)
      context watch sender
      users map { _.userActor ! UpdateUsers(users) }
    }
    case Terminated(user) => {
      println("Log: FieldActor#receive Terminated")
      println(users)
      users.map(u => if(u.userActor == user) users -= u)
      println(users)
      users map { _.userActor ! UpdateUsers(users) }
    }
  }
}
