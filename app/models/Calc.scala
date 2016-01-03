package models

import play.api._
import play.api.mvc._

import scala.util.Random

case class Calc(a: Int, b: Int)

object Calc {
  def question: Calc = {
    val a = (Random.nextInt(9) + 1) * 10 + Random.nextInt(10)
    val b = (Random.nextInt(9) + 1) * 10 + Random.nextInt(10)
    new Calc(a, b)
  }
}
