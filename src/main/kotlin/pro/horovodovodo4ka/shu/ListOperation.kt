package pro.horovodovodo4ka.shu

import com.github.kittinunf.fuel.core.Method.GET
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuOperationProvider

interface ListOperation

fun <Resource, ResourceType : Any> Resource.list(parameters: QueryParameters? = null)
        where Resource : ShuOperationProvider<ResourceType>, Resource : ListOperation =
    ShuOperation<Nothing, List<ResourceType>>(
        origin,
        GET,
        path,
        queryParameters = parameters,
        headers = customHeaders,
        responseDecoder = { resourceDekoder().list })