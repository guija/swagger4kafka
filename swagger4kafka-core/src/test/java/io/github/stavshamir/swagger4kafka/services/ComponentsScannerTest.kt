package io.github.stavshamir.swagger4kafka.services

import io.github.stavshamir.swagger4kafka.test.components.ComponentClass
import io.github.stavshamir.swagger4kafka.test.components.NotAComponentClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ComponentsScannerTest {

    private val componentsScanner = ComponentsScanner()

    @Test
    fun `given a base package containing a class annotated with @Component, get components should return a set containing that class`() {
        val basePackage = ComponentClass::class.java.`package`.name
        val components = componentsScanner.getComponentClasses(basePackage)
        assertThat(components)
                .contains(ComponentClass::class.java)
                .doesNotContain(NotAComponentClass::class.java)
    }
}