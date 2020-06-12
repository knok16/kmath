package scientifik.kmath.ast

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parser
import scientifik.kmath.operations.FieldOperations
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.RingOperations
import scientifik.kmath.operations.SpaceOperations

private object ArithmeticsEvaluator : Grammar<MST>() {
    val num by token("-?[\\d.]+(?:[eE]-?\\d+)?")
    val lpar by token("\\(")
    val rpar by token("\\)")
    val mul by token("\\*")
    val pow by token("\\^")
    val div by token("/")
    val minus by token("-")
    val plus by token("\\+")
    val ws by token("\\s+", ignore = true)

    val number: Parser<MST> by num use { MST.Numeric(text.toDouble()) }

    val term: Parser<MST> by number or
            (skip(minus) and parser(this::term) map { MST.Unary(SpaceOperations.MINUS_OPERATION, it) }) or
            (skip(lpar) and parser(this::rootParser) and skip(rpar))

    val powChain by leftAssociative(term, pow) { a, _, b ->
        MST.Binary(PowerOperations.POW_OPERATION, a, b)
    }

    val divMulChain: Parser<MST> by leftAssociative(powChain, div or mul use { type }) { a, op, b ->
        if (op == div) {
            MST.Binary(FieldOperations.DIV_OPERATION, a, b)
        } else {
            MST.Binary(RingOperations.TIMES_OPERATION, a, b)
        }
    }

    val subSumChain: Parser<MST> by leftAssociative(divMulChain, plus or minus use { type }) { a, op, b ->
        if (op == plus) {
            MST.Binary(SpaceOperations.PLUS_OPERATION, a, b)
        } else {
            MST.Binary(SpaceOperations.MINUS_OPERATION, a, b)
        }
    }

    override val rootParser: Parser<MST> by subSumChain
}

fun String.tryParseMath(): ParseResult<MST> = ArithmeticsEvaluator.tryParseToEnd(this)
fun String.parseMath(): MST = ArithmeticsEvaluator.parseToEnd(this)