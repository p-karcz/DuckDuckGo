package com.example

import java.io.File
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.collections.*

/**
 * Created by Andre Perkins (akperkins1@gmail.com) on 6/6/17.
 *
 * Kotlin script
 *
 * A simple script that executes a UI automation tests while, simultaneously, querying the Jank information of
 * the connected device via gfxinfo and then outputs a subset of the results.
 */

fun Map<String, String>.toJson(): String {
    if (this.isEmpty()){
        return "{}"
    }
    return StringBuilder("")
        .append("{")
        .append("\n")
        .append(this.map { (k, v) -> "  \"$k\": \"$v\"" }.reduce {o, n -> "$o,\n$n" })
        .append("\n}")
        .toString()
}

fun String.startsWithDigit() = Character.isDigit(this[0])

fun String.save(fileName: String): Unit {
    File(fileName).printWriter().use { out ->
        out.print(this)
    }
}

fun String.extractValues(): Map<String, String> {
    val mapStats: MutableMap<String, String> = mutableMapOf()
    val parts: List<String> = split("\\n".toRegex())
    parts.forEach {
        val split: List<String> = it.split(":")
        if (split.size != 2 || split[0].isBlank() || split[1].isBlank()){
            return@forEach
        }
        if (split[0] == "Janky frames") {
            val split_number_data = split[1].split("(")
            val number_of_janky_frames = split_number_data[0]
            mapStats["Janky frames"] = number_of_janky_frames.trim()
            val percentage_of_janky_frames = split_number_data[1].replace(")", "")
            mapStats["Janky frames Percentage"] = percentage_of_janky_frames.trim()
        } else {
            mapStats[split[0]] = split[1].trim()
        }
    }
    return mapStats
}

private fun extract_gfx_info(apkName: String) = "adb shell dumpsys gfxinfo $apkName".runCommand()

private fun app_process_still_alive(current_stats: String) = !current_stats.contains("No process found for:")

private fun startTest(startTestRunnerCommand: String) = startTestRunnerCommand.runCommand()


private fun String.runCommand(): String {
    return try {
        val parts = this.split("\\s".toRegex())
        val process = ProcessBuilder(*parts.toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        process.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        throw RuntimeException("Un-able to properly execute system call: $this",  e )
    }
}

private fun cutIrrelevantData(data : String) : String {
    var cutData = data.split("\n")
    val sb = StringBuilder()
    for (singleLine in cutData) {
        if (singleLine.contains("Total frames rendered") || singleLine.contains("Janky frames") || singleLine.contains("th percentile")) {
            sb.append(singleLine).append("\n")
        }
    }
    return sb.toString()
}

private fun extractTestName(command : String) : String {
    return command
        .split("com.duckduckgo.")[1]
        .split(" ")[0]
        .replace("\'", "")
        .replace("\"", "")
        .replace(".", "-")
}

fun main(args: Array<String>) {
    val apkName = args[0]

    for (i in 1..(args.size - 1)) {
        var testName: String = extractTestName(args[i])

        print("starting test " + i + " " + testName + "\n")

        val instrumentThread: Thread = thread {
            startTest(args[i])
        }

        var current_stats: String
        var stats: String = ""

        while (instrumentThread.isAlive) {
            current_stats = extract_gfx_info(apkName)
            if (app_process_still_alive(current_stats)) {
                stats = current_stats
            }
        }

        stats = cutIrrelevantData(stats)

        stats.save(testName + ".txt")
        val extractedValues = stats.extractValues()
        extractedValues.toJson().save(testName + ".json")
    }
}
