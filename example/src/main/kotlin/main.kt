import com.github.fluidsonic.fluid.json.JSONReader
import kotlinx.coroutines.runBlocking
import pro.horovodovodo4ka.kodable.core.DefaultKodableForType
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.kodable.core.Koder
import pro.horovodovodo4ka.shu.ApiClient
import pro.horovodovodo4ka.shu.ApiClientImpl
import pro.horovodovodo4ka.shu.ApiRequest
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.extension.uriQuery
import pro.horovodovodo4ka.shu.extension.uriQueryString
import java.net.URI
import java.nio.charset.Charset

@DefaultKodableForType(Unit::class)
object Unit_Kodable : IKodable<Unit> {
    override fun readValue(reader: JSONReader) = Unit
}

class TestRequest(customHeaders: QueryParameters? = null, apiClient: ApiClient) : ApiRequest<TestModel, Unit>("",  customHeaders, apiClient) {
    override val decodable: IKodable<TestModel> = TestModel::class.kodable()
    override val encodable: IKodable<Unit> = Unit_Kodable
}

@Koder
data class TestModel(val url: String)

fun main() {
//    val query: Map<String, String>
    val params = mapOf("a" to 1, "b" to listOf(1, 2))

        val client = ApiClientImpl("https://httpbin.org/")
        val request = TestRequest(client)
        runBlocking {
            val res = request.read("get")
            print(res)
        }
}