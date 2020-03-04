package io.github.stavshamir.swagger4kafka.services;

import io.github.stavshamir.swagger4kafka.types.Channel;

import java.util.Map;

public interface ChannelsService {

    Map<String, Channel> getChannels();

}
