package pro.horovodovodo4ka.shu.resource.operations

import com.github.kittinunf.fuel.core.Method.PUT
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuRWResource

interface Updatable

fun <Resource, ResourceType : Any> Resource.deferredUpdate(resourceId: String? = null, resource: ResourceType)
        where Resource : ShuRWResource<ResourceType>, Resource : Updatable =
    ShuOperation(
        origin,
        PUT,
        listOfNotNull(path, resourceId).joinToString("/"),
        resourceForSend = resource,
        headers = customHeaders,
        requestEncoder = resourceEncoder,
        responseDecoder = resourceDecoder
    )

// Shorthand
suspend fun <Resource, ResourceType : Any> Resource.update(resourceId: String? = null, resource: ResourceType)
        where Resource : ShuRWResource<ResourceType>, Resource : Updatable = deferredUpdate(resourceId, resource).run()