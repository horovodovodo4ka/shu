package pro.horovodovodo4ka.shu.extension

fun Any.uriQuery(): Map<String, String> {
    return uriQuery(null).toMap()
}

fun Any.uriQueryString() : String = uriQuery().toList().joinToString("&") { "${it.first}=${it.second}" }

private fun Any.uriQuery(prefix: String?): List<Pair<String, String>> {
    return when (this) {
        is Map<*, *> -> this.uriQuery(prefix)
        is Array<*> -> this.asIterable().uriQuery(prefix)
        is Iterable<*> -> this.uriQuery(prefix)

        else -> listOf((prefix ?: toString()) to toString())
    }
}

private fun Map<*, *>.uriQuery(prefix: String?): List<Pair<String, String>> {
    return flatMap {
        var key = it.key?.toString() ?: return@flatMap emptyList<Pair<String, String>>()
        val value = it.value ?: return@flatMap emptyList<Pair<String, String>>()
        prefix?.also { key = "$prefix[$key]" }
        value.uriQuery(key).toList() ?: emptyList()
    }
}

private fun Iterable<*>.uriQuery(prefix: String?): List<Pair<String, String>> {
    return filterNotNull().flatMap { value ->
        var key = ""
        prefix?.also { key = "$prefix[]" }
        value.uriQuery(key).toList() ?: emptyList()
    }
}