package xyz.qweru.geo.extend.kotlin.array

inline fun <reified T> Collection<T>.withModification(transform: (T) -> T): Array<T?> {
    val arr = arrayOfNulls<T>(this.size)
    this.forEachIndexed { index, t -> arr[index] = transform.invoke(t) }
    return arr
}

