package tasklist

import kotlinx.datetime.LocalDate
import java.lang.Exception
import kotlinx.datetime.*
import java.time.LocalTime
import java.io.File
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType


val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(LocalDateAdapter())
    .add(LocalDateTimeAdapter())
    .build()!!

val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)!!
val jsonAdapter = moshi.adapter<MutableList<Task>>(type)!!

class LocalDateTimeAdapter {
    @ToJson
    fun toJson(ldt: LocalTime): String {
        return ldt.toString()
    }

    @FromJson
    fun fromJson(ldt: String): LocalTime {
        return LocalTime .parse(ldt)
    }
}

class LocalDateAdapter {
    @ToJson
    fun toJson(ld: LocalDate?): String {
        return ld.toString()
    }

    @FromJson
    fun fromJson(ld: String): LocalDate {
        return LocalDate.parse(ld)
    }
}

class Task() {
    var items = mutableListOf<String>()
    var date: LocalDate? = null
    var time: LocalTime? = null
    var priority = 'L'


    fun add() {
        priority = inputPriority()
        date = inputData()
        time = inputTime()
        items = inputItems()
    }

}


fun inputItems(): MutableList<String> {

    var items = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    var theEnd = true
    while (theEnd) {
        var str = readln()
        if ((!chekStr(str)) && items.size == 0) {
            println("The task is blank")
            break
        } else
            if (str == "") break
            else items.add(str)
    }
    return items
}
fun inputPriority(): Char {
    var isValid = false
    var result = ""
    while (!isValid){
        println("Input the task priority (C, H, N, L):")
        result = readln()
        val regex = Regex("""^[CHNLcnhl]${'$'}""")
        if (result.contains(regex)) break else continue
    }
    return result.first()

}
fun inputTime(): LocalTime? {
    var isValidTime = false
    var time: LocalTime? = null
    // Запрос времени у пользователя
    while (!isValidTime) {
        print("Input the time (hh:mm):")
        val inputTime = readLine()!!
        try {
            var list = inputTime.split(":")
            time = LocalTime.of(list[0].toInt(),list[1].toInt())
            isValidTime = true
        } catch (e: Exception) {
            println("The input time is invalid")
        }
    }
    return time

}
fun inputData(): LocalDate {
    var result: LocalDate? = null
    var isValidDate = false
    while (!isValidDate) {
        print("Input the date (yyyy-mm-dd):")
        val inputDateStr = readLine()
        try {
            var list = inputDateStr?.split("-")
            result = LocalDate(list!![0].toInt(),list!![1].toInt(),list!![2].toInt())
            isValidDate = true
        } catch (e: Exception) {
           // println(result)
            println("The input date is invalid")
        }
    }
    return result!!
}

