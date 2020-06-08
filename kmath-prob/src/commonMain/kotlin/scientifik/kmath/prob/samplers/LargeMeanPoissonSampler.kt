package scientifik.kmath.prob.samplers

import kotlinx.coroutines.flow.first
import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.ConstantChain
import scientifik.kmath.chains.SimpleChain
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.next
import kotlin.math.*

class LargeMeanPoissonSampler(val mean: Double) : Sampler<Int> {
    private val exponential: Sampler<Double> =
        AhrensDieterExponentialSampler.of(1.0)
    private val gaussian: Sampler<Double> =
        ZigguratNormalizedGaussianSampler()
    private val factorialLog: InternalUtils.FactorialLog = NO_CACHE_FACTORIAL_LOG!!
    private val lambda: Double = floor(mean)
    private val logLambda: Double = ln(lambda)
    private val logLambdaFactorial: Double = getFactorialLog(lambda.toInt())
    private val delta: Double = sqrt(lambda * ln(32 * lambda / PI + 1))
    private val halfDelta: Double = delta / 2
    private val twolpd: Double = 2 * lambda + delta
    private val c1: Double = 1 / (8 * lambda)
    private val a1: Double = sqrt(PI * twolpd) * exp(c1)
    private val a2: Double = twolpd / delta * exp(-delta * (1 + delta) / twolpd)
    private val aSum: Double = a1 + a2 + 1
    private val p1: Double = a1 / aSum
    private val p2: Double = a2 / aSum

    private val smallMeanPoissonSampler: Sampler<Int> = if (mean - lambda < Double.MIN_VALUE)
        NO_SMALL_MEAN_POISSON_SAMPLER
    else  // Not used.
        KempSmallMeanPoissonSampler.of(mean - lambda)

    init {
        require(mean >= 1) { "mean is not >= 1: $mean" }
        // The algorithm is not valid if Math.floor(mean) is not an integer.
        require(mean <= MAX_MEAN) { "mean $mean > $MAX_MEAN" }
    }

    override fun sample(generator: RandomGenerator): Chain<Int> = SimpleChain {
        // This will never be null. It may be a no-op delegate that returns zero.
        val y2 = smallMeanPoissonSampler.next(generator)
        var x: Double
        var y: Double
        var v: Double
        var a: Int
        var t: Double
        var qr: Double
        var qa: Double

        while (true) {
            // Step 1:
            val u = generator.nextDouble()

            if (u <= p1) {
                // Step 2:
                val n = gaussian.next(generator)
                x = n * sqrt(lambda + halfDelta) - 0.5
                if (x > delta || x < -lambda) continue
                y = if (x < 0) floor(x) else ceil(x)
                val e = exponential.next(generator)
                v = -e - 0.5 * n * n + c1
            } else {
                // Step 3:
                if (u > p1 + p2) {
                    y = lambda
                    break
                }

                x = delta + twolpd / delta * exponential.next(generator)
                y = ceil(x)
                v = -exponential.next(generator) - delta * (x + 1) / twolpd
            }

            // The Squeeze Principle
            // Step 4.1:
            a = if (x < 0) 1 else 0
            t = y * (y + 1) / (2 * lambda)

            // Step 4.2
            if (v < -t && a == 0) {
                y += lambda
                break
            }

            // Step 4.3:
            qr = t * ((2 * y + 1) / (6 * lambda) - 1)
            qa = qr - t * t / (3 * (lambda + a * (y + 1)))

            // Step 4.4:
            if (v < qa) {
                y += lambda
                break
            }

            // Step 4.5:
            if (v > qr) continue

            // Step 4.6:
            if (v < y * logLambda - getFactorialLog((y + lambda).toInt()) + logLambdaFactorial) {
                y += lambda
                break
            }
        }

        min(y2 + y.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    private fun getFactorialLog(n: Int): Double = factorialLog.value(n)

    override fun toString(): String = "Large Mean Poisson deviate"

    companion object {
        private const val MAX_MEAN = 0.5 * Int.MAX_VALUE
        private var NO_CACHE_FACTORIAL_LOG: InternalUtils.FactorialLog? = null

        private val NO_SMALL_MEAN_POISSON_SAMPLER = object : Sampler<Int> {
            override fun sample(generator: RandomGenerator): Chain<Int> = ConstantChain(0)
        }

        fun of(mean: Double): LargeMeanPoissonSampler =
            LargeMeanPoissonSampler(mean)

        init {
            // Create without a cache.
            NO_CACHE_FACTORIAL_LOG =
                InternalUtils.FactorialLog.create()
        }
    }
}
