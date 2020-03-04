package io.github.stavshamir.swagger4kafka.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    private Operation publish;
    private Operation subscribe;

    public static Channel ofSubscribe(Operation subscribe) {
        return new Channel(null, subscribe);
    }

    public static Channel ofPublish(Operation publish) {
        return new Channel(publish, null);
    }

}
