package actors

import akka.actor.{Actor, ActorRef, Props}

object FieldActor {
  def props = Props[FieldActor]
  case class Result(uid: String, isCollect: Boolean)
}
object Subscribe

class FieldActor extends Actor {
  import FieldActor._

  var users = Set[ActorRef]()

  def receive = {
    case r:Result => {
      println("Log: FieldActor#receive Result")
      println(r)
      users map { _ ! r }
    }
    case Subscribe => {
      println("Log: FieldActor#receive Subscribe")
      users += sender
      context watch sender
    }
    case Terminated(user) => {
      println("Log: FieldActor#receive Terminated")
      users -= user
    }
  }
}
