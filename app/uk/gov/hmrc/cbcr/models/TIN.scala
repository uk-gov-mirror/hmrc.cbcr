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

package uk.gov.hmrc.cbcr.models

import play.api.libs.json.{JsValue, Json, Reads, Writes}
import uk.gov.hmrc.domain.{SimpleObjectReads, SimpleObjectWrites}

case class TIN(value: String, issuedBy: String)
object TIN {
  implicit val utrFormat: Writes[TIN] = new SimpleObjectWrites[TIN](_.value)
  implicit val utrRead = new Reads[TIN] {
    override def reads(json: JsValue) = json.validate[String].map(TIN(_, ""))
  }
}
