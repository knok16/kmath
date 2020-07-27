package scientifik.kmath.geometry.experimental.transformations

import scientifik.kmath.geometry.experimental.Dimension
import scientifik.kmath.geometry.experimental.Vector
import scientifik.kmath.geometry.experimental.VectorSpace

class ShiftTransformation<T, D : Dimension, V : Vector<T, D>>(
        private val vectorSpace: VectorSpace<T, D, V>,
        private val bias: V
) : Transformation<T, D, V> {
    override fun invoke(v: V): V = vectorSpace.add(v, bias)
    override val input: VectorSpace<T, D, V> get() = vectorSpace
    override val output: VectorSpace<T, D, V> get() = vectorSpace
}

fun <T, D : Dimension, V : Vector<T, D>> VectorSpace<T, D, V>.shiftBy(bias: V): Transformation<T, D, V> =
        ShiftTransformation(this, bias)

fun <T1, D1 : Dimension, V1 : Vector<T1, D1>, T2, D2 : Dimension, V2 : Vector<T2, D2>> Relation<T1, D1, V1, T2, D2, V2>.shiftBy(
        bias: V2
): Relation<T1, D1, V1, T2, D2, V2> = this.composeWith(ShiftTransformation(this.output, bias))