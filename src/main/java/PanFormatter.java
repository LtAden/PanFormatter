/*

*/

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class PanFormatter {
    private static final String CONF_FILE = "conf.csv";

    /**
     * Validates PAN Number. If it is supported returns formatted PAN.
     * Otherwise raises exception.
     *
     * @return
     * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
     */
    public String formatPan(String panNumber) throws UnsupportedOperationException {
        List<InnConf> configs = getConfiguration();
        return null;
    }

    /**
     * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
     *
     * @return Configuration of supported patterns related to IIN Ranges.
     */
    private List<InnConf> getConfiguration() {
        return null;
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