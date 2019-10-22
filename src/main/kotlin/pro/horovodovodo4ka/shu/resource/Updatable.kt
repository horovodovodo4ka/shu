package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method.PUT
import pro.horovodovodo4ka.shu.map

interface Updatable

fun <Resource, ResourceType : Any> Resource.deferredUpdate(resourceId: String? = null, resource: ResourceType)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Updatable =
    ShuOperation(
        origin,
        PUT,
        listOfNotNull(path, resourceId).joinToString("/"),
        resourceForSend = resource,
        headers = customHeaders,
        requestEncoder = resourceEnkoder,
        responseDecoder = resourceDekoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.update(resourceId: String? = null, resource: ResourceType)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Updatable = deferredUpdate(resourceId, resource).run().map { it.data }