package se.aorwall.bam

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.TypedActor
import akka.testkit.CallingThreadDispatcher
import akka.testkit.TestKit
import akka.testkit.TestProbe
import collect.Collector
import configuration.ConfigurationService
import configuration.ConfigurationServiceImpl
import grizzled.slf4j.Logging
import model.statement.window.LengthWindowConf
import model.statement.Latency
import se.aorwall.bam.analyse.Analyser
import se.aorwall.bam.model.events.LogEvent
import se.aorwall.bam.model.Alert
import se.aorwall.bam.model.State
import se.aorwall.bam.process.request.Request
import se.aorwall.bam.process.request.RequestProcessor
import se.aorwall.bam.process.simple.SimpleProcess
import se.aorwall.bam.process.ProcessorHandler

/**
 * Testing the whole log data flow.
 *
 * FIXME: This test doesn't shut down properly
 */
@RunWith(classOf[JUnitRunner])
class LogdataIntegrationSuite(_system: ActorSystem) extends TestKit(_system) with FunSuite with MustMatchers with BeforeAndAfterAll with Logging {

  def this() = this(ActorSystem("LogdataIntegrationSuite"))

  override protected def afterAll(): scala.Unit = {
    _system.shutdown()
  }

  test("Recieve a logdata objects and send an alert") {
    
    var result: Alert = null
    val processId = "processId"
    val camelEndpoint = "hej"

    // Start up the modules
    val system = ActorSystem("LogServerTest")
    val collector = system.actorOf(Props[Collector].withDispatcher(CallingThreadDispatcher.Id), name = "collect")
    val processor = system.actorOf(Props[ProcessorHandler].withDispatcher(CallingThreadDispatcher.Id), name = "process")
    val analyser = system.actorOf(Props[Analyser].withDispatcher(CallingThreadDispatcher.Id), "analyse")
      
    // start the processors
    processor ! new Request("requestProcessor", 120000L)
    processor ! new SimpleProcess(processId, List("startComponent", "endComponent"), 120000l)  
    
    analyser ! new Latency(processId, "statementId", camelEndpoint, 2000, Some(new LengthWindowConf(2)))

    // Collect logs
    val currentTime = System.currentTimeMillis

    Thread.sleep(400)

    collector ! new LogEvent("startComponent", "329380921309", currentTime, "329380921309", "client", "server", State.START, "hello")
    collector ! new LogEvent("startComponent", "329380921309", currentTime+1000, "329380921309", "client", "server" , State.SUCCESS, "") // success
    collector ! new LogEvent("endComponent", "329380921309", currentTime+2000, "329380921309", "client", "server", State.START, "")
    collector ! new LogEvent("endComponent", "329380921309",  currentTime+3000, "329380921309", "client", "server", State.SUCCESS, "") // success

    Thread.sleep(400)
    
  	//expectMsg(1 seconds, new Alert(processId, "Average latency 3000ms is higher than the maximum allowed latency 2000ms", true)) // the latency alert

    TypedActor(system).stop(processor)
    //TypedActor(system).stop(analyser)
  }
}