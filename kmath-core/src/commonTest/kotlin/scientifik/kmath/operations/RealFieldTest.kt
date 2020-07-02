package scientifik.kmath.operations

import kotlin.test.Test
import kotlin.test.assertEquals

internal class RealFieldTest {
    @Test
    fun testSqrt() {
        val sqrt = RealField { sqrt(25 * one) }
        assertEquals(5.0, sqrt)
    }
}