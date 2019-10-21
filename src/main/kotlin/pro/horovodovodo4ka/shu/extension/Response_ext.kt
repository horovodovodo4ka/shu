package pro.horovodovodo4ka.shu.extension

import com.github.kittinunf.fuel.core.Response

val Response.headersMap: Map<String, String>
    get() {
        @Suppress("UNCHECKED_CAST")
        val rawHeaders = this.headers as Map<String?, List<String>>
        return rawHeaders
            .filterKeys { it != null }
            .mapKeys { it.key!! }
            .mapValues { it.value.joinToString(";") }
    }