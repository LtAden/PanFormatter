package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InnConf {

    /** Name of institution that issues a card. */
    private String issuerName;

    /** Supported length of PAN Number. */
    private int supportedLength;

    /** Supported prefix length to check IIN Range. */
    private int prefixLength;

    /** Min value of a prefix. */
    private int innPrefixLow;

    /** Max value of a prefix. */
    private int innPrefixHigh;

    /** PAN Pattern for formatting. */
    private String panPattern;
}
