package react

import akka.actor.{Props, ActorLogging, Actor, ActorSystem}
import scala.concurrent.duration._
import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * User: david.wursteisen
 * Date: 25/04/14
 * Time: 17:09
 */
object Main {
  def main(args: Array[String]) {
    val system = ActorSystem("ReactSystem")
    system.actorOf(Props(classOf[Pricer], EUR_3M), name = "pricer-"+EUR_3M)
    system.actorOf(Props(classOf[Pricer], EUR_6M), name = "pricer-"+EUR_6M)

  }


  class Pricer(indice: Indices) extends Actor with ActorLogging {
    val client = context.actorOf(Props[Client])
    val randomTimer = new Random().nextInt(10)
    context.system.scheduler.schedule(0 seconds, randomTimer seconds, new Runnable {
      def run() = {
        client ! Message(new Random().nextInt(100))
    }})


    def receive = {
      case ThrowException(message) => {
        log.info("Exception message received : will detroy everything !" + message)
        throw new RuntimeException(message)
      }
      case StrMessage(msg) => {
        log.info("Pricer "+indice+ "receive message : "+msg)
      }

    }
  }

  class Client extends Actor with ActorLogging {
    def receive = {
      case Message(price) => {

        log.info("Receive " + price)
        if(price > 7) {
          log.info("Killed !")
          context.stop(self)
        }
      }
    }
  }

  case class StrMessage(msg: String)
  case class Message(price: Int)

  case class ThrowException(message: String)


}
