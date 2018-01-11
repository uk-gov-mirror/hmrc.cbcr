/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.cbcr.services

import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}
import uk.gov.hmrc.cbcr.repositories.{DocRefIdRepository, ReportingEntityDataRepo}
import configs.syntax._
import uk.gov.hmrc.cbcr.models.DocRefId

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import cats.syntax.cartesian._
import cats.instances.future._

@Singleton
class DocRefIdClearService @Inject()(docRefIdRepo:DocRefIdRepository,
                                     reportingEntityDataRepo: ReportingEntityDataRepo,
                                     configuration:Configuration,
                                     runMode: RunMode)(implicit ec:ExecutionContext){

  val docRefIds: List[DocRefId] = configuration.underlying.get[String](s"${runMode.env}.DocRefId.clear").valueOr(_ => "").split("_").toList.map(DocRefId.apply)

  if (docRefIds.nonEmpty) {
    Logger.info(s"About to clear DocRefIds:\n${docRefIds.mkString("\n")}")
    Future.sequence(docRefIds.map(d => (docRefIdRepo.delete(d) |@| reportingEntityDataRepo.delete(d)).tupled)).onComplete{
      case Success(results) => Logger.info(s"Successfully deleted all ${results.size} docRefIds")
      case Failure(t)       => Logger.error(s"Failed to delete all DocRefIds: ${t.getMessage}",t)
    }
  }
}
