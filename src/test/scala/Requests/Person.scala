package Requests

import io.gatling.core.structure.ChainBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.http.Predef._
import io.gatling.core.Predef._
import com.github.javafaker.Faker
import scala.util.Random


object Person {

  private val postPersonsUrl     = "/persons"
  private val postBulkPersonsUrl = "/persons/bulk"
  private val getPersonsByPage   = "/persons/"
  private val getPersonsByName   = "/persons/name/${name}"
  private val getPersonsCount    = "/persons/count"
  private val faker              = new Faker()

  val personBody = Iterator.continually(
    Map(
      "name"    -> faker.name().username(),
      "age"     -> faker.number().numberBetween(18,99),
      "created" -> (System.currentTimeMillis / 1000),
    )
  )

  val personBulk = Iterator.continually(
    Map(
      "name01"  -> faker.name().username(),
      "name02"  -> faker.name().username(),
      "name03"  -> faker.name().username(),
      "name04"  -> faker.name().username(),
      "name05"  -> faker.name().username(),
      "name06"  -> faker.name().username(),
      "name07"  -> faker.name().username(),
      "name08"  -> faker.name().username(),
      "name09"  -> faker.name().username(),
      "name10"  -> faker.name().username(),
      "age01"   -> faker.number().numberBetween(18,99),
      "age02"   -> faker.number().numberBetween(18,99),
      "age03"   -> faker.number().numberBetween(18,99),
      "age04"   -> faker.number().numberBetween(18,99),
      "age05"   -> faker.number().numberBetween(18,99),
      "age06"   -> faker.number().numberBetween(18,99),
      "age07"   -> faker.number().numberBetween(18,99),
      "age08"   -> faker.number().numberBetween(18,99),
      "age09"   -> faker.number().numberBetween(18,99),
      "age10"   -> faker.number().numberBetween(18,99),
      "created" -> (System.currentTimeMillis / 1000),
    )
  )




  private val createPersonRequest: HttpRequestBuilder =
    http("Create person request")
      .post(postPersonsUrl)
      .body(StringBody("""{ "name": "${name}", "age": ${age}, "created": ${created} }""")).asJson
      .header("Content-Type","application/json")
      .check(status.is(201))

  //{"name":"jasono","age": 12, "created": 1593421376}

   val createBulkPersonsRequest: HttpRequestBuilder =
    http("Create person requests")
      .post(postBulkPersonsUrl)
      .body(StringBody(
        """
          |[
          |{ "name": "${name01}", "age": ${age01}, "created": ${created} },
          |{ "name": "${name02}", "age": ${age02}, "created": ${created} },
          |{ "name": "${name03}", "age": ${age03}, "created": ${created} },
          |{ "name": "${name04}", "age": ${age04}, "created": ${created} },
          |{ "name": "${name05}", "age": ${age05}, "created": ${created} },
          |{ "name": "${name06}", "age": ${age06}, "created": ${created} },
          |{ "name": "${name07}", "age": ${age07}, "created": ${created} },
          |{ "name": "${name08}", "age": ${age08}, "created": ${created} },
          |{ "name": "${name09}", "age": ${age09}, "created": ${created} },
          |{ "name": "${name10}", "age": ${age10}, "created": ${created} }
          |]
          |""".stripMargin)
      ).asJson
      .header("Content-Type","application/json")
      .check(status.is(201))

  private def listPageSavePersonsRequest(pageNb:Int) : HttpRequestBuilder =
    listPersonsByPageRequest(pageNb)
      .check(jsonPath("$..name").findAll.transform(ids => Random.shuffle(ids).toList.distinct.take(10)).saveAs("persons"))

  private def listPersonsByPageRequest(pageNb:Int) : HttpRequestBuilder =
    http("Get person list by page")
      .get(getPersonsByPage + pageNb)
      .check(status.is(200))

   val listPersonsByNameRequests: HttpRequestBuilder =
    http("Get person list by name")
      .get(getPersonsByName)
     .check(status.is(200))


  val create: ChainBuilder =
    feed(personBody)
    .pause(1,2)
    .exec(createPersonRequest)

  val bulk: ChainBuilder =
    feed(personBulk)
    .pause(10,20)
    .exec(createBulkPersonsRequest)

  def listByPage(pageNb: Int) :ChainBuilder =
    pause(2,3)
    .exec(listPersonsByPageRequest(pageNb))

  val listAndSave: ChainBuilder=
    pause(2,3)
    .exec(listPageSavePersonsRequest(1))


  def loopNames():ChainBuilder =
    foreach("${persons}", "name") {
      pause(2,4)
      .exec(listPersonsByNameRequests)
    }
}



