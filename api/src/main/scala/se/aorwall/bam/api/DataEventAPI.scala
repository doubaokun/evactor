package se.aorwall.bam.api

import akka.actor.ActorSystem
import com.codahale.jerkson.Json._
import se.aorwall.bam.model.events.DataEvent
import se.aorwall.bam.storage.EventStorageExtension
import se.aorwall.bam.storage.Storage
import unfiltered.request.Path
import unfiltered.request.Seg
import unfiltered.response.ResponseString
import org.codehaus.jackson.map.ObjectMapper
import java.net.URLEncoder
import java.net.URLDecoder
import unfiltered.request.Params
import unfiltered.response.BadRequest
import se.aorwall.bam.model.events.Event

class DataEventAPI(val system: ActorSystem) extends EventAPI {
    
  val storage = EventStorageExtension(system).getEventStorage(classOf[DataEvent].getName) match {
    case Some(s) => s
    case None => throw new RuntimeException("No storage impl")
  }
     
  override implicit protected[api] def toMap(e: Event): Map[String, Any] = e match { 
    case event: DataEvent =>  Map ("id" -> event.id, 
         "timestamp" -> event.timestamp,
         "message" -> event.message)
  }
  
}

