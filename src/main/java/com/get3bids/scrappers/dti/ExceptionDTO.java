package com.get3bids.scrappers.dti;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDTO {
    private String exceptionMessage;
    private Integer errorReference;
    private String errorUserMessage;
}
