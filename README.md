# scala-parser-combinators

### Scala Standard Parser Combinator Library

This library is now community-maintained. If you are interested in helping please contact @gourlaysama or mention it [on Gitter](https://gitter.im/scala/scala-parser-combinators).

As of Scala 2.11, this library is a separate jar that can be omitted from Scala projects that do not use Parser Combinators.

## Documentation

 * [Current API](https://javadoc.io/page/org.scala-lang.modules/scala-parser-combinators_2.12/latest/scala/util/parsing/combinator/index.html)
 * The [Getting Started](docs/Getting_Started.md) guide
 * A more complicated example, [Building a lexer and parser with Scala's Parser Combinators](https://enear.github.io/2016/03/31/parser-combinators/)
 * "Combinator Parsing", chapter 33 of [_Programming in Scala, Third Edition_](http://www.artima.com/shop/programming_in_scala), shows how to use this library to parse arithmetic expressions and JSON. The second half of the chapter examines how the library is implemented.

## Adding an SBT dependency
To depend on scala-parser-combinators in SBT, add something like this to your build.sbt:

```
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
```

To support multiple Scala versions, see the example in [scala/scala-module-dependency-sample](https://github.com/scala/scala-module-dependency-sample).

## Example

```scala
import scala.util.parsing.combinator._

case class WordFreq(word: String, count: Int) {
  override def toString = s"Word <$word> occurs with frequency $count"
}

class SimpleParser extends RegexParsers {
  def word: Parser[String]   = """[a-z]+""".r       ^^ { _.toString }
  def number: Parser[Int]    = """(0|[1-9]\d*)""".r ^^ { _.toInt }
  def freq: Parser[WordFreq] = word ~ number        ^^ { case wd ~ fr => WordFreq(wd,fr) }
}

object TestSimpleParser extends SimpleParser {
  def main(args: Array[String]) = {
    parse(freq, "johnny 121") match {
      case Success(matched,_) => println(matched)
      case Failure(msg,_) => println(s"FAILURE: $msg")
      case Error(msg,_) => println(s"ERROR: $msg")
    }
  }
}
```

For a detailed unpacking of this example see
[Getting Started](docs/Getting_Started.md).

## ScalaJS support

Scala-parser-combinators directly supports Scala.js 0.6+:

```
libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2"
```

## Issues

Many old issues from the Scala JIRA issue tracker have been migrated
here, but not all of them. Community assistance identifying and
migrating still-relevant issues is welcome.  See [this
page](https://github.com/scala/scala-parser-combinators/issues/61) for
details.

## Contributing

 * See the [Scala Developer Guidelines](https://github.com/scala/scala/blob/2.13.x/CONTRIBUTING.md) for general contributing guidelines
 * Have a look at [existing issues](https://github.com/scala/scala-parser-combinators/issues)
 * Ask questions and discuss [on Gitter](https://gitter.im/scala/scala-parser-combinators)
