package pro.horovodovodo4ka.shu.resource

import com.github.kittinunf.fuel.core.Method.POST
import pro.horovodovodo4ka.shu.coders.Encoder
import pro.horovodovodo4ka.shu.map

interface Processable

fun <Resource, RequestType : Any, ResourceType : Any> Resource.deferredProcess(command: RequestType? = null, encoder: Encoder<RequestType>)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Processable =
    ShuOperation(
        origin,
        POST,
        path,
        resourceForSend = command,
        headers = customHeaders,
        requestEncoder = { encoder },
        responseDecoder = resourceDekoder
    )

// Shorthand
suspend fun <Resource, RequestType : Any, ResourceType : Any> Resource.process(command: RequestType? = null, encoder: Encoder<RequestType>)
        where Resource : ShuOperationProvider<ResourceType>, Resource : Processable = deferredProcess(command, encoder).run().map { it.data }