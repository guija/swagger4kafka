package io.github.stavshamir.swagger4kafka.services;

import io.github.stavshamir.swagger4kafka.types.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DefaultChannelsService implements ChannelsService {

    private final KafkaListenersScanner kafkaListenersScanner;

    @Getter
    private Map<String, Channel> channels = new HashMap<>();

    @Autowired
    public DefaultChannelsService(@Nullable KafkaListenersScanner kafkaListenersScanner) {
        this.kafkaListenersScanner = kafkaListenersScanner;
    }

    @PostConstruct
    public void postConstruct() {
        Optional.ofNullable(kafkaListenersScanner).ifPresent(scanner -> channels.putAll(scanner.getChannels()));
    }

}
