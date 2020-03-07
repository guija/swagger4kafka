package io.github.stavshamir.swagger4kafka.web;

import io.github.stavshamir.swagger4kafka.services.AsyncApiDocService;
import io.github.stavshamir.swagger4kafka.types.AsyncApiDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/async-api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncApiController {

    private final AsyncApiDocService asyncApiDocService;

    @GetMapping(value = "/doc", produces = MediaType.APPLICATION_JSON_VALUE)
    public AsyncApiDoc doc() {
        return asyncApiDocService.getDoc();
    }

    @GetMapping(value = "/doc.yaml", produces = MediaType.TEXT_PLAIN_VALUE)
    public String docAsYaml() {
        return asyncApiDocService.getDocAsYaml();
    }

}
