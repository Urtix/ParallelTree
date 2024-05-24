import kotlinx.coroutines.sync.Mutex

open class ThinBST<T : Comparable<T>> {
    private var root: NodeMutex<T>? = null

    // Вставка узла
    open suspend fun add(key: T, value: T) {
        Mutex().lock()
        root = if (root != null) {
            root?.lock()
            add(root, key, value, null)
        } else {
            NodeMutex(key, value, null)
        }
    }

    open suspend fun add(key: T){
        add(key, key)
    }

    private suspend fun add(node: NodeMutex<T>?, key: T, value: T, parent: NodeMutex<T>?): NodeMutex<T> {
        return if (node == null) {
            NodeMutex(key, value, parent)
        } else {
            when {
                value < node.value -> {
                    if (node.left != null) {
                        node.left?.lock()
                        node.unlock()
                        node.left = add(node.left, key, value, node)
                    } else {
                        node.unlock()
                        node.left?.lock()
                        node.left = NodeMutex(key, value, parent)
                    }
                    node
                }
                value > node.value -> {
                    if (node.right != null) {
                        node.right?.lock()
                        node.unlock()
                        node.right = add(node.right, key, value, node)
                    } else {
                        node.unlock()
                        node.right?.lock()
                        node.right = NodeMutex(key, value, parent)
                    }
                    node
                }
                else -> {
                    node.unlock()
                    node // Узел со значением value уже существует, поэтому мы ничего не делаем
                }
            }
        }
    }

    // Удаление узла
    open suspend fun delete(key: T) {
        Mutex().lock()
        if (root != null) {
            root?.lock()
            root = thinDelete(root, key)
        }
    }

     protected suspend fun thinDelete(node: NodeMutex<T>?, key: T): NodeMutex<T>? {
        if (node == null) {
            return null
        }
        when {
            key == node.key -> {
                when {
                    node.left == null -> {
                        node.unlock()
                        return node.right
                    }
                    node.right == null -> {
                        node.unlock()
                        return node.left
                    }
                    else -> {
                        node.right?.lock()
                        var successor = node.right
                        while (successor?.left != null) {
                            successor = successor.left
                        }
                        node.key = successor!!.key
                        node.value = successor.value
                        node.unlock()
                        node.right = thinDelete(node.right, successor.key)
                        return node
                    }
                }
            }
            key < node.key -> {
                return if (node.left == null ){
                    node.unlock()
                    null
                } else {
                    node.left?.lock()
                    node.unlock()
                    node.left = thinDelete(node.left, key)
                    node
                }
            }
            else -> {
                return if (node.right == null ){
                    node.unlock()
                    null
                } else {
                    node.right?.lock()
                    node.unlock()
                    node.right = thinDelete(node.right, key)
                    node
                }
            }
        }
    }

    // Поиск узла
    open suspend fun search(key: T): Any? {
        return if (root == null) null
        else {
            root?.lock()
            search(root, key)
        }
    }

    private suspend fun search(node: NodeMutex<T>?, key: T): Any? {
        if (node == null) {
            return null
        } else {
            when {
                key == node.key -> {
                    node.unlock()
                    return node.value
                }
                key < node.key -> {
                    return if (node.left != null) {
                        node.unlock()
                        node.left?.lock()
                        search(node.left, key)
                    } else {
                        node.unlock()
                        null
                    }
                }
                else -> {
                    return if (node.right != null) {
                        node.unlock()
                        node.right?.lock()
                        search(node.right, key)
                    } else {
                        node.unlock()
                        null
                    }
                }
            }
        }
    }

    // Печать дерева
    open fun printTree() {
        print("Value: ")
        printTreeValue(root)
        println()
        print("Key:   ")
        printTreeKey(root)
    }

    private fun printTreeValue(node: NodeMutex<T>?) {
        if (node == null) {
            return
        }
        printTreeValue(node.left)
        print("${node.value} ")
        printTreeValue(node.right)
    }
    private fun printTreeKey(node: NodeMutex<T>?) {
        if (node == null) {
            return
        }
        printTreeKey(node.left)
        print("${node.key} ")
        printTreeKey(node.right)
    }
}