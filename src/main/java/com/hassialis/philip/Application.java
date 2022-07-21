package com.hassialis.philip;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "Philip Broker", version = "1.0", description = "Demo micronaut 2 application", license = @License(name = "MIT")))

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
