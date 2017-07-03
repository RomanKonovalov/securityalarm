import java.nio.charset.StandardCharsets
import java.util.Base64

import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * Performance test for the Status entity.
 */
class StatusGatlingTest extends Simulation {

    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    // Log all HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("TRACE"))
    // Log failed HTTP requests
    //context.getLogger("io.gatling.http").setLevel(Level.valueOf("DEBUG"))

    val baseURL = Option(System.getProperty("baseURL")) getOrElse """http://ec2-35-164-7-108.us-west-2.compute.amazonaws.com:8080"""

    val httpConf = http
        .baseURL(baseURL)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val headers_http = Map(
        "Accept" -> """application/json"""
    )

    val authorization_header = "Basic " + Base64.getEncoder.encodeToString("securityalarmapp:my-secret-token-to-change-in-production".getBytes(StandardCharsets.UTF_8))

    val headers_http_authentication = Map(
        "Content-Type" -> """application/x-www-form-urlencoded""",
        "Accept" -> """application/json""",
        "Authorization"-> authorization_header
    )

    /*val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "Authorization" -> "Bearer ${access_token}"
    )*/

    val headers_http_authenticated = Map(
        "Accept" -> """application/json""",
        "Authorization" -> "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0NzV4cWNzZzhuIiwiYXV0aCI6IlJPTEVfREVWSUNFIn0.vGkFBSLkzqG-9snQU5f2_DgJwtj65fJNREahpQuIjFk"
    )

    val scn = scenario("Test the Status entity")
        .exec(http("Create new status")
            .post("/securityalarm/api/statuses")
            .headers(headers_http_authenticated)
            .body(StringBody("""{"id":null, "deviceState":"OK", "latitude":53.87796, "longitude": 30.361100}""")).asJSON
            .check(status.is(201)))


    val users = scenario("Users").exec(scn)

    setUp(
        users.inject(rampUsers(50000) over (5 minutes))
    ).protocols(httpConf)
}
