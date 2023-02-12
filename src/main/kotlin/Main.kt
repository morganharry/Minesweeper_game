package minesweeper

import kotlin.random.Random

class Cells(
    val x: Int,
    val y: Int,
    var sign: Char = '.',
    var listOfNeighb: MutableList<MutableList<Int>> = mutableListOf(mutableListOf<Int>(), mutableListOf<Int>())
) {
    //Делаем свойство для объекта - список соседних клеток
    fun checkNeighbours(field: MutableList<MutableList<Cells>>) {
        val x = this.x
        val y = this.y
        when {
            x == 0 && y == 0 -> {
                this.listOfNeighb[0] = mutableListOf(x + 1, x + 1, x)
                this.listOfNeighb[1] = mutableListOf(y, y + 1, y + 1)
            }

            x == 0 && y == field.lastIndex -> {
                this.listOfNeighb[0] = mutableListOf(x, x + 1, x + 1)
                this.listOfNeighb[1] = mutableListOf(y - 1, y - 1, y)
            }

            x == field[y].lastIndex && y == 0 -> {
                this.listOfNeighb[0] = mutableListOf(x, x - 1, x - 1)
                this.listOfNeighb[1] = mutableListOf(y + 1, y + 1, y)
            }

            x == field[y].lastIndex && y == field.lastIndex -> {
                this.listOfNeighb[0] = mutableListOf(x - 1, x - 1, x)
                this.listOfNeighb[1] = mutableListOf(y, y - 1, y - 1)
            }

            x == 0 -> {
                this.listOfNeighb[0] = mutableListOf(x, x + 1, x + 1, x + 1, x)
                this.listOfNeighb[1] = mutableListOf(y - 1, y - 1, y, y + 1, y + 1)
            }

            y == 0 -> {
                this.listOfNeighb[0] = mutableListOf(x + 1, x + 1, x, x - 1, x - 1)
                this.listOfNeighb[1] = mutableListOf(y, y + 1, y + 1, y + 1, y)
            }

            x == field[y].lastIndex -> {
                this.listOfNeighb[0] = mutableListOf(x, x - 1, x - 1, x - 1, x)
                this.listOfNeighb[1] = mutableListOf(y + 1, y + 1, y, y - 1, y - 1)
            }

            y == field.lastIndex -> {
                this.listOfNeighb[0] = mutableListOf(x - 1, x - 1, x, x + 1, x + 1)
                this.listOfNeighb[1] = mutableListOf(y, y - 1, y - 1, y - 1, y)
            }

            else -> {
                this.listOfNeighb[0] = mutableListOf(x, x + 1, x + 1, x + 1, x, x - 1, x - 1, x - 1)
                this.listOfNeighb[1] = mutableListOf(y - 1, y - 1, y, y + 1, y + 1, y + 1, y, y - 1)
            }
        }
    }

    // Проверка на соседство с миной
    fun advise(field: MutableList<MutableList<Cells>>) {
        var numOfMine = 0
        for (i in this.listOfNeighb.first().indices) {
            val x = this.listOfNeighb[0][i]
            val y = this.listOfNeighb[1][i]
            if (field[y][x].sign == 'X') numOfMine++
        }
        if (numOfMine != 0) this.sign = Character.forDigit(numOfMine, 10)
    }
}

//Генерация поля для интерфейса
fun generateFieldView(width: Int, height: Int): MutableList<MutableList<Cells>> {
    val field = mutableListOf<MutableList<Cells>>()
    for (y in 0 until height) {
        val line = mutableListOf<Cells>()
        for (x in 0 until width) {
            line.add(x, Cells(x, y))
        }
        field.add(y, line)
    }
    return field
}

//Первый ход. Всегда безопасный
fun firstMove(freeFieldView: MutableList<MutableList<Cells>>): MutableList<MutableList<Cells>> {
    var statusFirstMove = "FirstMove"
    do {
        print("Set/unset mines marks or claim a cell as free:")
        val move = readln().split(" ").toMutableList()
        val x = move[0].toInt() - 1
        val y = move[1].toInt() - 1
        val comand = move[2]
        when (comand) {
            "free" -> {
                freeFieldView[y][x].sign = '/'
                statusFirstMove = "Start"
            }

            "mine" -> {
                if (freeFieldView[y][x].sign == '.') freeFieldView[y][x].sign = '*'
                else freeFieldView[y][x].sign = '.'
                printField(freeFieldView)
            }
        }
    } while (statusFirstMove == "FirstMove")
    return freeFieldView
}

//Последующие ходы
fun nextMove(fieldView: MutableList<MutableList<Cells>>): MutableList<MutableList<Cells>> {
    print("Set/unset mines marks or claim a cell as free:")
    val move = readln().split(" ").toMutableList()
    val x = move[0].toInt() - 1
    val y = move[1].toInt() - 1
    val comand = move[2]
    when (comand) {
        "free" -> fieldView[y][x].sign = '/'
        "mine" -> {
            if (fieldView[y][x].sign == '.') fieldView[y][x].sign = '*'
            else fieldView[y][x].sign = '.'
        }
    }
    return fieldView
}

