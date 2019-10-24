@file:JvmName("RpcKt")

package pro.horovodovodo4ka.shu.resource.operations

import com.github.kittinunf.fuel.core.Method.POST
import pro.horovodovodo4ka.shu.map
import pro.horovodovodo4ka.shu.resource.Rpc
import pro.horovodovodo4ka.shu.resource.ShuOperation

fun <Resource, RequestType : Any, ResponseType : Any> Resource.deferredCall(command: RequestType)
        where Resource : Rpc<RequestType, ResponseType> =
    ShuOperation(
        origin,
        POST,
        path,
        resourceForSend = command,
        headers = customHeaders,
        requestEncoder = requestEncoder,
        responseDecoder = requestDecoder
    )

// Shorthand
suspend fun <Resource, RequestType : Any, ResponseType : Any> Resource.call(command: RequestType)
        where Resource : Rpc<RequestType, ResponseType> = deferredCall(command).run()