import kotlinx.coroutines.runBlocking
import pro.horovodovodo4ka.astaroth.Log
import pro.horovodovodo4ka.astaroth.LogLevel
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.astaroth.Logger
import pro.horovodovodo4ka.astaroth.d
import pro.horovodovodo4ka.astaroth.isAbleToLog
import pro.horovodovodo4ka.kodable.core.Koder
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.ShuRemoteDefault
import pro.horovodovodo4ka.shu.coders.decoder
import pro.horovodovodo4ka.shu.coders.digInto
import pro.horovodovodo4ka.shu.getOrThrow
import pro.horovodovodo4ka.shu.resource.Readable
import pro.horovodovodo4ka.shu.resource.ShuResource
import pro.horovodovodo4ka.shu.resource.deferredRead
import pro.horovodovodo4ka.shu.resource.read

class PrintLogger : Logger {
    override var config = Logger.Config()

    companion object {
        private val levels = mapOf(
            LogLevel.Verbose to "💙",
            LogLevel.Debug to "💚",
            LogLevel.Info to "⚪️",
            LogLevel.Warning to "💛",
            LogLevel.Error to "❤️",
            LogLevel.WhatTheFuck to "💔"
        )
    }

    override fun log(message: Lazy<Any>, level: LogLevel, type: LogType) {
        if (!isAbleToLog(level, type)) return
        val tag = type.logTag
        val prefix = levels.getValue(level)
        val stringMessage = "$prefix[$tag] ${message.value}\r\n"
        println(stringMessage)
    }
}

object API : ShuRemote by ShuRemoteDefault("https://httpbin.org/") {
    init {
        addMiddleware {
            headers { mapOf("X-OS" to "Android") }
        }
    }
}

class TestRequest(customHeaders: Headers? = null) : ShuResource<TestModel>(API, "/get", customHeaders, resourceDecoder = TestModel_Kodable.decoder.digInto(".headers")),
    Readable

@Koder
data class TestModel(val Accept: String, val Host: String)

fun main() {
    Log.addLoggers(PrintLogger())

    val params = mapOf("a" to 1, "b" to listOf(1, 2))

    val request = TestRequest()

    runBlocking {
        val res = request.read(parameters = params).getOrThrow()
        Log.d(res)
    }
}