package scientifik.kmath.geometry.experimental.transformations

import org.junit.jupiter.api.Test
import scientifik.kmath.geometry.experimental.*

internal class MapTransformationTest {
    @Test
    fun identity() {
        with(real2DVectorSpace) {
            val transformation = map { it }

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                val vector = vectorOf(x, y)
                assertVectorEquals(vector, transformation(vector))
            }
        }
    }

    @Test
    fun swap() {
        with(real2DVectorSpace) {
            val transformation = map { vectorOf(it[1], it[0]) }

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(vectorOf(y, x), transformation(vectorOf(x, y)))
            }
        }
    }

    @Test
    fun expand() {
        with(real2DVectorSpace) {
            val transformation = mapTo(real4DVectorSpace) { vectorOf(it[0] / 2.0, it[0] + 1.0, 0.0, it[1] - 0.01) }

            grid(-10.0..10.0, -10.0..10.0, 0.15).forEach { (x, y) ->
                assertVectorEquals(vectorOf(x / 2.0, x + 1.0, 0.0, y - 0.01), transformation(vectorOf(x, y)))
            }
        }
    }
}