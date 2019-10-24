package pro.horovodovodo4ka.shu.resource.operations

import com.github.fluidsonic.fluid.json.JSONReader
import com.github.kittinunf.fuel.core.Method.DELETE
import pro.horovodovodo4ka.kodable.core.IKodable
import pro.horovodovodo4ka.shu.kodable.decoder
import pro.horovodovodo4ka.shu.resource.ShuOperation
import pro.horovodovodo4ka.shu.resource.ShuWOResource

interface Deletable

private object UnitKodable : IKodable<Unit> {
    override fun readValue(reader: JSONReader) = Unit
}

fun <Resource> Resource.deferredDelete(resourceId: String)
        where Resource : ShuWOResource<Unit>, Resource : Deletable =
    ShuOperation<Nothing, Unit>(
        origin,
        DELETE,
        listOfNotNull(path, resourceId).joinToString("/"),
        headers = customHeaders,
        responseDecoder = { UnitKodable.decoder })

// Shorthand
suspend fun <Resource> Resource.delete(resourceId: String)
        where Resource : ShuWOResource<Unit>, Resource : Deletable = deferredDelete(resourceId).run(Throwable().stackTrace.getOrNull(2))