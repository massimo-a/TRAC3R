trait Token
case class IntToken(value: String, next: Token) extends Token
case class DecimalToken(value: String, next: Token) extends Token
case class KeywordToken(value: String, next: Token) extends Token
case class StringToken(value: String, next: Token) extends Token
case class OpToken(value: String, next: Token) extends Token
case class ErrToken(value: String, message: String, next: Token) extends Token
case class EOF() extends Token

object Token {
	private case class StringIterator(position: Int, string: String) {
		def current(): String = {
			string(position).toString();
		}
		def isEnd(): Boolean = {
			position >= string.length
		}
		def isKeyword(): Boolean = {
			"abcdefghijklmnopqrstuvwxyz_,.?!;:".contains(current.toLowerCase)
		}
		def isNumber(): Boolean = {
			"0123456789.".contains(current)
		}
		def isString(): Boolean = {
			"\"\'".contains(current)
		}
		def isWhiteSpace(): Boolean = {
			" \t\n\r".contains(current)
		}
		def isOp(): Boolean = {
			"-+*/=%^".contains(current)
		}
		private def readWhile(cond: StringIterator => Boolean, accu: String): String = {
			if(!isEnd && cond(this)) {
				return StringIterator(position+1, string).readWhile(cond, accu+current())
			} else return accu
		}
		def readWhile(cond: StringIterator => Boolean): String = {
			readWhile(cond, "");
		}
	}
	private def reverse(curr: Token, accu: Token): Token = {
		return curr match {
			case EOF() => accu
			case IntToken(v: String, next: Token) => reverse(next, IntToken(v, accu))
			case DecimalToken(v: String, next: Token) => reverse(next, DecimalToken(v, accu))
			case KeywordToken(v: String, next: Token) => reverse(next, KeywordToken(v, accu))
			case StringToken(v: String, next: Token) => reverse(next, StringToken(v, accu))
			case OpToken(v: String, next: Token) => reverse(next, OpToken(v, accu))
			case ErrToken(v: String, msg: String, next: Token) => reverse(next, ErrToken(v, msg, accu))
		}
	}
	def reverse(curr: Token): Token = {
		reverse(curr, EOF())
	}
	private def tokenize(stream: StringIterator, accu: Token): Token = {
		val pos = stream.position;
		val str = stream.string
		if(stream.isEnd) {
			return reverse(accu)
		} else if(stream.isWhiteSpace) {
			return tokenize(StringIterator(pos+1, str), accu)
		} else if(stream.isKeyword) {
			val keyword = stream.readWhile(_.isKeyword)
			return tokenize(StringIterator(pos+keyword.length, str), KeywordToken(keyword, accu))
		} else if(stream.isNumber) {
			val num = stream.readWhile(_.isNumber)
			return (num.count(_ == '.') match {
				case 0 => tokenize(StringIterator(pos+num.length, str), IntToken(num, accu))
				case 1 => tokenize(StringIterator(pos+num.length, str), DecimalToken(num, accu))
				case _ => tokenize(StringIterator(pos+num.length, str), ErrToken(num, "Multiple decimal points in number", accu))
			})
		} else if(stream.isString) {
			val word = StringIterator(pos+1, str).readWhile(s => !s.isString)
			return tokenize(StringIterator(pos+word.length+2, str), StringToken(word, accu))
		} else if(stream.isOp) {
			val op = stream.readWhile(_.isOp)
			return tokenize(StringIterator(pos+op.length, str), OpToken(op, accu))
		} else {
			return tokenize(StringIterator(pos+1, str), ErrToken(stream.current(), "Unrecognized symbol", accu))
		}
	}
	def tokenize(str: String): Token = {
		tokenize(StringIterator(0, str), EOF())
	}
	def toString(tok: Token): String = {
		return tok match {
			case EOF() => ""
			case IntToken(v: String, next: Token) => "('" + v + "', Integer) \n" + toString(next)
			case DecimalToken(v: String, next: Token) => "('" + v + "', Decimal) \n" + toString(next)
			case KeywordToken(v: String, next: Token) => "('" + v + "', Keyword) \n" + toString(next)
			case StringToken(v: String, next: Token) => "('" + v + "', String) \n" + toString(next)
			case OpToken(v: String, next: Token) => "('" + v + "', Operation) \n" + toString(next)
			case ErrToken(v: String, msg: String, next: Token) => "('" + v + "', Error : " + msg + ") \n" + toString(next)
		}
	}
}

object Main {
	def main(args: Array[String]): Unit = {
		println(Token.toString(Token.tokenize("this symbol { is not recognized, but + is. 12 is an int and 12.5 is a decimal 12..5 is a typo")))
	}
}