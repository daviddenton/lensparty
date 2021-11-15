import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.junit.jupiter.api.Test

class LensPartyTest {

    @Test
    fun `part 1`() {
        val lens: Extract<Request, String?> = TODO()
        assertThat(lens(Request(GET, "")), absent())
    }
}
