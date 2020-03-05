package io.github.stavshamir.swagger4kafka.services;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class ComponentScanner {

    public Set<Class<?>> getComponentClasses(String basePackage) {
        return getCandidateComponents(basePackage).stream()
                .flatMap(this::getClass)
                .collect(toSet());
    }

    private Set<BeanDefinition> getCandidateComponents(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        return provider.findCandidateComponents(basePackage);
    }

    private Stream<Class<?>> getClass(BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        log.debug("Found candidate class: {}", className);

        try {
            return Stream.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            log.error("Class {} not found", className);
        }

        return Stream.empty();
    }

}