//Генерируем игровое поле с минами и подсказками
fun generateFieldGame(fieldView: MutableList<MutableList<Cells>>, numOfMines: Int): MutableList<MutableList<Cells>> {
    var numOfMines = numOfMines
    val fieldGame = mutableListOf<MutableList<Cells>>()
    for (y in fieldView.indices) {
        val fieldGameline = mutableListOf<Cells>()
        for (x in fieldView[y].indices) {
            fieldGameline.add(x, Cells(x, y, '/'))
        }
        fieldGame.add(y, fieldGameline)
    }
    while (numOfMines > 0) {
        val xMine = Random.nextInt(fieldGame.first().size)
        val yMine = Random.nextInt(fieldGame.size)
        if (fieldGame[yMine][xMine].sign != 'X' && fieldView[yMine][xMine].sign != '/') {
            fieldGame[yMine][xMine].sign = 'X'
            numOfMines--
        }
    }
    for (y in fieldGame.indices) {
        for (x in fieldGame[y].indices) {
            fieldGame[y][x].checkNeighbours(fieldGame)
            if (fieldGame[y][x].sign != 'X') {
                fieldGame[y][x].advise(fieldGame)
            }
        }
    }
    return fieldGame
}

//Проверка хода
fun checkField(
    fieldView: MutableList<MutableList<Cells>>,
    fieldGame: MutableList<MutableList<Cells>>
): MutableList<MutableList<Cells>> {
    do {
        var count = 0
        var safeOfGame = 0
        var safeOfView = 0

        for (y in fieldView.indices) {
            for (x in fieldView[y].indices) {
                if (fieldView[y][x].sign == '/') {
                    when {
                        (fieldGame[y][x].sign in '1'..'8') -> fieldView[y][x].sign = fieldGame[y][x].sign
                        (fieldGame[y][x].sign == 'X') -> fieldView[y][x].sign = fieldGame[y][x].sign
                        else -> {

                            for (i in fieldGame[y][x].listOfNeighb.first().indices) {
                                val xCheck = fieldGame[y][x].listOfNeighb[0][i]
                                val yCheck = fieldGame[y][x].listOfNeighb[1][i]
                                if (fieldGame[yCheck][xCheck].sign == '/') {
                                    safeOfGame++
                                }
                                if (fieldView[yCheck][xCheck].sign == '/') {
                                    safeOfView++
                                }
                                if (fieldGame[yCheck][xCheck].sign != 'X') fieldView[yCheck][xCheck].sign = fieldGame[yCheck][xCheck].sign
                            }

                        }
                    }

                }
            }
        }
        count = safeOfGame - safeOfView
    } while (count != 0)
    return fieldView
}

//Проверка окончания игры
fun checkEnd(
    fieldView: MutableList<MutableList<Cells>>,
    fieldGame: MutableList<MutableList<Cells>>
): String {
    var status = "game"
    var countXX = 0
    var countX = 0
    var countZ = 0
    for (y in fieldGame.indices) {
        for (x in fieldGame[y].indices) {
            if (fieldView[y][x].sign == 'X') status = "endOver"
            else {
                if (fieldGame[y][x].sign == 'X' && fieldView[y][x].sign == '*') countXX++
                if (fieldGame[y][x].sign == 'X') countX++
                if (fieldView[y][x].sign == '*') countZ++
            }
        }
    }
    if (status != "endOver") {
        if (countXX == countX && countXX == countZ) status = "endWin"
    }
    return status
}

fun seeAllMine(
    fieldView: MutableList<MutableList<Cells>>,
    fieldGame: MutableList<MutableList<Cells>>
): MutableList<MutableList<Cells>> {
    for (y in fieldView.indices) {
        for (x in fieldView[y].indices) {
            if (fieldGame[y][x].sign == 'X') fieldView[y][x].sign = 'X'
        }
    }
    return fieldView
}

fun printField(field: MutableList<MutableList<Cells>>) {
    val printField = mutableListOf<MutableList<Char>>()
    for (y in field.indices) {
        val printFieldLine = mutableListOf<Char>()
        for (x in field[y].indices) {
            printFieldLine.add(x, field[y][x].sign)
        }
        printField.add(y, printFieldLine)
    }
    println()
    println(" │123456789│")
    println("-│---------│")
    for (i in printField.indices) {
        println("${i + 1}│${printField[i].joinToString("")}│")
    }
    println("-│---------│")
}


fun main() {
    val width = 9
    val height = 9
    print("How many mines do you want on the field?")
    var numOfMines = readln().toInt()
    var fieldView = generateFieldView(width, height) //Генерируем поле для интерфейса
    printField(fieldView)
    fieldView = firstMove(fieldView)  //Первый ход. Всегда безопасный!
    val fieldGame = generateFieldGame(fieldView, numOfMines) //Генерируем поле с минами и подсказками
    var count = 0
    var status = "game"
    do {
        if (count > 0) {
            fieldView = nextMove(fieldView)
        }
        count++
        fieldView = checkField(fieldView, fieldGame) // Проверка соседних полей после первого хода

        status = checkEnd(fieldView, fieldGame)    //Проверка окончания игры
        when (status) {
            "endWin" -> {
                status = "end"
                printField(fieldView)
                println("Congratulations! You found all the mines!")
            }
            "endOver" -> {

                status = "end"
                fieldView = seeAllMine (fieldView, fieldGame)
                printField(fieldView)
                println("You stepped on a mine and failed!")
            }
            else -> printField(fieldView)
        }
    } while (status != "end")
}

