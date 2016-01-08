package actors

import akka.actor.{Actor, ActorRef, Props, Terminated}
import play.libs.Akka

object FieldActor {
  lazy val field = Akka.system().actorOf(Props[FieldActor])
  
  case class Result(isCorrect: Boolean)
  case class Subscribe(uid: String)
  case class User(uid: String, continuationCorrect: Int)
}

class FieldActor extends Actor {
  import FieldActor._
  var users = Map[ActorRef, User]()

  def receive = {
    case Result(isCorrect) => {
      users.get(sender) match {
        case Some(user) => {
          val updateUser = user.copy(continuationCorrect = if (isCorrect) user.continuationCorrect + 1 else 0)
          val result = updateUser.uid -> updateUser.continuationCorrect
          val finish = updateUser.continuationCorrect >= 5
          users = users.updated(sender, updateUser)

          users.keys.foreach { userActor =>
            userActor ! UserActor.UpdateUser(result, finish)
          }
        }
        case None => {
          // 送信元のActorRefが存在しない場合
        }
      }
    }
    case Subscribe(uid: String) => {
      users += (sender -> User(uid, 0))
      context watch sender
      val results = users.values.map { user =>
        user.uid -> user.continuationCorrect
      }.toMap[String, Int]

      users.keys.foreach { userActor =>
        userActor ! UserActor.UpdateUsers(results)
      }
    }
    case Terminated(user) => {
      users -= user
      val results = users.values.map { user =>
        user.uid -> user.continuationCorrect
      }.toMap[String, Int]

      users.keys.foreach { userActor =>
        userActor ! UserActor.UpdateUsers(results)
      }
    }
  }
}
