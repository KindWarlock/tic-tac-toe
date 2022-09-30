import org.intellij.lang.annotations.JdkConstants.CursorType

class Field (val size: Int = 3) {
    var field = Array(size){ Array(size) { Array(size) { CharArray(size) {'-'} } } }

    fun showSmallField(x: Int, y: Int): Int {
        if (x >= size || y >= size) {
            println("Out of range")
            return -1
        }
        val smallField = field[y][x]
        for (i in 0 until size) {
            for (k in 0 until size) {
                print("${smallField[i][k]} ")
            }
            println()
        }
        return 0
    }

    fun showField() {
        for (y in 0 until size * size) {
            val bigY = y / size
            val smallY = y % size

            // разделение на маленькие поля по горизонтали
            if (smallY == 0 && bigY != 0) {
                // size ** 2 - кол-во ячеек, size - 1 - разделители, ... * 2 - 1 из-за пробелов
                for (x in 0 until (size * size + (size - 1)) * 2 - 1 ) {
                    print("-")
                }
                println()
            }

            for (x in 0 until size * size) {
                val bigX = x / size
                val smallX = x % size

                // разделение на маленькие поля по вертикали
                if (smallX == 0 && bigX != 0)
                    print("| ")
                print("${field[bigY][bigX][smallY][smallX]} ")
            }
            println()
        }
    }

    fun checkDraw(x: Int, y: Int):Boolean {
        val smallField = field[y][x]
        var isFull = true
        firstLoop@ for (_y in 0 until this.size) {
            for (_x in 0 until this.size) {
                if (smallField[_y][_x] == '-') {
                    isFull = false
                    break@firstLoop
                }
            }
        }
        return isFull
    }

    fun checkVictory(x: Int, y: Int, sign: Char): Boolean {
        val smallField = field[y][x]

        // можно было создать массив возможных вариантов (строк, столбцов и диагоналей), найти в нем только те,
        // в которых встречается последний ход и анализировать их, но, мне кажется, это займет еще больше времени

        // строки и столбцы
        for (_y in 0 until this.size) {
            var winRow = true
            var winCol = true
            for (_x in 0 until this.size) {
                if (smallField[_y][_x] != sign) {
                    winRow = false
                }
                if (smallField[_x][_y] != sign) {
                    winCol = false
                }
            }
            if (winCol || winRow)
                return true
        }

        // диагонали
        var winDiag = true
        for (i in 0 until this.size) {
            if (smallField[i][i] != sign) {
                winDiag = false
                break
            }
        }
        if (winDiag)
            return true

        winDiag = true
        for (i in 0 until this.size) {
            if (smallField[i][(size - 1) - i] != sign) {
                winDiag = false
                break
            }
        }
        if (winDiag)
            return true

        return false
    }
}

class Player(val sign: Char, private val field: Field) {
    var x = 0
    var y = 0

    fun makeMove(x: Int, y: Int): Int {
        if (x >= field.size || y >= field.size) {
            println("Out of range")
            return -1
        }
        if (field.field[this.y][this.x][y][x] != '-') {
            println("Already used")
            return -1
        }

        field.field[this.y][this.x][y][x] = this.sign
        if (field.checkVictory(this.x, this.y, this.sign)) {
            return 1
        }

        this.x = x
        this.y = y
        if (field.checkDraw(x, y)) {
            return 2
        }
        return 0
    }
}

fun togglePlayer(curr: Player, p1: Player, p2: Player) = if (curr == p1) p2 else p1

fun main() {
    print("Write N: ")
    val n = readln().toInt()
    val field = Field(n)
    val p1 = Player('x', field)
    val p2 = Player('o', field)

    var moveResult = -1
    var currPlayer = p1
    println("\n+++++++++++++++++++++++++++++++++++++++++++\n" +
            "RULES:\n" +
            "- write x y to make your move\n" +
            "- write f to see the whole field\n" +
            "- write fs to see your current small field\n" +
            "- write sur to surrender.\n" +
            "Good luck!\n" +
            "+++++++++++++++++++++++++++++++++++++++++++\n")
    println("GAME START")

    turnLoop@ while (true) {
        println("PLAYER '${currPlayer.sign}' TURN. FIELD: ${currPlayer.x}, ${currPlayer.y}")

        readLoop@ while (true) {
            val str = readln().trim()
            when (str) {
                "sur" -> {
                    currPlayer = togglePlayer(currPlayer, p1, p2)
                    break@turnLoop
                }
                "f" -> field.showField()
                "fs" -> field.showSmallField(currPlayer.x, currPlayer.y)
                "" -> continue@readLoop
                else -> {
                    val (x:Int, y:Int) = str.split(' ').map(String::toInt)
                    moveResult = currPlayer.makeMove(x, y)
                    if (moveResult > -1) {
                        break@readLoop
                    }
                }   // else end
            }   // when end
        }   // readLoop end
        when (moveResult) {
            0 -> currPlayer = togglePlayer(currPlayer, p1, p2)
            1, 2 -> break@turnLoop
        }
    }
    when (moveResult) {
        1 -> println("PLAYER '${currPlayer.sign}' IS VICTORIOUS!")
        2 -> println("DRAW")
    }

    field.showField()
}

/* ============================== LOG ==============================

Write N: 3

+++++++++++++++++++++++++++++++++++++++++++
RULES:
- write x y to make your move
- write f to see the whole field
- write fs to see your current small field
- write sur to surrender.
Good luck!
+++++++++++++++++++++++++++++++++++++++++++

GAME START
PLAYER 'x' TURN. FIELD: 0, 0
0 1
PLAYER 'o' TURN. FIELD: 0, 0
1 2
PLAYER 'x' TURN. FIELD: 0, 1
0 0
PLAYER 'o' TURN. FIELD: 1, 2
2 2
PLAYER 'x' TURN. FIELD: 0, 0
0 2
PLAYER 'o' TURN. FIELD: 2, 2
2 2
PLAYER 'x' TURN. FIELD: 0, 2
0 0
PLAYER 'o' TURN. FIELD: 2, 2
1 2
PLAYER 'x' TURN. FIELD: 0, 0
fs
- - -
x - -
x o -
0 0
PLAYER 'x' IS VICTORIOUS!
x - - | - - - | - - - 
x - - | - - - | - - - 
x o - | - - - | - - - 
---------------------
x - - | - - - | - - - 
- - - | - - - | - - - 
- - - | - - - | - - - 
---------------------
x - - | - - - | - - - 
- - - | - - - | - - - 
- - - | - - o | - o o 
*/
