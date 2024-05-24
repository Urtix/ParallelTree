open class BinarySearchTree<T : Comparable<T>> {
    private var root: Node<T>? = null

    // Вставка узла
    open fun add(key: T, value: T) {
        root = add(root, key, value)
    }

    open fun add(key: T) {
        add(key, key)
    }

    protected fun add(node: Node<T>?, key: T, value: T): Node<T> {
        return if (node == null) {
            Node(key, value)
        } else {
            when {
                key < node.key -> {
                    node.left = add(node.left, key, value)
                    node
                }

                key > node.key -> {
                    node.right = add(node.right, key, value)
                    node
                }

                else -> node // Узел со значением value уже существует, поэтому мы ничего не делаем
            }
        }
    }

    // Удаление узла
    open fun delete(key: T) {
        root = delete(root, key)
    }

    protected fun delete(node: Node<T>?, key: T): Node<T>? {
        if (node == null) {
            return null
        }

        when {
            key == node.key -> {
                return when {
                    node.left == null -> node.right
                    node.right == null -> node.left
                    else -> {
                        var successor = node.right
                        while (successor?.left != null) {
                            successor = successor.left
                        }
                        node.key = successor!!.key
                        node.value = successor!!.value
                        node.right = delete(node.right, successor.key)
                        node
                    }
                }
            }
            key < node.key -> {
                node.left = delete(node.left, key)
                return node
            }
            else -> {
                node.right = delete(node.right, key)
                return node
            }
        }
    }

    // Поиск узла
    open fun search(key: T): Any? {
        return search(root, key)
    }

    protected fun search(node: Node<T>?, key: T): Any? {
        return if (node == null) {
            null
        } else {
            when {
                key == node.key -> node.value
                key < node.key -> search(node.left, key)
                else -> search(node.right, key)
            }
        }
    }

    // Печать дерева
    open fun printTree() {
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
        print("${node.value} ")
        printTreeKey(node.right)
    }
}