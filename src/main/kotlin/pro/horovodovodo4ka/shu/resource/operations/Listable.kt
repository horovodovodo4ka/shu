package pro.horovodovodo4ka.shu.resource.operations

import com.github.kittinunf.fuel.core.Method.GET
import pro.horovodovodo4ka.shu.QueryParameters
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuROResource

interface Listable

fun <Resource, ResourceType : Any> Resource.listDeferred(parameters: QueryParameters? = null)
        where Resource : ShuROResource<ResourceType>, Resource : Listable =
    ShuOperation<Nothing, List<ResourceType>>(
        origin,
        GET,
        path,
        queryParameters = parameters,
        headers = customHeaders,
        responseDecoder = { resourceDecoder().list })

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.list(parameters: QueryParameters? = null)
        where Resource : ShuROResource<ResourceType>, Resource : Listable = listDeferred(parameters).run(Throwable().stackTrace.getOrNull(2))