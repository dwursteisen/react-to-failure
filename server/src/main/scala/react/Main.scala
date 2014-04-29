package react

import akka.actor._
import scala.concurrent.duration._
import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global
import org.webbitserver.{WebSocketConnection, BaseWebSocketHandler, WebServers}

/**
 * User: david.wursteisen
 * Date: 25/04/14
 * Time: 17:09
 */
object Main {
  def main(args: Array[String]) {
    val system = ActorSystem("ReactSystem")
    val supervisor: ActorRef = system.actorOf(Props[Supervisor])
    supervisor ! StartIndice(EUR_6M)

  }

  case class StartIndice(indice: Indices)

  case class StopIndice(indice: Indices)

  class Supervisor extends Actor with ActorLogging {

    var indices = Map[Indices, ActorRef]()

    def receive = {
      case StartIndice(indice) => {
        context.actorOf(Props(classOf[Pricer], indice), name = "pricer-" + indice)
        log.info("Start of context of pricing: " + indice)
      }


      case StopIndice(indice) => {
        log.info("Stop of context of pricing: " + indice)
        indices.get(indice).map {
          ref => context.stop(ref)
        }
      }

    }
  }

  class Pricer(indice: Indices) extends Actor with ActorLogging {
    val client = context.actorOf(Props[Client])
    context.system.scheduler.schedule(0 seconds, 1 seconds, new Runnable {
      def run() = {
        val price: Int = new Random().nextInt(100)
        log.info("Produce price : " + price)
        client ! Message(price)
      }
    })


    def receive = {
      case _ => {}
    }
  }

  class Client extends Actor with ActorLogging {

    val ws = WebServers.createWebServer(4444).add("/context/?", new BaseWebSocketHandler() {


      override def onMessage(connection: WebSocketConnection, msg: String) = {

      }

      override def onClose(connection: WebSocketConnection) = {
        self ! ByeBye(connection)
      }

      override def onOpen(connection: WebSocketConnection) = {
        System.out.println("connection open")

        self ! Hello(connection)
      }
    }).start().get()

    var clients = Seq[WebSocketConnection]()

    def receive = {
      case Hello(ctx) => clients = clients :+ ctx
      case ByeBye(ctx) => clients = clients.diff(Seq(ctx))
      case Message(price) => {
        System.out.println("Send price => " + price)
        clients.foreach((ctx) => ctx.send("{\'price\': %d}".format(price)))
      }
      case _ => ()
    }
  }


  case class Message(price: Int)


  case class Hello(connection: WebSocketConnection)

  case class ByeBye(connection: WebSocketConnection)

}
