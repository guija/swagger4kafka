package io.github.stavshamir.swagger4kafka.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.stavshamir.swagger4kafka.configuration.KafkaProtocolConfiguration;
import io.github.stavshamir.swagger4kafka.types.Channel;
import io.github.stavshamir.swagger4kafka.types.KafkaOperationBindings;
import io.github.stavshamir.swagger4kafka.types.Message;
import io.github.stavshamir.swagger4kafka.types.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaListenersScanner implements EmbeddedValueResolverAware {

    private StringValueResolver resolver;
    private final ModelsService modelsService;
    private final ComponentScanner componentScanner;
    private final KafkaProtocolConfiguration kafkaProtocolConfiguration;

    @Override
    public void setEmbeddedValueResolver(@NotNull StringValueResolver resolver) {
        this.resolver = resolver;
    }

    public Map<String, Channel> getChannels() {
        return componentScanner.getComponentClasses(kafkaProtocolConfiguration.getBasePackage()).stream()
                .map(this::getChannels)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Channel> getChannels(Class<?> type) {
        log.debug("Scanning {}", type.getName());

        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(KafkaListener.class))
                .map(this::kafkaListenerToChannels)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Channel> kafkaListenerToChannels(Method method) {
        KafkaListener annotation = Optional.of(method.getAnnotation(KafkaListener.class))
                .orElseThrow(() -> new IllegalArgumentException("Method must be annotated with @KafkaListener"));

        return getTopics(annotation).stream()
                .collect(toMap(topic -> topic, topic -> buildChannel(method)));
    }

    private Channel buildChannel(Method method) {
        Class<?> payloadType = getPayloadType(method);
        String modelName = modelsService.register(payloadType);

        Message message = Message.builder()
                .name(payloadType.getName())
                .title(modelName)
                .payload(modelsService.getDefinitions().get(modelName))
                .examples(ImmutableList.of(modelsService.getExample(modelName)))
                .build();

        Operation operation = Operation.builder()
                .bindings(ImmutableMap.of("kafka", new KafkaOperationBindings()))
                .message(message)
                .build();

        return Channel.ofSubscribe(operation);
    }

    private List<String> getTopics(KafkaListener kafkaListener) {
        String[] topics = kafkaListener.topics();

        if (topics.length == 1) {
            String s = resolver.resolveStringValue(topics[0]);
            return Collections.singletonList(s);
        }

        return Arrays.asList(topics);
    }

    private static Class<?> getPayloadType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException("Only single parameter KafkaListener methods are supported");
        }

        return parameterTypes[0];
    }

}
