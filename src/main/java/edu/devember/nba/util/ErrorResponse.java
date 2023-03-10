package edu.devember.nba.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> details;
}
