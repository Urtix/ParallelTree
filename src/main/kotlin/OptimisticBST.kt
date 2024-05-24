
open class OptimisticBST<T : Comparable<T>> : ThinBST<T>()  {
    private var root: NodeMutex<T>? = null

    // Вставка узла
    override suspend fun add(key: T, value: T) {
        root = if (root?.value != null) {
            add(root, key, value, null)
        } else {
            NodeMutex(key, value, null)
        }
    }

    override suspend fun add(key: T) {
        add(key, key)
    }

    private suspend fun add(node: NodeMutex<T>?, key: T, value: T, parent: NodeMutex<T>?): NodeMutex<T> {
        return if (node == null) {
            NodeMutex(key, value, parent)
        } else {
            when {
                value < node.value -> {
                    if (node.left == null) {
                        node.lock()
                        node.parent?.lock()
                        val newNode = doubleSearch(root, node.value)
                        if ((newNode == node) && (newNode.parent == node.parent)) {
                            node.left = NodeMutex(key, value, parent)
                            node.unlock()
                            node.parent?.unlock()
                            
                        } else {
                            add(root, key, value, null)
                        }
                    } else {
                        node.left = add(node.left, key, value, null)
                    }
                    node
                }

                value > node.value -> {
                    if (node.right == null) {
                        node.lock()
                        node.parent?.lock()
                        val newNode = doubleSearch(root, node.value)
                        if ((newNode == node) && (newNode.parent == node.parent)) {
                            node.right = NodeMutex(value, key, parent)
                            node.unlock()
                            node.parent?.unlock()

                        } else {
                            root = add(root, key, value, null)
                        }
                    } else {
                        node.right = add(node.right, key, value, node)
                    }
                    node
                }

                else -> node // Узел со значением value уже существует, поэтому мы ничего не делаем
            }
        }
    }

    // Удаление узла
    override suspend fun delete(key: T) {
        root = optimisticDelete(root, key)
    }

    private suspend fun optimisticDelete(node: NodeMutex<T>?, key: T): NodeMutex<T>? {
        if (node == null) {
            return null
        }

        when {
            key == node.key -> {
                node.parent?.lock()
                node.lock()
                val newNode = doubleSearch(root, node.key)
                if ((newNode == node) && (newNode.parent == node.parent)) {
                    return when {
                        node.left == null -> {
                            node.parent?.unlock()
                            node.unlock()
                            node.right
                        }
                        node.right == null -> {
                            node.parent?.unlock()
                            node.unlock()
                            node.left
                        }
                        else -> {
                            var successor = node.right
                            while (successor?.left != null) {
                                successor = successor.left
                            }
                            node.right?.lock()
                            node.right = thinDelete(node.right, successor!!.key)
                            node.key = successor.key
                            node.value = successor.value
                            node.parent?.unlock()
                            node.unlock()
                            return node
                        }
                    }
                } else {
                    node.parent?.unlock()
                    node.unlock()
                    return optimisticDelete(root, key)
                }
            }
            key < node.key -> {
                node.left = optimisticDelete(node.left, key)
                return node
            }
            else -> {
                node.right = optimisticDelete(node.right, key)
                return node
            }
        }
    }

    // Поиск узла
    override suspend fun search(key: T): Any? {
        return search(root, key)
    }

    private suspend fun search(node: NodeMutex<T>?, key: T): Any? {
        return if (node == null) {
            null
        } else {
            when {
                key == node.key -> {
                    node.parent?.lock()
                    node.lock()
                    val newNode = doubleSearch(root, key)
                    if ((newNode == node) && (newNode.parent == node.parent)) {
                        node.unlock()
                        node.parent?.unlock()
                        return node.value
                    } else {
                        search(root, key)
                    }
                }
                key < node.value -> search(node.left, key)
                else -> search(node.right, key)
            }
        }
    }

    private fun doubleSearch(node: NodeMutex<T>?, key: T): NodeMutex<T>? {
        return if (node == null) {
            null
        } else {
            when {
                key == node.key -> node
                key < node.key -> doubleSearch(node.left, key)
                else -> doubleSearch(node.right, key)
            }
        }
    }

    // Печать дерева
    override fun printTree() {
        printTree(root)
    }

    private fun printTree(node: NodeMutex<T>?) {
        if (node == null) {
            return
        }
        printTree(node.left)
        print("${node.value} ")
        printTree(node.right)
    }
}