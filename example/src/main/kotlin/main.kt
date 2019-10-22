import com.github.fluidsonic.fluid.json.JSONReader
import kotlinx.coroutines.runBlocking
import pro.horovodovodo4ka.astaroth.Log
import pro.horovodovodo4ka.astaroth.LogLevel
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.astaroth.Logger
import pro.horovodovodo4ka.astaroth.d
import pro.horovodovodo4ka.astaroth.isAbleToLog
import pro.horovodovodo4ka.kodable.core.DefaultKodableForType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.Koder
import pro.horovodovodo4ka.shu.ApiClientImpl
import pro.horovodovodo4ka.shu.ShuResource
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.decoder
import pro.horovodovodo4ka.shu.digInto
import pro.horovodovodo4ka.shu.extension.uriQuery

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

@DefaultKodableForType(Unit::class)
object Unit_Kodable : IKodable<Unit> {
    override fun readValue(reader: JSONReader) = Unit
}

class TestRequest(customHeaders: Headers? = null) : ShuResource<TestModel>("", customHeaders, resourceDecoder = TestModel_Kodable.decoder.digInto(".headers"))

@Koder
data class TestModel(val Accept: String)

fun main() {
    Log.addLoggers(PrintLogger())

    val params = mapOf("a" to 1, "b" to listOf(1, 2))

    val client = ApiClientImpl("https://httpbin.org/")

    client.addMiddleware {
        headers { mapOf("X-OS" to "Android") }
    }

    val request = TestRequest()
    runBlocking {
        val res = request.read("get", params).with(client).await()
        Log.d(res)
    }
}