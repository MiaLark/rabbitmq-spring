package com.wangwei.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author: wangwei
 * @date: 2019-09-15 16:02
 */
@Data
public class BnakInfo {

    private String cardType;
    private String bank;

    @JsonProperty("key")
    private String cardNumber;
    private String messages;
    private boolean validated;
    private String stat;
}
