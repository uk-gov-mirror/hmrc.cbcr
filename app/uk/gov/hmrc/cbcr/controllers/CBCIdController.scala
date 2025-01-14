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

package uk.gov.hmrc.cbcr.controllers

import javax.inject._
import play.api.libs.json.JsValue
import play.api.mvc.{ControllerComponents, Request, Result}
import uk.gov.hmrc.cbcr.auth.CBCRAuth
import uk.gov.hmrc.cbcr.models._
import uk.gov.hmrc.cbcr.services.SubscriptionHandlerImpl
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CBCIdController @Inject()(gen: SubscriptionHandlerImpl, auth: CBCRAuth, cc: ControllerComponents)(
  implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def subscribe =
    auth.authCBCRWithJson(
      { implicit request: Request[JsValue] =>
        request.body
          .validate[SubscriptionDetails]
          .fold[Future[Result]](
            _ => Future.successful(BadRequest),
            srb => gen.createSubscription(srb)
          )
      },
      parse.json
    )

  def updateSubscription(safeId: String) =
    auth.authCBCRWithJson(
      { implicit request =>
        request.body
          .validate[CorrespondenceDetails]
          .fold[Future[Result]](
            e => Future.successful(BadRequest(e.toString)),
            details => gen.updateSubscription(safeId, details)
          )
      },
      parse.json
    )

  def getSubscription(safeId: String) = auth.authCBCR { implicit request =>
    gen.getSubscription(safeId)
  }

  @inline implicit private def subscriptionDetailsToSubscriptionRequestBody(
    s: SubscriptionDetails): SubscriptionRequest =
    SubscriptionRequest(
      s.businessPartnerRecord.safeId,
      false,
      CorrespondenceDetails(
        s.businessPartnerRecord.address,
        ContactDetails(s.subscriberContact.email, s.subscriberContact.phoneNumber),
        ContactName(s.subscriberContact.firstName, s.subscriberContact.lastName)
      )
    )

}
