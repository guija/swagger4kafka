package io.github.stavshamir.swagger4kafka.services

import io.github.stavshamir.swagger4kafka.configuration.KafkaProtocolConfiguration
import io.github.stavshamir.swagger4kafka.types.AsyncApiDoc
import io.github.stavshamir.swagger4kafka.types.Channel
import io.github.stavshamir.swagger4kafka.types.Message
import io.github.stavshamir.swagger4kafka.types.Operation
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


private const val TOPIC = "example-topic"
private const val ANOTHER_TOPIC = "another-topic"

class DefaultChannelsServiceTest {

    @Mock
    private lateinit var kafkaListenersScanner: KafkaListenersScanner

    @Test
    fun `given kafka is not enabled, ChannelService should not return a kafka channel`() {
        val docket = Docket.builder().asyncApiDoc(AsyncApiDoc()).build();

        val kafkaChannels = mapOf(TOPIC to Channel.ofSubscribe(buildOperation()))
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(kafkaListenersScanner.getChannels(KafkaConsumerClass::class.java))
                .thenReturn(kafkaChannels)

        val channelService = DefaultChannelsService(docket, kafkaListenersScanner)
        channelService.postConstruct()

        assertEquals(mapOf<String, Channel>(), channelService.channels)
    }

    @Test
    fun `given kafka is enabled, ChannelService should return a kafka channel`() {
        val kafkaProtocolConfiguration = KafkaProtocolConfiguration.builder()
                .basePackage(KafkaConsumerClass::class.java.`package`.name)
                .build();

        val docket = Docket.builder()
                .asyncApiDoc(AsyncApiDoc())
                .kafka(kafkaProtocolConfiguration)
                .build();

        val expectedChannels = mapOf(TOPIC to Channel.ofSubscribe(buildOperation()))

        MockitoAnnotations.initMocks(this)
        Mockito.`when`(kafkaListenersScanner.getChannels(KafkaConsumerClass::class.java))
                .thenReturn(expectedChannels)

        val channelService = DefaultChannelsService(docket, kafkaListenersScanner)
        channelService.postConstruct()

        assertEquals(expectedChannels, channelService.channels)
    }

    @Test
    fun `given kafka is enabled, ChannelService should return all kafka channels`() {
        val kafkaProtocolConfiguration = KafkaProtocolConfiguration.builder()
                .basePackage(KafkaConsumerClass::class.java.`package`.name)
                .build();

        val docket = Docket.builder()
                .asyncApiDoc(AsyncApiDoc())
                .kafka(kafkaProtocolConfiguration)
                .build();

        MockitoAnnotations.initMocks(this)

        Mockito.`when`(kafkaListenersScanner.getChannels(KafkaConsumerClass::class.java))
                .thenReturn(mapOf(TOPIC to Channel.ofSubscribe(buildOperation())))

        Mockito.`when`(kafkaListenersScanner.getChannels(AnotherKafkaConsumerClass::class.java))
                .thenReturn(mapOf(ANOTHER_TOPIC to Channel.ofSubscribe(buildOperation())))

        val channelService = DefaultChannelsService(docket, kafkaListenersScanner)
        channelService.postConstruct()

        val expectedChannels = mapOf(
                TOPIC to Channel.ofSubscribe(buildOperation()),
                ANOTHER_TOPIC to Channel.ofSubscribe(buildOperation())
        )

        assertEquals(expectedChannels, channelService.channels)
    }

    private fun buildOperation(): Operation? {
        val modelsService = ModelsService()

        val modelName = modelsService.register(Foo::class.java)

        val message = Message.builder()
                .name(Foo::class.java.name)
                .title(Foo::class.java.simpleName)
                .payload(modelsService.definitions[modelName])
                .examples(listOf(modelsService.getExample(modelName)))
                .build()

        return Operation.builder()
                .message(message)
                .bindings(mapOf("kafka" to null))
                .build()
    }
}

@Component
class KafkaConsumerClass {

    @KafkaListener(topics = [TOPIC])
    private fun listenerMethod(payload: Foo) {
    }

}

@Component
class AnotherKafkaConsumerClass {

    @KafkaListener(topics = [ANOTHER_TOPIC])
    private fun listenerMethod(payload: Foo) {
    }

}

data class Foo(val s: String, val b: Boolean)