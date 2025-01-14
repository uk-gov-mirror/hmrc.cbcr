/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cbcr.util

import java.nio.charset.Charset

import akka.stream.Materializer
import akka.util.ByteString
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.cbcr.models.{DatesOverlap, ReportingEntityData, ReportingEntityDataModel}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

trait UnitSpec extends WordSpecLike with Matchers with OptionValues {

  import scala.concurrent.duration._
  import scala.concurrent.{Await, Future}

  implicit val defaultTimeout: FiniteDuration = 5 seconds

  implicit def extractAwait[A](future: Future[A]): A = await[A](future)

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  def status(of: Result): Int = of.header.status

  def status(of: Future[Result])(implicit timeout: Duration): Int = status(Await.result(of, timeout))

  def jsonBodyOf(result: Result)(implicit mat: Materializer): JsValue =
    Json.parse(bodyOf(result))

  def jsonBodyOf(resultF: Future[Result])(implicit mat: Materializer): Future[JsValue] =
    resultF.map(jsonBodyOf)

  def bodyOf(result: Result)(implicit mat: Materializer): String = {
    val bodyBytes: ByteString = await(result.body.consumeData)
    bodyBytes.decodeString(Charset.defaultCharset().name)
  }

  def bodyOf(resultF: Future[Result])(implicit mat: Materializer): Future[String] =
    resultF.map(bodyOf)

  def verifyResult(result: Future[Result], red: ReportingEntityData)(implicit mat: Materializer) =
    Await.result(jsonBodyOf(result), 2.seconds) shouldEqual Json.toJson(red)

  def verifyResult(result: Future[Result], red: ReportingEntityDataModel)(implicit mat: Materializer) =
    Await.result(jsonBodyOf(result), 2.seconds) shouldEqual Json.toJson(red)

  def verifyResult(result: Future[Result], datesOverlap: DatesOverlap)(implicit mat: Materializer) =
    Await.result(jsonBodyOf(result), 2.seconds) shouldEqual Json.toJson(datesOverlap)

  def verifyStatusCode(result: Future[Result], statusCode: Int) = status(result) shouldBe statusCode

  def verifyStatusCode[A](result: Future[Result], expected: A) = status(result) shouldBe expected

}
