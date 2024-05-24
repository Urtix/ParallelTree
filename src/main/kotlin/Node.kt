data class Node<T : Comparable<T>>(var key: T, var value: T) {
    var left: Node<T>? = null
    var right: Node<T>? = null
}