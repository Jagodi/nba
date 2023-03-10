package edu.devember.nba.util;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class NonValidJsonFormException extends RuntimeException {

    private final BindingResult bindingResult;

    public NonValidJsonFormException(BindingResult result) {
        this.bindingResult = result;
    }
}
