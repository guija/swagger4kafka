package io.github.stavshamir.swagger4kafka.services

import io.github.stavshamir.swagger4kafka.types.*
import junit.framework.TestCase.assertEquals
import lombok.Data
import lombok.NoArgsConstructor
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.io.File

@RunWith(SpringRunner::class)
@Import(AsyncApiDocServiceTestConfiguration::class)
@ContextConfiguration(classes = [AsyncApiDocService::class])
class AsyncApiDocServiceTest {

    @Autowired
    private lateinit var asyncApiService: AsyncApiDocService

    @Test
    fun `docsAsYaml should return the correct async api doc as yaml`() {
        val expected = File("src/test/resources/async-api-docs/full-doc.yaml").readText()
        assertEquals(expected, asyncApiService.docAsYaml)
    }

    @Data
    @NoArgsConstructor
    class Foo {
        var s: String? = null
        var b: Boolean? = null
    }

}

@TestConfiguration
open class AsyncApiDocServiceTestConfiguration {

    @Bean
    open fun userAsyncApiDoc(): AsyncApiDoc {
        val info = Info.builder()
                .title("Example Spec")
                .description("An example of the AsyncApi specification")
                .version("1.0.0")
                .build()

        return AsyncApiDoc.builder()
                .info(info)
                .servers(Server.kafkaBootstrapServers("kafka:9092"))
                .build()!!
    }

    @Bean
    open fun mockChannelsService(): ChannelsService {
        val modelsService = ModelsService()

        val modelName = modelsService.register(AsyncApiDocServiceTest.Foo::class.java)

        val message = Message.builder()
                .name(AsyncApiDocServiceTest.Foo::class.java.name)
                .title(AsyncApiDocServiceTest.Foo::class.java.simpleName)
                .payload(modelsService.definitions[modelName])
                .examples(listOf(modelsService.getExample(modelName)))
                .build()

        val subscribeOperation = Operation.builder()
                .message(message)
                .bindings(mapOf("kafka" to KafkaOperationBindings("example-group-id")))
                .build()

        return ChannelsService {
            mapOf("example-topic" to Channel.ofSubscribe(subscribeOperation))
        }
    }

}

