package pro.horovodovodo4ka.shu

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Method.DELETE
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.core.Method.POST
import com.github.kittinunf.fuel.core.Method.PUT
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import pro.horovodovodo4ka.kodable.core.IKodable

open class Operation<RequestType : Any, ResponseType : Any>(
    val method: Method,
    val path: String,
    val queryParameters: QueryParameters? = null,
    val headers: Headers? = null,
    val resourceForSend: RequestType? = null,
    val requestEncoder: () -> Encoder<RequestType> = { TODO() },
    val responseDecoder: () -> Decoder<ResponseType> = { TODO() }
) {
    private lateinit var apiClient: ApiClient

    fun digInto(jsonPath: String) =
        Operation(method, path, queryParameters, headers, resourceForSend, requestEncoder, { responseDecoder().digInto(jsonPath) })
            .also { if (::apiClient.isInitialized) it.apiClient = apiClient }

    fun with(client: ApiClient) = apply { apiClient = client }

    suspend fun awaitWithHeaders(): Result<Pair<ResponseType, Headers>> = withContext(IO) { apiClient.request(this@Operation) }
    suspend fun await(): Result<ResponseType> = awaitWithHeaders().map { it.first }

    fun async() = GlobalScope.async(IO) { await() }
}

abstract class ShuResource<ResourceType : Any> {
    private val path: String
    private var customHeaders: Headers?
    private val resourceDekoder: () -> Decoder<ResourceType>
    private val resourceEnkoder: () -> Encoder<ResourceType>

    private constructor(path: String, customHeaders: Headers?, resourceDekoder: Decoder<ResourceType>?, resourceEnkoder: Encoder<ResourceType>?) {
        this.path = path
        this.customHeaders = customHeaders
        this.resourceDekoder = { resourceDekoder ?: TODO() }
        this.resourceEnkoder = { resourceEnkoder ?: TODO() }
    }

    constructor(path: String, customHeaders: Headers? = null, resourceCoder: Coder<ResourceType>) : this(
        path,
        customHeaders,
        resourceCoder,
        resourceCoder
    )

    constructor(path: String, customHeaders: Headers? = null, resourceDecoder: Decoder<ResourceType>) : this(
        path,
        customHeaders,
        resourceDecoder,
        null
    )

    constructor(path: String, customHeaders: Headers? = null, resourceEncoder: Encoder<ResourceType>) : this(
        path,
        customHeaders,
        null,
        resourceEncoder
    )

    fun list(parameters: QueryParameters? = null): Operation<Nothing, List<ResourceType>> {
        return Operation(
            GET,
            path,
            queryParameters = parameters,
            headers = customHeaders,
            responseDecoder = { resourceDekoder().list })
    }

    fun create(resources: ResourceType) =
        Operation(
            POST,
            path,
            resourceForSend = resources,
            headers = customHeaders,
            requestEncoder = resourceEnkoder,
            responseDecoder = resourceDekoder
        )

    fun <RequestType : Any> process(command: RequestType? = null, koder: IKodable<RequestType>) =
        Operation(
            POST,
            path,
            resourceForSend = command,
            headers = customHeaders,
            requestEncoder = { koder.encoder },
            responseDecoder = resourceDekoder
        )

    fun read(resourceId: String? = null, parameters: QueryParameters? = null) =
        Operation<Nothing, ResourceType>(
            GET,
            listOfNotNull(path, resourceId).joinToString("/"),
            queryParameters = parameters,
            headers = customHeaders,
            responseDecoder = resourceDekoder
        )

    fun update(resourceId: String? = null, resources: ResourceType) =
        Operation(
            PUT,
            listOfNotNull(path, resourceId).joinToString("/"),
            resourceForSend = resources,
            headers = customHeaders,
            requestEncoder = resourceEnkoder,
            responseDecoder = resourceDekoder
        )

    fun delete(resourceId: String) =
        Operation<Nothing, Unit>(
            DELETE,
            listOfNotNull(path, resourceId).joinToString("/"),
            headers = customHeaders,
            responseDecoder = { UnitKodable.decoder })
}

private object UnitKodable : IKodable<Unit> {
    override fun readValue(reader: JSONReader) = Unit
}

//class ShuResourceProxy<ResourceType: Any>(
//
//fun <ResourceType: Any> ApiClient.list(resource: ShuResource<ResourceType>, parameters: QueryParameters? = null) = resource.list(parameters).with(this)
//fun <ResourceType: Any> ApiClient.create(resource: ShuResource<ResourceType>, resources: ResourceType), parameters: QueryParameters? = null) = resource.list(parameters).with(this)

