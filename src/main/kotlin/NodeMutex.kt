import kotlinx.coroutines.sync.Mutex

class NodeMutex <T : Comparable<T>>(var key: T, var value: T, val parent: NodeMutex<T>?) {
    var left: NodeMutex<T>? = null
    var right: NodeMutex<T>? = null

    private val mutex = Mutex()
    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()
}