package com.github.leapbound.yc.hub.service.impl.gpt.function;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.function.Function;

public class CheckSmsStatusFunction implements Function<CheckSmsStatusFunction.Request, CheckSmsStatusFunction.Response> {

    @Override
    public Response apply(Request request) {

        double temperature = 0;
        if (request.location().contains("Paris")) {
            temperature = 15;
        }
        else if (request.location().contains("Tokyo")) {
            temperature = 10;
        }
        else if (request.location().contains("San Francisco")) {
            temperature = 30;
        }

        return new Response(temperature, 15, 20, 2, 53, 45, Unit.C);
    }

    /**
     * Temperature units.
     */
    public enum Unit {

        /**
         * Celsius.
         */
        C("metric"),
        /**
         * Fahrenheit.
         */
        F("imperial");

        /**
         * Human readable unit name.
         */
        public final String unitName;

        Unit(String text) {
            this.unitName = text;
        }

    }

    /**
     * Weather Function request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Weather API request")
    public record Request(@JsonProperty(required = true,
            value = "location") @JsonPropertyDescription("The city and state e.g. San Francisco, CA") String location,
                          @JsonProperty(required = true, value = "unit") @JsonPropertyDescription("Temperature unit") Unit unit) {

    }

    /**
     * Weather Function response.
     */
    public record Response(double temp, double feels_like, double temp_min, double temp_max, int pressure, int humidity,
                           Unit unit) {

    }

}
