package pro.horovodovodo4ka.shu.resource.operations

import com.github.kittinunf.fuel.core.Method.GET
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuROResource

interface Readable

fun <Resource, ResourceType : Any> Resource.deferredRead(resourceId: String? = null, parameters: QueryParameters? = null)
        where Resource : ShuROResource<ResourceType>, Resource : Readable =
    ShuOperation<Nothing, ResourceType>(
        origin,
        GET,
        listOfNotNull(path, resourceId).joinToString("/"),
        queryParameters = parameters,
        headers = customHeaders,
        responseDecoder = resourceDecoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.read(resourceId: String? = null, parameters: QueryParameters? = null)
        where Resource : ShuROResource<ResourceType>, Resource : Readable = deferredRead(resourceId, parameters).run()