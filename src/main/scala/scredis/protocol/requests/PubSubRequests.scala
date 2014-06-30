package scredis.protocol.requests

import scredis.protocol._
import scredis.serialization.Writer

import scala.collection.generic.CanBuildFrom

object PubSubRequests {
  
  import scredis.serialization.Implicits.stringReader
  
  private object PSubscribe extends Command("PSUBSCRIBE")
  private object Publish extends Command("PUBLISH")
  private object PubSubChannels extends Command("PUBSUB CHANNELS")
  private object PubSubNumSub extends Command("PUBSUB NUMSUB")
  private object PubSubNumPat extends ZeroArgCommand("PUBSUB NUMPAT")
  private object PUnsubscribe extends Command("PUNSUBSCRIBE")
  private object Subscribe extends Command("SUBSCRIBE")
  private object Unsubscribe extends Command("UNSUBSCRIBE")
  
  case class PSubscribe(patterns: String*) extends Request[Nothing](
    PSubscribe, patterns: _*
  ) {
    override def decode = ???
  }
  
  case class Publish[W: Writer](channel: String, message: W) extends Request[Long](
    Publish, channel, implicitly[Writer[W]].write(message)
  ) {
    override def decode = {
      case IntegerResponse(value) => value
    }
  }
  
  case class PubSubChannels[CC[X] <: Traversable[X]](patternOpt: Option[String])(
    implicit cbf: CanBuildFrom[Nothing, String, CC[String]]
  ) extends Request[CC[String]](PubSubChannels, patternOpt.toSeq: _*) {
    override def decode = {
      case a: ArrayResponse => a.parsed[String, CC] {
        case b: BulkStringResponse => b.flattened[String]
      }
    }
  }
  
  case class PubSubNumSub[CC[X, Y] <: collection.Map[X, Y]](channels: String*)(
    implicit cbf: CanBuildFrom[Nothing, (String, Long), CC[String, Long]]
  ) extends Request[CC[String, Long]](PubSubNumSub, channels: _*) {
    override def decode = {
      case a: ArrayResponse => a.parsedAsPairsMap[String, Long, CC] {
        case b: BulkStringResponse => b.flattened[String]
      } {
        case IntegerResponse(value) => value
      }
    }
  }
  
  case class PubSubNumPat() extends Request[Long](PubSubNumPat) {
    override def decode = {
      case IntegerResponse(value) => value
    }
  }
  
  case class PUnsubscribe(patterns: String*) extends Request[Nothing](
    PUnsubscribe, patterns: _*
  ) {
    override def decode = ???
  }
  
  case class Subscribe(channels: String*) extends Request[Nothing](
    Subscribe, channels: _*
  ) {
    override def decode = ???
  }
  
  case class Unsubscribe(channels: String*) extends Request[Nothing](
    Unsubscribe, channels: _*
  ) {
    override def decode = ???
  }
  
}