package pro.horovodovodo4ka.shu.resource.operations

import com.github.kittinunf.fuel.core.Method.POST
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuRWResource

interface Creatable

fun <Resource, ResourceType : Any> Resource.deferredCreate(resource: ResourceType)
        where Resource : ShuRWResource<ResourceType>, Resource : Creatable =
    ShuOperation(
        origin,
        POST,
        path,
        resourceForSend = resource,
        headers = customHeaders,
        requestEncoder = resourceEncoder,
        responseDecoder = resourceDecoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.create(resource: ResourceType)
        where Resource : ShuRWResource<ResourceType>, Resource : Creatable = deferredCreate(resource).run(Throwable().stackTrace.getOrNull(2))