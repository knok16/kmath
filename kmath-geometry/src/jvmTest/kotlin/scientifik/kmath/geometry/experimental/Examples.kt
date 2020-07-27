package scientifik.kmath.geometry.experimental

import scientifik.kmath.geometry.experimental.transformations.mapTo
import scientifik.kmath.geometry.experimental.transformations.projection
import scientifik.kmath.geometry.experimental.transformations.shiftBy
import scientifik.kmath.operations.RealField
import kotlin.math.sqrt

fun main() {
    with(VectorSpaceImpl(D4, RealField, ::sqrt)) {
        val a: VectorImpl<Double, D4> = VectorImpl(listOf(1.0, 2.0, 3.0, 5.0))
        val b = vectorOf(2.0, -3.0, 4.0, 5.0)
        println(a - b)
        println(a + b)
        println(a * 4)
        println(a dot b)
        println(a.distanceTo(b))
        println(distance(a, b))

        println(zero)
    }

    with(real4DVectorSpace) {
        val a = vectorOf(1.0, 2.0, 3.0, 5.0)
        val b = vectorOf(2.0, -3.0, 4.0, 5.0)

        val transformation = projection(vectorOf(0.0, 0.0, 1.0, 0.0), zero)
                .mapTo(real3DVectorSpace) { vector -> vectorOf(vector[0], vector[1], vector[3]) }
                .shiftBy(vectorOf(1.0, -2.0, 0.01))

        val distance = real3DVectorSpace.distance(
                transformation(a + 3 * b),
                transformation((a dot b) * b - a / norm(b))
        )

        println("""Resulting distance: $distance""")
    }
}