import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.junit.jupiter.api.Test

class LensPartyTest {

    @Test
    fun `part 1`() {
        val lens: Extract<Request, String?> = { r -> r.header("foobar") }
        assertThat(lens(Request(GET, "")), absent())
    }

    @Test
    fun `part 2`() {
        fun optional(name: String) = { r: Request -> r.header(name) }

        val lens: Extract<Request, String?> = optional("foobar")
        assertThat(lens(Request(GET, "")), absent())
    }

    object Part3 {
        object AHeader {
            fun optional(name: String) = { r: Request -> r.header(name) }
        }
    }

    object Part4 {
        object AHeader {
            fun optional(name: String) = { r: Request -> r.header(name) }
            fun required(name: String) = { r: Request -> r.header(name) ?: throw ExtractFailed(name) }
        }
    }

    object Part5 {

        object AHeader {
            fun optional(name: String) = { r: Request -> r.header(name) }
            fun required(name: String) = { r: Request -> r.header(name) ?: throw ExtractFailed(name) }
        }

        object Query {
            fun optional(name: String) = { r: Request -> r.query(name) }
            fun required(name: String) = { r: Request -> r.query(name) ?: throw ExtractFailed(name) }
        }
    }

    object Part6 {
        open class Builder(private val get: (String, Request) -> String?) {
            fun optional(name: String) = { r: Request -> get(name, r) }
            fun required(name: String) = { r: Request -> get(name, r) ?: throw ExtractFailed(name) }
        }

