package pro.horovodovodo4ka.shu

import pro.horovodovodo4ka.shu.Result.Failure
import pro.horovodovodo4ka.shu.Result.Success

// Simple wrapper around kotlin.Result due restrictions of using it
sealed class Result<out V> {

    open operator fun component1(): V? = null
    open operator fun component2(): Throwable? = null

    class Success<V>(private val value: V) : Result<V>() {
        override fun component1(): V? = value

        override fun toString() = "[Success: $value]"

        override fun hashCode(): Int = value.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Success<*> && value == other.value
        }

        fun get() = value
    }

    class Failure(private val error: Throwable) : Result<Nothing>() {
        override fun component2(): Throwable? = error

        override fun toString() = "[Failure: $error]"

        override fun hashCode(): Int = error.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Failure && error == other.error
        }

        fun getException(): Throwable = error
    }

    companion object {
        fun <E : Throwable> error(exception: E) = Failure(exception)

        fun <V> success(value: V) = Success(value)

        inline fun <V : Any> of(value: V?, fail: (() -> Throwable) = { Throwable() }): Result<V> =
            value?.let { success(it) } ?: error(fail())

        inline fun <V> of(f: () -> V): Result<V> = try {
            success(f())
        } catch (ex: Throwable) {
            error(ex)
        }
    }
}

fun <V> Result<V>.getOrNull(): V? = when (this) {
    is Success -> this.get()
    is Failure -> null
}

fun Result<*>.exceptionOrNull(): Throwable? = when (this) {
    is Success -> null
    is Failure -> this.getException()
}

val Result<*>.isSuccess: Boolean
    get() = this is Success

val Result<*>.isFailure: Boolean
    get() = this is Failure

fun <V> Result<V>.getOrThrow(): V = when (this) {
    is Success -> this.get()
    is Failure -> throw this.getException()
}

infix fun <V> Result<V>.getOrDefault(defaultValue: V): V = getOrNull() ?: defaultValue

inline infix fun <V> Result<V>.getOrElse(onFailure: (exception: Throwable) -> V): V = when (this) {
    is Success -> this.get()
    is Failure -> onFailure(this.getException())
}

inline fun <X, V> Result<V>.fold(success: (V) -> X, failure: (Throwable) -> X): X = when (this) {
    is Success -> success(this.get())
    is Failure -> failure(this.getException())
}

inline fun <X, V> Result<V>.map(transform: (value: V) -> X): Result<X> = when (this) {
    is Success -> Success(transform(this.get()))
    is Failure -> this
}

inline fun <X, V> Result<V>.mapCatching(crossinline transform: (value: V) -> X): Result<X> = when (this) {
    is Success -> Result.of { transform(this.get()) }
    is Failure -> this
}

inline fun <V> Result<V>.recover(transform: (exception: Throwable) -> V): Result<V> = when (this) {
    is Success -> this
    is Failure -> Success(transform(this.getException()))
}

inline fun <V> Result<V>.onSuccess(action: (value: V) -> Unit): Result<V> {
    if (this is Success) action(this.get())
    return this
}

inline fun <V> Result<V>.onFailure(action: (exception: Throwable) -> Unit): Result<V> {
    if (this is Failure) action(this.getException())
    return this
}
