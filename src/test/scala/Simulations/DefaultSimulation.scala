package Simulations

import Requests.Person._
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.http

class DefaultSimulation extends Simulation{


  val scn: ScenarioBuilder =
    scenario("Usual user scenario")
    .repeat(10) {
        exec(create)
    }
    .repeat(10) {
      exec(bulk)
    }
    .exec(listAndSave)
    .exec(listByPage(2))
    .exec(listByPage(3))
    .exec(listByPage(4))
    .exec(listByPage(5))
    .exec(loopNames())


  //concurrent users

  val protocol = http
    .baseUrl("http://157.245.25.216")
    .disableCaching
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")

  setUp(
    scn.inject(constantConcurrentUsers(50) during 120)
  ).protocols(protocol).maxDuration(10*60)


}
