/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.cbcr.audit

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy
import play.api.Environment
import uk.gov.hmrc.play.audit.http.config.{AuditingConfig, LoadAuditingConfig}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.inject.RunMode


/**
  * This simply wraps the existing AuditConnector with Guice annotation to allow for injection
  */
@ImplementedBy(classOf[AuditConnectorIImpl])
trait AuditConnectorI extends AuditConnector

@Singleton
class AuditConnectorIImpl @Inject() (val environment: Environment) extends AuditConnectorI with RunMode {
  override def auditingConfig: AuditingConfig = LoadAuditingConfig("auditing")
}