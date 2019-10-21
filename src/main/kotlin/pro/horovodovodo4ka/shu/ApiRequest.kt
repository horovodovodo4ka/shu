package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.Method.DELETE
import com.github.kittinunf.fuel.core.Method.GET
import com.github.kittinunf.fuel.core.Method.POST
import com.github.kittinunf.fuel.core.Method.PUT

abstract class ApiRequest<ResponseType : Any, RequestType : Any>(
    private val path: String,
    private var customHeaders: Headers? = null,
    private val apiClient: ApiClient
) : Decoder<ResponseType>, Encoder<RequestType> {
    suspend fun listWithMeta(parameters: QueryParameters? = null): ResourceResponseWithHeaders<List<ResponseType>> {
        return apiClient.requestCollection(path, query = parameters, customHeader = customHeaders, responseDecoder = this)
    }

    suspend fun list(parameters: QueryParameters? = null): Result<List<ResponseType>> = listWithMeta(parameters).mapCatching { it.first }

    suspend fun create(resources: RequestType): Result<ResponseType> {
        return apiClient.requestResource(path = path, resource = resources, method = POST, customHeader = customHeaders, requestEncoder = this, responseDecoder = this)
            .mapCatching { it.first ?: throw Error("Empty result") }

    }

    suspend fun process(command: RequestType? = null): Result<ResponseType> {
        return (apiClient.requestResource(path = path, resource = command, method = POST, customHeader = customHeaders, requestEncoder = this, responseDecoder = this))
            .mapCatching { it.first ?: throw Error("Empty result") }

    }

    suspend fun read(resourceId: String? = null, parameters: QueryParameters? = null): Result<ResponseType> {
        var path = this.path
        resourceId?.also { path += "/$resourceId" }
        return (apiClient.requestResource(path = path, query = parameters, method = GET, customHeader = customHeaders, requestEncoder = this, responseDecoder = this))
            .mapCatching { it.first ?: throw Error("Empty result") }

    }


    suspend fun update(resourceId: String? = null, resources: RequestType): Result<ResponseType> {
        val path = resourceId?.let { "$path/$resourceId" } ?: path
        return (apiClient.requestResource(path = path, resource = resources, method = PUT, customHeader = customHeaders, requestEncoder = this, responseDecoder = this))
            .mapCatching { it.first ?: throw Error("Empty result") }

    }

    suspend fun delete(resourceId: String): Result<ResponseType> {
        return (apiClient.requestResource(path = "$path/$resourceId", method = DELETE, customHeader = customHeaders, requestEncoder = this, responseDecoder = this))
            .mapCatching { it.first ?: throw Error("Empty result") }

    }
}


