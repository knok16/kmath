package scientifik.kmath.geometry

import scientifik.kmath.dimensions.*
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.asSequence

internal data class VectorImpl<T, D : Dimension>(private val array: List<T>) : Vector<T, D> {
    override val size: Int get() = array.size

    override fun get(index: Int): T = when {
        index < 0 || index >= size -> error("Accessing outside of point bounds")
        else -> array[index]
    }

    override fun iterator(): Iterator<T> = array.iterator()
}

fun <T> vectorOf(a1: T): Vector<T, D1> = VectorImpl(listOf(a1))
fun <T> vectorOf(a1: T, a2: T): Vector<T, D2> = VectorImpl(listOf(a1, a2))
fun <T> vectorOf(a1: T, a2: T, a3: T): Vector<T, D3> = VectorImpl(listOf(a1, a2, a3))
fun <T> vectorOf(a1: T, a2: T, a3: T, a4: T): Vector<T, D4> = VectorImpl(listOf(a1, a2, a3, a4))
fun <T> vectorOf(a1: T, a2: T, a3: T, a4: T, a5: T): Vector<T, D5> = VectorImpl(listOf(a1, a2, a3, a4, a5))

private fun <T> Field<T>.square(a: T): T = a * a

class EuclideanSpace<T : Any, D : Dimension>(
        override val dim: D,
        override val field: ExtendedField<T>
) : InnerProductSpace<T, D, Vector<T, D>> {
    private val dimSize = dim.dim.toInt()

    override fun distance(a: Vector<T, D>, b: Vector<T, D>): T =
            field.sqrt(a.asSequence().zip(b.asSequence()).map { (l, r) -> field { square(l - r) } }.reduce(field::add))

    override fun norm(a: Vector<T, D>): T =
            field.sqrt(dotProduct(a, a))

    override fun dotProduct(a: Vector<T, D>, b: Vector<T, D>): T =
            a.asSequence().zip(b.asSequence()).map { (l, r) -> field.multiply(l, r) }.reduce(field::add)

    override fun add(a: Vector<T, D>, b: Vector<T, D>): Vector<T, D> =
            vectorFrom(a.asSequence().zip(b.asSequence()).map { (l, r) -> field.add(l, r) })

    override fun multiply(a: Vector<T, D>, k: Number): Vector<T, D> =
            vectorFrom(a.asSequence().map { field.multiply(it, k) })

    override fun multiply(a: Vector<T, D>, k: T): Vector<T, D> =
            vectorFrom(a.asSequence().map { field.multiply(it, k) })

    override val zero: Vector<T, D> =
            vectorFrom(generateSequence { field.zero }.take(dimSize))

    override fun vectorFrom(x: Sequence<T>): Vector<T, D> {
        val list = x.toList()
        require(list.size == dimSize)
        return VectorImpl(list)
    }
}