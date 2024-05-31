import kotlin.random.Random
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class TestsOptimisticBST{
    private val nodes: Int = 500
    private val tree = OptimisticBST<Int>()
    @Test
    fun `one thread test`() = runBlocking {
        repeat(100) {
            val listRandomNodes = (0..nodes).shuffled().take(nodes)

            repeat(nodes) {
                tree.add(listRandomNodes[it])
            }

            repeat(nodes) {
                tree.delete(listRandomNodes[it])
            }

            for (key in listRandomNodes) {
                assertEquals(null, tree.search(key))
            }
        }

    }

    @Test
    fun `parallel test`() = runBlocking {
        repeat(100) {
            val listRandomNodes = (0..nodes).shuffled().take(nodes)
            val headNodes = listRandomNodes.subList(0, listRandomNodes.size / 2)
            val tailNodes = listRandomNodes.subList(listRandomNodes.size / 2, listRandomNodes.size)
            coroutineScope {

                launch {
                    repeat(nodes / 2) {
                        delay(50)
                        println(headNodes[it])
                        tree.add(headNodes[it])
                    }
                }
                launch {
                    repeat(nodes / 2) {
                        delay(50)
                        tree.add(tailNodes[it])
                    }
                }

            }

            val nodesToDelete = listRandomNodes.shuffled(Random).take(nodes / 2)
            val headNodesToDelete = nodesToDelete.subList(0, nodesToDelete.size / 2)
            val tailNodesToDelete = nodesToDelete.subList(nodesToDelete.size / 2, nodesToDelete.size)

            coroutineScope {
                launch {
                    repeat(nodes / 4) {
                        delay(50)
                        tree.delete(headNodesToDelete[it])
                    }
                }
                launch {
                    repeat(nodes / 4) {
                        delay(50)
                        tree.delete(tailNodesToDelete[it])
                    }
                }
            }

            for (key in listRandomNodes) {
                if (key !in nodesToDelete) {
                    assertEquals(key, tree.search(key))
                } else {
                    assertEquals(null, tree.search(key))
                }
            }

        }
    }
}