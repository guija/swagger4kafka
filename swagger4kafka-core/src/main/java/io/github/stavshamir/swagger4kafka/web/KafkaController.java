package io.github.stavshamir.swagger4kafka.web;

import io.github.stavshamir.swagger4kafka.dtos.Payload;
import io.github.stavshamir.swagger4kafka.dtos.ValidationMessage;
import io.github.stavshamir.swagger4kafka.producer.KafkaProducer;
import io.github.stavshamir.swagger4kafka.services.ModelsService;
import io.github.stavshamir.swagger4kafka.validation.PayloadValidator;
import io.swagger.models.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Map;

@RestController
@RequestMapping("/kafka-api")
@CrossOrigin
public class KafkaController {

    private final KafkaProducer kafkaProducer;
    private final ModelsService modelsService;

    @Autowired
    public KafkaController(@Nullable  KafkaProducer kafkaProducer, ModelsService modelsService) {
        this.kafkaProducer = kafkaProducer;
        this.modelsService = modelsService;
    }

    @GetMapping(value = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Model> models() {
        return modelsService.getDefinitions();
    }

    @PostMapping("/validate")
    public ValidationMessage validate(@RequestBody Payload payload) {
        return PayloadValidator.validate(payload.getObject(), payload.getClassName());
    }

    @PostMapping("/publish")
    public ValidationMessage send(@RequestParam String topic, @RequestBody Payload payload) {
        ValidationMessage validation = PayloadValidator.validate(payload.getObject(), payload.getClassName());
        kafkaProducer.send(topic, payload.getObject());
        return validation;
    }

}

