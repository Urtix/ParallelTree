import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val tree = ThinBST<Int>()

    coroutineScope {
        launch {
            tree.add(1)
            tree.add(3)
            tree.add(7)
            tree.add(9)
            tree.add(5)
        }

        launch {
            tree.add(0)
            tree.add(6)
            tree.add(2)
            tree.add(4)
            tree.add(8)
        }

    }
    tree.delete(1)


    tree.printTree()

}