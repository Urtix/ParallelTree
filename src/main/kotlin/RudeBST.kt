class RudeBST<T : Comparable<T>> : BinarySearchTree<T>() {
    private val lock = Any()

    private var root: Node<T>? = null

    // Вставка узла
    override fun add(key: T, value: T) {
        synchronized(lock) {
            root = add(root, key, value)
        }
    }

    override fun add(key: T){
        add(key, key)
    }

    // Удаление узла
    override fun delete(key: T) {
        synchronized(lock) {
            root = delete(root, key)
        }
    }

    // Поиск узла
    override fun search(key: T): Any? {
        synchronized(lock) {
            return search(root, key)
        }
    }

    override fun printTree() {
        printTreeValue(root)
        println()
        printTreeKey(root)
    }

    private fun printTreeValue(node: Node<T>?) {
        if (node == null) {
            return
        }
        printTreeValue(node.left)
        print("${node.value} ")
        printTreeValue(node.right)
    }
    private fun printTreeKey(node: Node<T>?) {
        if (node == null) {
            return
        }
        printTreeKey(node.left)
        print("${node.key} ")
        printTreeKey(node.right)
    }
}