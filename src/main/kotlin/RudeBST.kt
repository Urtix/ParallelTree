import kotlinx.coroutines.sync.Mutex

class RudeBST<T : Comparable<T>> : BinarySearchTree<T>() {
    //private val lock = Any()

    private var root: Node<T>? = null

    private val treeMutex = Mutex()

    // Вставка узла
    override suspend fun add(key: T, value: T) {
        treeMutex.lock()
        root = add(root, key, value)
        treeMutex.unlock()
    }

    override suspend fun add(key: T){
        add(key, key)
    }

    // Удаление узла
    override suspend fun delete(key: T) {
        treeMutex.lock()
        root = delete(root, key)
        treeMutex.unlock()
    }

    // Поиск узла
    override suspend fun search(key: T): Any? {
        treeMutex.lock()
        val res = search(root, key)
        treeMutex.unlock()
        return res
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