        object AHeader : Builder({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder({ name: String, r: Request -> r.query(name) })
    }

    object Part7 {
        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String) = { r: ENTITY -> get(name, r) }
            fun required(name: String) = { r: ENTITY -> get(name, r) ?: throw ExtractFailed(name) }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })
    }

    object Part8 {
        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String): (ENTITY) -> PART? = { r: ENTITY ->
                try {
                    get(name, r)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun required(name: String): (ENTITY) -> PART = { r: ENTITY ->
                try {
                    get(name, r) ?: throw ExtractFailed(name)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })
    }

    object Part9 {
        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String): (ENTITY) -> PART? = { r: ENTITY ->
                try {
                    get(name, r)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun required(name: String): (ENTITY) -> PART = { r: ENTITY ->
                try {
                    get(name, r) ?: throw ExtractFailed(name)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART): Builder<ENTITY, NEXT_PART> =
                Builder { name, input -> get(name, input)?.let(nextGet) }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })

    }

    object Part10 {
        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String): (ENTITY) -> PART? = { r: ENTITY ->
                try {
                    get(name, r)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun required(name: String): (ENTITY) -> PART = { r: ENTITY ->
                try {
                    get(name, r) ?: throw ExtractFailed(name)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART): Builder<ENTITY, NEXT_PART> =
                Builder { name, input -> get(name, input)?.let(nextGet) }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })

        fun <T> Builder<T, String>.int() = map(String::toInt)
    }

    object Part11 {
        interface Extract<ENTITY, PART> {
            operator fun invoke(r: ENTITY): PART
        }

        interface Inject<PART, ENTITY> {
            operator fun invoke(p: PART, r: ENTITY): ENTITY
        }

        interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>

        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String): (ENTITY) -> PART? = { r: ENTITY ->
                try {
                    get(name, r)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun required(name: String): (ENTITY) -> PART = { r: ENTITY ->
                try {
                    get(name, r) ?: throw ExtractFailed(name)
                } catch (e: Exception) {
                    throw ExtractFailed(name)
                }
            }

            fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART): Builder<ENTITY, NEXT_PART> =
                Builder { name, input -> get(name, input)?.let(nextGet) }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })

        fun <T> Builder<T, String>.int() = map(String::toInt)
    }

    object Part12 {
        interface Extract<ENTITY, PART> {
            operator fun invoke(r: ENTITY): PART
        }

        interface Inject<PART, ENTITY> {
            operator fun invoke(p: PART, r: ENTITY): ENTITY
        }

        interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>

        open class Builder<ENTITY, PART>(private val get: (String, ENTITY) -> PART?) {
            fun optional(name: String) = object : BiDi<ENTITY, PART?> {
                override fun invoke(r: ENTITY): PART? =
                    try {
                        get(name, r)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART?, r: ENTITY): ENTITY {
                    TODO("Not yet implemented")
                }
            }

            fun required(name: String) = object : BiDi<ENTITY, PART> {
                override fun invoke(r: ENTITY): PART =
                    try {
                        get(name, r) ?: throw ExtractFailed(name)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART, r: ENTITY): ENTITY {
                    TODO("Not yet implemented")
                }
            }

            fun <NEXT_PART> map(nextGet: (PART) -> NEXT_PART): Builder<ENTITY, NEXT_PART> =
                Builder { name, input -> get(name, input)?.let(nextGet) }
        }

        object AHeader : Builder<Request, String>({ name: String, r: Request -> r.header(name) })
        object AQuery : Builder<Request, String>({ name: String, r: Request -> r.query(name) })

        fun <T> Builder<T, String>.int() = map(String::toInt)
    }

    object Part13 {
        interface Extract<ENTITY, PART> {
            operator fun invoke(r: ENTITY): PART
        }

        interface Inject<PART, ENTITY> {
            operator fun invoke(p: PART, r: ENTITY): ENTITY
        }

        interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>

        open class Builder<ENTITY, PART>(
            private val get: (String, ENTITY) -> PART?,
            private val set: (String, ENTITY, PART) -> ENTITY) {

            fun optional(name: String) = object : BiDi<ENTITY, PART?> {
                override fun invoke(r: ENTITY): PART? =
                    try {
                        get(name, r)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART?, r: ENTITY) = p?.let { set(name, r, p) } ?: r
            }

            fun required(name: String) = object : BiDi<ENTITY, PART> {
                override fun invoke(r: ENTITY): PART =
                    try {
                        get(name, r) ?: throw ExtractFailed(name)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART, r: ENTITY) = set(name, r, p)
            }

            fun <NEXT_PART> map(
                nextGet: (PART) -> NEXT_PART,
                nextSet: (NEXT_PART) -> PART,
            ): Builder<ENTITY, NEXT_PART> =
                Builder({ name, input -> get(name, input)?.let(nextGet) },
                    { name, e, it -> set(name, e, nextSet(it)) })
        }

        object AHeader : Builder<Request, String>(
            { name: String, r: Request -> r.header(name) },
            { name, r, value -> r.header(name, value) }
        )

        object AQuery : Builder<Request, String>(
            { name: String, r: Request -> r.query(name) },
            { name, r, value -> r.query(name, value) }
        )

        fun <T> Builder<T, String>.int() = map(String::toInt, Int::toString)
    }

    object Part14 {
        interface Extract<ENTITY, PART> {
            operator fun invoke(r: ENTITY): PART
        }

        interface Inject<PART, ENTITY> {
            operator fun invoke(p: PART, r: ENTITY): ENTITY
        }

        interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>

        open class Builder<ENTITY, PART>(
            private val get: (String, ENTITY) -> List<PART>?,
            private val set: (String, ENTITY, PART) -> ENTITY) {

            fun optional(name: String) = object : BiDi<ENTITY, PART?> {
                override fun invoke(r: ENTITY): PART? =
                    try {
                        get(name, r)?.firstOrNull()
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART?, r: ENTITY) = p?.let { set(name, r, p) } ?: r
            }

            fun required(name: String) = object : BiDi<ENTITY, PART> {
                override fun invoke(r: ENTITY): PART =
                    try {
                        get(name, r)?.firstOrNull() ?: throw ExtractFailed(name)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART, r: ENTITY) = set(name, r, p)
            }

            fun <NEXT_PART> map(
                nextGet: (PART) -> NEXT_PART,
                nextSet: (NEXT_PART) -> PART,
            ): Builder<ENTITY, NEXT_PART> =
                Builder(
                    { name, input -> get(name, input)?.map(nextGet) },
                    { name, e, it -> set(name, e, nextSet(it)) })
        }

        object AHeader : Builder<Request, String>(
            { name: String, r: Request -> r.header(name)?.let { listOf(it) } },
            { name, r, value -> r.header(name, value) }
        )

        object AQuery : Builder<Request, String>(
            { name: String, r: Request -> r.query(name)?.let { listOf(it) } },
            { name, r, value -> r.query(name, value) }
        )

        fun <T> Builder<T, String>.int() = map(String::toInt, Int::toString)
    }

    object Part15 {
        interface Extract<ENTITY, PART> {
            operator fun invoke(r: ENTITY): PART
        }

        interface Inject<PART, ENTITY> {
            operator fun invoke(p: PART, r: ENTITY): ENTITY
        }

        interface BiDi<ENTITY, PART> : Extract<ENTITY, PART>, Inject<PART, ENTITY>

        open class Builder<ENTITY, PART>(
            private val get: (String, ENTITY) -> List<PART>?,
            private val set: (String, ENTITY, PART) -> ENTITY) {

            fun optional(name: String) = object : BiDi<ENTITY, PART?> {
                override fun invoke(r: ENTITY): PART? =
                    try {
                        get(name, r)?.firstOrNull()
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART?, r: ENTITY) = p?.let { set(name, r, p) } ?: r
            }

            fun required(name: String) = object : BiDi<ENTITY, PART> {
                override fun invoke(r: ENTITY): PART =
                    try {
                        get(name, r)?.firstOrNull() ?: throw ExtractFailed(name)
                    } catch (e: Exception) {
                        throw ExtractFailed(name)
                    }

                override fun invoke(p: PART, r: ENTITY) = set(name, r, p)
            }

            fun <NEXT_PART> map(
                nextGet: (PART) -> NEXT_PART,
                nextSet: (NEXT_PART) -> PART,
            ): Builder<ENTITY, NEXT_PART> =
                Builder(
                    { name, input -> get(name, input)?.map(nextGet) },
                    { name, e, it -> set(name, e, nextSet(it)) })

            interface MultiBuilder<ENTITY, PART> {
                fun optional(name: String): BiDi<ENTITY, PART?>
                fun required(name: String): BiDi<ENTITY, PART>
            }

            val multi = object : MultiBuilder<ENTITY, List<PART>> {
                override fun optional(name: String) = object : BiDi<ENTITY, List<PART>?> {
                    override fun invoke(r: ENTITY): List<PART>? =
                        try {
                            get(name, r)
                        } catch (e: Exception) {
                            throw ExtractFailed(name)
                        }

                    override fun invoke(p: List<PART>?, r: ENTITY) = p?.fold(r) {
                            acc, next -> set(name, acc, next)
                    } ?: r
                }

                override fun required(name: String) = object : BiDi<ENTITY, List<PART>> {
                    override fun invoke(r: ENTITY): List<PART> =
                        try {
                            get(name, r) ?: throw ExtractFailed(name)
                        } catch (e: Exception) {
                            throw ExtractFailed(name)
                        }

                    override fun invoke(p: List<PART>, r: ENTITY) = p.fold(r) {
                            acc, next -> set(name, acc, next)
                    }
                }
            }
        }

        object AHeader : Builder<Request, String>(
            { name: String, r: Request -> r.header(name)?.let { listOf(it) } },
            { name, r, value -> r.header(name, value) }
        )

        object AQuery : Builder<Request, String>(
            { name: String, r: Request -> r.query(name)?.let { listOf(it) } },
            { name, r, value -> r.query(name, value) }
        )

        fun <T> Builder<T, String>.int() = map(String::toInt, Int::toString)
    }
}
