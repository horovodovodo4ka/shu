import com.github.fluidsonic.fluid.json.JSONReader
import kotlinx.coroutines.runBlocking
import pro.horovodovodo4ka.astaroth.*
import pro.horovodovodo4ka.kodable.core.DefaultKodableForType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.Koder
import pro.horovodovodo4ka.shu.ApiClient
import pro.horovodovodo4ka.shu.ApiClientImpl
import pro.horovodovodo4ka.shu.ApiRequest
import pro.horovodovodo4ka.shu.Headers
import pro.horovodovodo4ka.shu.extension.uriQuery

object PrintLogger : Logger {
    override var config = Logger.Config()

    override fun log(message: Lazy<Any>, level: LogLevel, type: LogType) {
        println("${type.logTag} [${level.name}] ${message.value}")
    }
}

@DefaultKodableForType(Unit::class)
object Unit_Kodable : IKodable<Unit> {
    override fun readValue(reader: JSONReader) = Unit
}

class TestRequest(customHeaders: Headers? = null, apiClient: ApiClient) : ApiRequest<TestModel, Unit>("", customHeaders, apiClient) {
    override val decodable: IKodable<TestModel> = TestModel::class.kodable()
    override val encodable: IKodable<Unit> = Unit_Kodable
}

@Koder
data class TestModel(val url: String)

fun main() {
    Log.addLoggers(PrintLogger)

//    val query: Map<String, String>
    val params = mapOf("a" to 1, "b" to listOf(1, 2))
    val p = params.uriQuery()

    val client = ApiClientImpl("https://httpbin.org/")
    val request = TestRequest(apiClient = client)
    runBlocking {
        val res = request.read("get", params)
        Log.d(res)
    }
}