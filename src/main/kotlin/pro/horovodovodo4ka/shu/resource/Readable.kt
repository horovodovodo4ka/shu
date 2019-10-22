package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method.GET
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.map

interface Readable

fun <Resource, ResourceType : Any> Resource.deferredRead(resourceId: String? = null, parameters: QueryParameters? = null)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Readable =
    ShuOperation<Nothing, ResourceType>(
        origin,
        GET,
        listOfNotNull(path, resourceId).joinToString("/"),
        queryParameters = parameters,
        headers = customHeaders,
        responseDecoder = resourceDekoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.read(resourceId: String? = null, parameters: QueryParameters? = null)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Readable = deferredRead(resourceId, parameters).run().map { it.data }