package io.github.stavshamir.swagger4kafka.services

import io.github.stavshamir.swagger4kafka.types.Channel
import io.github.stavshamir.swagger4kafka.types.KafkaOperationBindings
import io.github.stavshamir.swagger4kafka.types.Message
import io.github.stavshamir.swagger4kafka.types.Operation
import junit.framework.TestCase.assertEquals
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

private const val TOPIC = "test-topic"

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [KafkaListenersScanner::class, ModelsService::class])
@TestPropertySource(properties = ["kafka.topics.test=$TOPIC"])
class KafkaListenersScannerTest {

    @Autowired
    private lateinit var kafkaListenersScanner: KafkaListenersScanner

    private val modelsService = ModelsService()

    @Value("\${kafka.topics.test}")
    private val topicFromProperties: String? = null

    private fun buildOperation(type: Class<*>): Operation? {
        val modelName = modelsService.register(type)

        val message = Message.builder()
                .name(type.name)
                .title(type.simpleName)
                .payload(modelsService.definitions[modelName])
                .examples(listOf(modelsService.getExample(modelName)))
                .build()

        return Operation.builder()
                .message(message)
                .bindings(mapOf("kafka" to KafkaOperationBindings()))
                .build()
    }

    @Test
    fun `given no annotated methods, getChannels should return an empty map`() {
        // Given a class without methods annotated with KafkaListener
        // When getChannels is called
        val channels = kafkaListenersScanner.getChannels(ClassWithoutKafkaListenerAnnotations::class.java)

        // Then the returned collection is empty
        assertThat(channels).isEmpty()
    }

    @Test
    fun `given methods annotated with @KafkaListener with hardcoded topic, getChannels should return a matching channel`() {
        // Given a class with methods annotated with KafkaListener, whose topics attribute is hard coded
        // When getChannels is called
        val actualChannels = kafkaListenersScanner.getChannels(ClassWithKafkaListenerAnnotationsHardCodedTopics::class.java)

        // Then the returned collection contains the methods' details
        val operation = buildOperation(SimpleFoo::class.java)
        val expectedChannels = mapOf(TOPIC to Channel.ofSubscribe(operation))
        assertEquals(expectedChannels, actualChannels)
    }

    @Test
    fun `given methods annotated with @KafkaListener with an embedded topic, getChannels should return a matching channel`() {
        // Given a class with methods annotated with KafkaListener, whose topics attribute is an embedded value
        // When getChannels is called
        val actualChannels = kafkaListenersScanner.getChannels(ClassWithKafkaListenerAnnotationsEmbeddedValueTopic::class.java)

        // Then the returned collection contains the methods' details
        val operation = buildOperation(SimpleFoo::class.java)
        val expectedChannels = mapOf(TOPIC to Channel.ofSubscribe(operation))
        assertEquals(expectedChannels, actualChannels)
    }

    @Test
    fun `given methods annotated with @KafkaListener with multiple topics, getChannels should return matching channels`() {
        // Given a class with methods annotated with KafkaListener, whose topics contain multiple topics
        // When getChannels is called
        val actualChannels = kafkaListenersScanner.getChannels(ClassWithKafkaListenerAnnotationsMultipleTopics::class.java)

        // Then the returned collection contains the methods' details
        val operation = buildOperation(SimpleFoo::class.java)
        val expectedChannels = mapOf(
                TOPIC + "1" to Channel.ofSubscribe(operation),
                TOPIC + "2" to Channel.ofSubscribe(operation)
        )
        assertEquals(expectedChannels, actualChannels)
    }

    private class ClassWithoutKafkaListenerAnnotations {
        @Deprecated("")
        private fun methodWithoutAnnotation1() {
        }

        private fun methodWithoutAnnotation2() {}
    }

    private class ClassWithKafkaListenerAnnotationsHardCodedTopics {
        @KafkaListener(topics = [TOPIC])
        private fun methodWithAnnotation1(payload: SimpleFoo) {
        }

        private fun methodWithoutAnnotation1() {}
        private fun methodWithoutAnnotation2() {}
    }

    private class ClassWithKafkaListenerAnnotationsEmbeddedValueTopic {
        @KafkaListener(topics = ["\${kafka.topics.test}"])
        private fun methodWithAnnotation1(payload: SimpleFoo) {
        }
    }

    private class ClassWithKafkaListenerAnnotationsMultipleTopics {
        @KafkaListener(topics = [TOPIC + "1", TOPIC + "2"])
        private fun methodWithAnnotation1(payload: SimpleFoo) {
        }
    }

    private class SimpleFoo {
        val s: String? = null
        val b = false
    }

}