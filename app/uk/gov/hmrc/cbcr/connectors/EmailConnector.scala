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

package uk.gov.hmrc.cbcr.connectors

import com.google.inject.ImplementedBy
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.cbcr.models.Email
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpPost, HttpResponse}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[EmailConnectorImpl])
trait EmailConnector {
  val serviceUrl: String
  val port: String
  val host: String
  val protocol: String

  def http: HttpPost
}

@Singleton
class EmailConnectorImpl @Inject()(config: Configuration, httpClient: HttpClient)(implicit ec: ExecutionContext)
    extends EmailConnector {
  val http = httpClient
  val conf: Config = config.underlying.getConfig("microservice.services.email")
  val host = conf.getString("host")
  val port = conf.getInt("port").toString
  val protocol = conf.getString("protocol")
  val serviceUrl = s"$protocol://$host:$port/hmrc"

  def sendEmail(email: Email)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.POST[Email, HttpResponse](s"$serviceUrl/email/", email)
}
