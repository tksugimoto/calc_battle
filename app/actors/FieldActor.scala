package actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import play.libs.Akka

object FieldActor {
  lazy val field = Akka.system().actorOf(Props[FieldActor])
  def apply() = field
}

case class Result(uid: String, isCorrect: Boolean)
case class Subscribe(uid: String)

class FieldActor extends Actor {
  var users = Map[ActorRef, String]()

  def receive = {
    case r:Result => {
      println("Log: FieldActor#receive Result")
      users.keys map { _ ! r }
    }
    case Subscribe(uid: String) => {
      println("Log: FieldActor#receive Subscribe")
      println(users)
      if(!users.values.exists(_ == uid)) users += (sender -> uid)
      println(users)
      context watch sender
      users.keys map { _ ! UpdateUsers(users.values) }
    }
    case Terminated(user) => {
      println("Log: FieldActor#receive Terminated")
      println(users)
      users -= user
      println(users)
      users.keys map { _ ! UpdateUsers(users.values) }
    }
  }
}
