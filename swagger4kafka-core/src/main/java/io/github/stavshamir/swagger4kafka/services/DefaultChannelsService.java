package io.github.stavshamir.swagger4kafka.services;

import io.github.stavshamir.swagger4kafka.types.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DefaultChannelsService implements ChannelsService {

    @Getter
    private final Map<String, Channel> channels = new HashMap<>();

}
