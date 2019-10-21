package pro.horovodovodo4ka.shu

import pro.horovodovodo4ka.shu.Result.Failure
import pro.horovodovodo4ka.shu.Result.Success

// Simple wrapper around kotlin.Result due restrictions of using it
sealed class Result<out V, out E : Throwable> {

    open operator fun component1(): V? = null
    open operator fun component2(): E? = null

    class Success<V>(private val value: V) : Result<V, Nothing>() {
        override fun component1(): V? = value

        override fun toString() = "[Success: $value]"

        override fun hashCode(): Int = value.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Success<*> && value == other.value
        }

        fun get() = value
    }

    class Failure<E : Throwable>(private val error: E) : Result<Nothing, E>() {
        override fun component2(): E? = error

        override fun toString() = "[Failure: $error]"

        override fun hashCode(): Int = error.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Failure<*> && error == other.error
        }

        fun getException(): E = error
    }

    companion object {
        // Factory methods
        fun <E : Throwable> error(exception: E) = Failure(exception)

        fun <V : Any> success(value: V) = Success(value)

        fun <V : Any> of(value: V?, fail: (() -> Throwable) = { Throwable() }): Result<V, Throwable> =
            value?.let { success(it) } ?: error(fail())

        fun <V : Any, E : Throwable> of(f: () -> V): Result<V, E> = try {
            success(f())
        } catch (ex: Throwable) {
            @Suppress("UNCHECKED_CAST")
            error(ex as E)
        }
    }
}

fun <V> Result<V, *>.getOrNull(): V? = when (this) {
    is Success -> this.get()
    is Failure -> null
}

fun <E : Throwable> Result<*, E>.exceptionOrNull(): E? = when (this) {
    is Success -> null
    is Failure -> this.getException()
}

val Result<*, *>.isSuccess: Boolean get() = this is Success
val Result<*, *>.isFailure: Boolean get() = this is Failure

fun <V, E : Throwable> Result<V, E>.getOrThrow(): V = when (this) {
    is Success -> this.get()
    is Failure -> throw this.getException()
}

infix fun <V> Result<V, *>.getOrDefault(defaultValue: V): V = getOrNull() ?: defaultValue

inline infix fun <V, E : Throwable> Result<V, E>.getOrElse(onFailure: (exception: E) -> V): V = when (this) {
    is Success -> this.get()
    is Failure -> onFailure(this.getException())
}

inline fun <X, V, E : Throwable> Result<V, E>.fold(success: (V) -> X, failure: (E) -> X): X = when (this) {
    is Success -> success(this.get())
    is Failure -> failure(this.getException())
}

inline fun <V, E : Throwable> Result<V, E>.map(transform: (value: V) -> V): Result<V, E> = when (this) {
    is Success -> Success(transform(this.get()))
    is Failure -> this
}

inline fun <V, E : Throwable> Result<V, E>.recover(transform: (exception: E) -> V): Result<V, E> = when(this) {
    is Success -> this
    is Failure -> Success(transform(this.getException()))
}

inline fun <V, E : Throwable> Result<V, E>.onSuccess(action: (value: V) -> Unit): Result<V, E> {
    if(this is Success) action(this.get())
    return this
}

inline fun <V, E : Throwable> Result<V, E>.onFailure(action: (exception: E) -> Unit): Result<V, E> {
    if(this is Failure) action(this.getException())
    return this
}
