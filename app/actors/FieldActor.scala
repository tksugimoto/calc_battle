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
  var users = Set[ActorRef]()
  var uids: Array[String] = Array()

  def receive = {
    case r:Result => {
      println("Log: FieldActor#receive Result")
      println(r)
      users map { _ ! r }
    }
    case Subscribe(uid: String) => {
      println("Log: FieldActor#receive Subscribe")
      users += sender
      uids = uids + uid
      println(uids)
      context watch sender
      users map { _ ! Subscribe(uid) }
    }
    case Terminated(user) => {
      println("Log: FieldActor#receive Terminated")
      println(user)
      users -= user
      users map { _ ! Terminated }
    }
  }
}
