package io.github.stavshamir.swagger4kafka.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.stavshamir.swagger4kafka.services.SpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/async-api")
public class AsyncApiController {

    private final SpecificationService specificationService;

    @GetMapping(value = "/doc", produces = MediaType.APPLICATION_JSON_VALUE)
    public String doc() throws JsonProcessingException {
        return specificationService.getDocAsJson();
    }

    @GetMapping(value = "/doc.yaml")
    public String docAsYaml() throws JsonProcessingException {
        return specificationService.getSpecificationAsYaml();
    }

}
