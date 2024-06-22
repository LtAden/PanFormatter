/*
conf.csv

Issuer Name;supported pan length;prefixLength;innRangeLow;innRangeHigh;pattern
Visa (incl. VPay);16;1;4;4;#### #### #### ####
Visa (incl. VPay);13;1;4;4;Pattern not known
Visa (incl. VPay);14;1;4;4;Pattern not known
Visa (incl. VPay);15;1;4;4;Pattern not known
Visa (incl. VPay);17;1;4;4;Pattern not known
Visa (incl. VPay);18;1;4;4;Pattern not known
Visa (incl. VPay);19;1;4;4;Pattern not known
Visa Electron;16;4;4026;4026;#### #### #### ####
Visa Electron;16;6;417500;417500;#### #### #### ####
Visa Electron;16;4;4405;4405;#### #### #### ####
Visa Electron;16;4;4508;4508;#### #### #### ####
Visa Electron;16;4;4844;4844;#### #### #### ####
Visa Electron;16;4;4913;4913;#### #### #### ####
Visa Electron;16;4;4917;4917;#### #### #### ####
American Express;15;2;34;34;#### ###### #####
American Express;15;2;37;37;#### ###### #####
China UnionPay;16;2;62;62;#### #### #### ####
China UnionPay;19;2;62;62;###### #############
China UnionPay;17;2;62;62;Pattern not known
China UnionPay;18;2;62;62;Pattern not known
MasterCard;16;2;51;55;#### #### #### ####
MasterCard;16;6;222100;272099;#### #### #### ####
Maestro;13;6;500000;509999;#### #### #####
Maestro;13;6;560000;589999;#### #### #####
Maestro;13;6;600000;699999;#### #### #####
Maestro;15;6;500000;509999;#### ###### #####
Maestro;15;6;560000;589999;#### ###### #####
Maestro;15;6;600000;699999;#### ###### #####
Maestro;19;6;500000;509999;#### #### #### #### ###
Maestro;19;6;560000;589999;#### #### #### #### ###
Maestro;19;6;600000;699999;#### #### #### #### ###
Maestro;16;6;500000;509999;#### #### #### ####
Maestro;16;6;560000;589999;#### #### #### ####
Maestro;16;6;600000;699999;#### #### #### ####
Maestro;12;6;500000;509999;Pattern not known
Maestro;12;6;560000;589999;Pattern not known
Maestro;12;6;600000;699999;Pattern not known
Maestro;14;6;500000;509999;Pattern not known
Maestro;14;6;560000;589999;Pattern not known
Maestro;14;6;600000;699999;Pattern not known
Maestro;17;6;500000;509999;Pattern not known
Maestro;17;6;560000;589999;Pattern not known
Maestro;17;6;600000;699999;Pattern not known
Maestro;18;6;500000;509999;Pattern not known
Maestro;18;6;560000;589999;Pattern not known
Maestro;18;6;600000;699999;Pattern not known
Diners Club Carte Blanche;14;3;300;305;#### ###### ####
Diners Club International;14;3;300;305;#### ###### ####
Diners Club International;14;3;309;309;#### ###### ####
Diners Club International;14;2;36;36;#### ###### ####
Diners Club International;14;2;38;39;#### ###### ####
Diners Club United States & Canada;16;2;54;54;#### #### #### ####
Diners Club United States & Canada;16;2;55;55;#### #### #### ####
Discover;16;4;6011;6011;#### #### #### ####
Discover;16;6;622126;622925;#### #### #### ####
Discover;16;3;644;649;#### #### #### ####
Discover;16;2;65;65;#### #### #### ####
JCB;16;4;3528;3589;#### #### #### ####
UATP;15;1;1;1;#### ##### ######
Dankort;16;4;5019;5019;#### #### #### ####
InterPayment;16;3;636;636;#### #### #### ####
InterPayment;17;3;636;636;Pattern not known
InterPayment;18;3;636;636;Pattern not known
InterPayment;19;3;636;636;Pattern not known
*/

/*
examples
input:
4444444444444444
match:
Visa (incl. VPay);16;1;4;4;#### #### #### ####
output:
4444 4444 4444 4444

input:
30122994494222
match:
Diners Club Carte Blanche;14;3;300;305;#### ###### ####
output:
3012 299449 4222
*/

import java.util.List;

public class PanFormatter {
    private static final String CONF_FILE = "conf.csv";

    /**
     * Validates PAN Number. If it is supported returns formatted PAN.
     * Otherwise raises exception.
     * @return
     * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
     */
    public String formatPan(String panNumber) throws UnsupportedOperationException{
        List<InnConf> configs = getConfiguration();
        //...
    }

    /**
     * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
     * @return Configuration of supported patterns related to IIN Ranges.
     */
    private List<InnConf> getConfiguration(){
       //...
    }

    @Data
    @RequiredArgsConstructor
    private static class InnConf {

        /**
         * Name of institution that issues a card.
         */
        private String issuerName;

        /**
         * Supported length of PAN Number.
         */
        private int supportedLength;

        /**
         * Supported prefix length to check IIN Range.
         */
        private int prefixLength;
        /**
         * Min value of a prefix.
         */
        private int innPrefixLow;

        /**
         * Max value of a prefix.
         */
        private int innPrefixHigh;
        /**
         * PAN Pattern for formatting.
         */
        private String panPattern;

    }
}

//Inputs:
//        - IIN Range to format configuration - input is provided in the CSV file, program has to read it (decision if statically or file path configurable as program param is up to Your preference)
//        - PAN Number
//        Outputs:
//        - Formatted PAN - in case of positive match
//        - Exception with a message - in case of not supported pan
//        Goals:
//        - PAN Number needs to be matched to the IIN Range
//        - not all of IIN Ranges define the same prefix length
//        - each IIN Range can have specific formatting for specific PAN length
//        - program needs to decide on the best match of the record in configuration
//    - in case of unsupported formatting user has to be notified about that fact
//            - when match of IIN Range leads to supported formatting, program should return formatted PAN
//            NFR:
//            - Code coverage over 70% - aim for 100%
//            - clean code
//            - edge cases scenarios covered