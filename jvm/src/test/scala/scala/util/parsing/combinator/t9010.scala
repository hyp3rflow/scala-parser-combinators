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

import scala.util.parsing.combinator._
import scala.util.DynamicVariable

import org.junit.Test

class t9010 {
  @Test
  def test: Unit = {
    val p = new grammar
    import p._

    val res1 = parse(x, "x")
    assert(res1.successful)

    val res2 = parse(x, "y")
    assert(!res2.successful)

    val res3 = parseAll(x, "x")
    assert(res3.successful)

    val res4 = parseAll(x, "y")
    assert(!res4.successful)
  }

  private final class grammar extends RegexParsers {
    val x: Parser[String] = "x"
  }
}
