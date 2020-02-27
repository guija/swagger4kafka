import io.github.stavshamir.swagger4kafka.example.dtos.ExamplePayloadDto
import junit.framework.TestCase.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.ClassRule
import org.junit.Test
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class ApiIntegrationTests {

    companion object {
        private val restTemplate = RestTemplate()

        private const val APP_NAME = "app_1"
        private const val APP_PORT = 8080

        @JvmField
        @ClassRule
        val environment: DockerComposeContainer<Nothing> = DockerComposeContainer<Nothing>(File("docker-compose.yml"))
                .withExposedService(APP_NAME, APP_PORT)
    }

    private fun baseUrl(): String {
        val host = environment.getServiceHost(APP_NAME, APP_PORT)
        val port = environment.getServicePort(APP_NAME, APP_PORT)
        return "http://$host:$port"
    }

    data class Info(val bootstrapServers: String, val serviceName: String)

    @Test
    fun `info endpoint should return the correct response`() {
        val infoEndpoint = "/kafka-api/info"

        val actual = restTemplate.getForObject(baseUrl() + infoEndpoint, Info::class.java)
        val expected = Info(bootstrapServers = "localhost:9092", serviceName = "swagger4kafka Example Project")

        assertEquals(expected, actual)
    }

    data class KafkaEndpoint(val topic: String, val payloadClassName: String, val payloadModelName: String, val payloadExample: Map<String, Any>)

    @Test
    fun `endpoints endpoint should return the correct response`() {
        val endpointsEndpoint = "/kafka-api/endpoints"

        val actual = restTemplate.getForObject(baseUrl() + endpointsEndpoint, Array<KafkaEndpoint>::class.java)?.toList()
        val expected = listOf(
                KafkaEndpoint(
                        topic = "example-topic",
                        payloadClassName = ExamplePayloadDto::class.java.name,
                        payloadModelName = "ExamplePayloadDto",
                        payloadExample = mapOf("someString" to "string", "someLong" to 0, "someEnum" to "FOO1")
                )
        )

        assertEquals(expected, actual)
    }

    data class Payload(val className: String, val `object`: Map<String, Any>)
    data class ValidationMessage(val valid: Boolean, val message: String)

    @Test
    fun `validate should return a non-valid response for a non-valid payload`() {
        val validateEndpoint = "/kafka-api/validate"

        val payload = Payload(className = ExamplePayloadDto::class.java.name, `object` = mapOf("wrongFieldName" to "string"))
        val actual = restTemplate.postForObject(baseUrl() + validateEndpoint, payload, ValidationMessage::class.java)

        assertThat(actual).isNotNull()
        assertThat(actual?.valid).isFalse()
        assertThat(actual?.message).isNotEmpty()
    }

    @Test
    fun `validate should return a valid response for valid payloads`() {
        val validateEndpoint = "/kafka-api/validate"

        val payload = Payload(
                className = ExamplePayloadDto::class.java.name,
                `object` = mapOf("someString" to "string", "someLong" to 0, "someEnum" to "FOO1")
        )
        val actual = restTemplate.postForObject(baseUrl() + validateEndpoint, payload, ValidationMessage::class.java)

        assertThat(actual).isNotNull()
        assertThat(actual?.valid).isTrue()
        assertThat(actual?.message).isEmpty()
    }

}