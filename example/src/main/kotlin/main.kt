import kotlinx.coroutines.runBlocking
import pro.horovodovodo4ka.astaroth.Log
import pro.horovodovodo4ka.astaroth.LogLevel
import pro.horovodovodo4ka.astaroth.LogType
import pro.horovodovodo4ka.astaroth.Logger
import pro.horovodovodo4ka.astaroth.d
import pro.horovodovodo4ka.astaroth.isAbleToLog
import pro.horovodovodo4ka.kodable.core.Koder
import pro.horovodovodo4ka.shu.ShuRemote
import pro.horovodovodo4ka.shu.ShuRemoteDefault
import pro.horovodovodo4ka.shu.kodable.decoder
import pro.horovodovodo4ka.shu.getOrThrow
import pro.horovodovodo4ka.shu.kodable.digInto
import pro.horovodovodo4ka.shu.kodable.encoder
import pro.horovodovodo4ka.shu.resource.ro
import pro.horovodovodo4ka.shu.resource.Rpc
import pro.horovodovodo4ka.shu.resource.operations.Readable
import pro.horovodovodo4ka.shu.resource.ShuROResource
import pro.horovodovodo4ka.shu.resource.operations.deferredCall
import pro.horovodovodo4ka.shu.resource.operations.read
import pro.horovodovodo4ka.shu.resource.rpc

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

object TestResource : ShuROResource<TestModel> by ro(API, "/get", decoder = TestModel_Kodable.decoder.digInto(".headers")),
    Readable

object TestRPC : Rpc<A, TestModel> by rpc(API, "post", encoder = A_Kodable.encoder, decoder = TestModel_Kodable.decoder)

@Koder
data class A(val i: Int?)

@Koder
data class TestModel(val Accept: String, val Host: String)

fun main() {
    Log.addLoggers(PrintLogger())

    val params = mapOf("a" to 1, "b" to listOf(1, 2))

    runBlocking {
        val r1 = TestRPC.deferredCall(A(1)).digInto(".headers").run().getOrThrow().value
        val (res) = TestResource.read(parameters = params).getOrThrow()
        Log.d(res)
    }
}