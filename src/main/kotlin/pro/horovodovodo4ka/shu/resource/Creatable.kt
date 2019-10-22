package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method.POST
import pro.horovodovodo4ka.shu.map

interface Creatable

fun <Resource, ResourceType : Any> Resource.deferredCreate(resource: ResourceType)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Creatable =
    ShuOperation(
        origin,
        POST,
        path,
        resourceForSend = resource,
        headers = customHeaders,
        requestEncoder = resourceEnkoder,
        responseDecoder = resourceDekoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.create(resource: ResourceType)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Creatable = deferredCreate(resource).run().map { it.data }