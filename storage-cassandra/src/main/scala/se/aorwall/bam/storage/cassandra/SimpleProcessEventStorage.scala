package se.aorwall.bam.storage.cassandra

import se.aorwall.bam.model.events.LogEvent
import me.prettyprint.hector.api.Keyspace
import se.aorwall.bam.model.events.SimpleProcessEvent
import akka.actor.ActorContext
import akka.actor.ActorSystem
import se.aorwall.bam.storage.EventStorage
import me.prettyprint.hector.api.beans.ColumnSlice
import se.aorwall.bam.model.events.RequestEvent
import se.aorwall.bam.model.Start
import se.aorwall.bam.model.events.Event

class SimpleProcessEventStorage(system: ActorSystem, cfPrefix: String) extends CassandraStorage (system, cfPrefix) with EventStorage {
	type EventType = SimpleProcessEvent
	
   def this(system: ActorSystem) = this(system, "SimpleProcessEvent")
   
	def eventToColumns(event: Event): List[(String, String)] = 	  
		List[(String,String)]() // TODO: Implement 
	
   def columnsToEvent(columns: ColumnSlice[String, String]): Event = 
     new SimpleProcessEvent("","",0L,List[RequestEvent](),Start,0L) // TODO: Implement 
     
}
