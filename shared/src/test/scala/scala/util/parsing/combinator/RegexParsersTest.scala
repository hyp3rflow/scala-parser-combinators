/*
 * Scala (https://www.scala-lang.org)
 *
 * Copyright EPFL and Lightbend, Inc.
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package scala.util.parsing.combinator

import org.junit.Test
import org.junit.Assert.assertEquals

class RegexParsersTest {
  @Test
  def parserNoSuccessMessage: Unit = {
    object parser extends RegexParsers {
      def sign = "-"
      def number = "\\d+".r
      type ResultType = Option[String] ~ String
      def p: Parser[ResultType] = sign.? ~ number withErrorMessage  "Number expected!"
      def q: Parser[ResultType] = sign.? ~! number withErrorMessage  "Number expected!"
    }
    import parser._
    def extractResult(r: ParseResult[ResultType]): ResultType = r match {
      case Success(r, _) => r
      case r => sys.error(r.toString)
    }
    def result(num: Int): ResultType = {
      val minusSign = if (num < 0) Some("-") else None
      val absNumStr = Math.abs(num).toString
      new ~(minusSign, absNumStr)
    }

    val failure1 = parseAll(p, "-x").asInstanceOf[Failure]
    assertEquals("string matching regex '\\d+' expected but 'x' found", failure1.msg)
    val failure2 = parseAll(p, "x").asInstanceOf[Failure]
    assertEquals("string matching regex '\\d+' expected but 'x' found", failure2.msg)
    assertEquals(result(-5), extractResult(parseAll(p, "-5")))
    assertEquals(result(5), extractResult(parseAll(p, "5")))
    val error1 = parseAll(q, "-x").asInstanceOf[Error]
    assertEquals("Number expected!", error1.msg)
    val error2 = parseAll(q, "x").asInstanceOf[Error]
    assertEquals("Number expected!", error2.msg)
    assertEquals(result(-5), extractResult(parseAll(q, "-5")))
    assertEquals(result(5), extractResult(parseAll(q, "5")))
  }

  @Test
  def parserSkippingResult: Unit = {
    object parser extends RegexParsers {
      def quote = "\""
      def string = """[a-zA-Z]*""".r
      type ResultType = String
      def p: Parser[ResultType] = quote ~> string <~ quote
      def q: Parser[ResultType] = quote ~>! string <~! quote
      def halfQuoted = quote ~ string ^^ { case q ~ s => q + s }
    }
    import parser._
    val failureLq = parseAll(p, "\"asdf").asInstanceOf[Failure]
    val failureRq = parseAll(p, "asdf\"").asInstanceOf[Failure]
    val failureQBacktrackL = parseAll(q | quote, "\"").asInstanceOf[Error]
    val failureQBacktrackR = parseAll(q | halfQuoted, "\"asdf").asInstanceOf[Error]

    val successP = parseAll(p, "\"asdf\"").get
    assertEquals(successP, "asdf")
    val successPBacktrackL = parseAll(p | quote, "\"").get
    assertEquals(successPBacktrackL, "\"")
    val successPBacktrackR = parseAll(p | halfQuoted, "\"asdf").get
    assertEquals(successPBacktrackR, "\"asdf")

    val successQ = parseAll(q, "\"asdf\"").get
    assertEquals(successQ, "asdf")
  }

  @Test
  def parserFilter: Unit = {
    object parser extends RegexParsers {
      val keywords = Set("if", "false")
      def word: Parser[String] = "\\w+".r

      def keyword: Parser[String] = word filter (keywords.contains)
      def ident: Parser[String] = word filter(!keywords.contains(_))

      def test: Parser[String ~ String] = keyword ~ ident
    }
    import parser._

    val failure1 = parseAll(test, "if false").asInstanceOf[Failure]
    assertEquals("Input doesn't match filter: false", failure1.msg)
    val failure2 = parseAll(test, "not true").asInstanceOf[Failure]
    assertEquals("Input doesn't match filter: not", failure2.msg)
    val success = parseAll(test, "if true").asInstanceOf[Success[String ~ String]]
    assertEquals(new ~("if", "true"), success.get)
  }

  @Test
  def parserForFilter: Unit = {
    object parser extends RegexParsers {
      def word: Parser[String] = "\\w+".r

      def twoWords = for {
        (a ~ b) <- word ~ word
      } yield (b, a)
    }
    import parser._

    val success = parseAll(twoWords, "first second").asInstanceOf[Success[(String, String)]]
    assertEquals(("second", "first"), success.get)
  }
}