class TaskList() {
    var list = mutableMapOf<Int, Task>()
    fun add() {
        var str = " "
        var strList = Task()
        strList.add()
        list.put(list.size + 1, strList)
        }
    fun print(){
        var sepr = " "

        if (list.size == 0) println("No tasks have been input") else {
            println("+----+------------+-------+---+---+--------------------------------------------+\n" +
                    "| N  |    Date    | Time  | P | D |                   Task                     |\n" +
                    "+----+------------+-------+---+---+--------------------------------------------+")
            for (el in list){
                sepr = if (el.key < 10) "  " else " "
                print("| ${el.key}$sepr| ${el.value.date} | ${el.value.time} | ${checkPriority(el.value.priority)} | ${checkDue(dueTag(el.value.date))} |")

                for (i in 0..el.value.items.lastIndex) {
                    if (i != 0) print("|    |            |       |   |   |")
                    if (el.value.items[i].length > 44) {
                        printMor44(el.value.items[i])
                    } else {
                        print("%-${44}s".format(el.value.items[i]))
                        println("|")
                    }
                }
                println("+----+------------+-------+---+---+--------------------------------------------+")
                    }
                }
            }
    fun delete() {
        this.print()
        if (this.list.size == 0) return
        var isEndDelete = false
        while (!isEndDelete) {
            println("Input the task number (1-${this.list.size}):")
            try {
                var num = readln().toInt()
                if (num !in 1..this.list.size) {
                    println("Invalid task number")
                    continue
                } else {
                    var newList = TaskList()
                    this.list.remove(num)
                    var newCount = this.list.size
                    for (i in 1..newCount) {
                        if (i < num) {
                            this.list.get(i)?.let { newList.list.put(i, it) }
                        } else {
                            this.list.get(i+1)?.let { newList.list.put(i, it) }
                        }
                    }
                    isEndDelete = true
                    this.list.clear()
                    this.list = newList.list
                    println("The task is deleted")

                }
            } catch (e: Exception) {
                println("Invalid task number")
                continue
            }

            }
    }
    fun edit() {
        this.print()
        if (this.list.size == 0) return
        var isEndEdit = false
        while (!isEndEdit) {
            println("Input the task number (1-${this.list.size}):")
            try {
                var num = readln().toInt()
                if (num !in 1..this.list.size) {
                    println("Invalid task number")
                    continue
                } else {
                    var isEndField = false
                    while (!isEndField) {
                        println("Input a field to edit (priority, date, time, task):")
                        var editFieldStr = readln()!!
                        when (editFieldStr){
                            "priority" ->{
                                this.list.get(num)!!.priority = inputPriority() }
                            "date" -> {
                                this.list.get(num)!!.date = inputData()
                            }
                            "time" -> {
                                this.list.get(num)!!.time = inputTime()
                            }
                            "task" -> {
                                this.list.get(num)!!.items = inputItems()
                            }
                            else -> {
                                println("Invalid field")
                                continue
                            }
                        }
                        println("The task is changed")
                        return
                    }
                    }
            }
            catch (e: Exception){
                println("Invalid task number")
                continue
            }
            }
        }
    private fun editField(editFieldStr: String) {
        println("Input the time (hh:mm):")
        println("Input a new task (enter a blank line to end):")
        println("")
        when(editFieldStr){
        }

    }

    fun fromJson(str: String) {
        var list2 = jsonAdapter.fromJson(str)!!

        for (i in 0..list2.lastIndex) {
            list.put(i + 1,list2.get(i))
        }
    }
}

private fun dueTag(date: LocalDate?): Char {
    val taskDate = date
    val currentDate = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    val numberOfDays = taskDate?.let { currentDate.daysUntil(it) }
    var tag = if (numberOfDays == 0 ) 'T' else if (numberOfDays!! > 0) 'I' else 'O'
    return tag
}

fun main() {
    var theEnd = false
    var myList = TaskList()
    val jsonFile = File("tasklist.json")

    if (jsonFile.exists()) {
        var str = jsonFile.readText()
        if (str != "") {}
            myList.fromJson(str)
    } else jsonFile.createNewFile()


    while (!theEnd) {
        println("Input an action (add, print, edit, delete, end):")
        var action = readln()
        when (action) {
            "add" -> {
                myList.add()
            }
            "print" -> myList.print()
            "edit" -> myList.edit()
            "delete" -> myList.delete()
            "end" -> {
                println("Tasklist exiting!")

                var s = myList.list.map {
                    it.value
                }.toMutableList()
                jsonFile.writeText(jsonAdapter.toJson(s))
                return
            }
            else -> {
                println("The input action is invalid")
                continue
               }

            }
        }
    }




fun chekStr(str: String): Boolean {
    val regex = Regex("""[a-zA-Z]""")
    return str.contains(regex)
}

fun checkPriority(priority: Char): String {
    var res = when (priority.uppercaseChar()) {
        'C' -> "\u001B[101m \u001B[0m"
        'H' -> "\u001B[103m \u001B[0m"
        'N' -> "\u001B[102m \u001B[0m"
        'L' -> "\u001B[104m \u001B[0m"
        else -> "\u001B[104m \u001B[0m"
    }
    return res

}

private fun checkDue(dueTag: Char): String {
var res = when (dueTag.uppercaseChar()) {
    'I' -> "\u001B[102m \u001B[0m"
    'T' -> "\u001B[103m \u001B[0m"
    'O' -> "\u001B[101m \u001B[0m"
    else -> "\u001B[101m \u001B[0m"
}
    return  res
}
private fun printMor44(s: String) {
    var k = 0
    for (j in 0..s.lastIndex){
        k = k + 1
        print(s[j])
        if (k == 44 && j != s.lastIndex ) {
            print("|")
            println()
            print("|    |            |       |   |   |")
            k=0
        } else  if (j == s.lastIndex && k < 44) {
            print("%${45 - k}s".format("|"))
        }
        if (k == 44 && j == s.lastIndex ) print("|")
    }
    println()
}