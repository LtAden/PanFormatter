/*

 */

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class PanFormatter {
    private static final String CONF_FILE = "conf.csv";

    /**
     * Validates PAN Number. If it is supported returns formatted PAN.
     * Otherwise raises exception.
     *
     * @return
     * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
     */
    public String formatPan(String panNumber){
        List<InnConf> configs = getConfiguration();
        return null;
    }

    /**
     * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
     *
     * @return Configuration of supported patterns related to IIN Ranges.
     */
    public List<InnConf> getConfiguration() {
        List<Map<String, String>> listOfMappedRecords;
        try {
            listOfMappedRecords = getListOfMappedRecords();
        } catch(IOException e){
            throw new IllegalStateException("Unable to read the records from config file. Stacktrace: " + e);
        }

        List<InnConf> result = getListofInnConfFromMappedRecords(listOfMappedRecords);
        return null;
    }

    private List<Map<String, String>> getListOfMappedRecords() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream(CONF_FILE)))) {
            String[] headers = br.readLine().split(";");
            return br.lines().map(line -> line.split(";"))
                    .map(lineArray -> IntStream.range(0, lineArray.length)
                            .boxed()
                            .collect(toMap(i -> headers[i], i -> lineArray[i]))).toList();
        }
    }

    private List<InnConf> getListofInnConfFromMappedRecords(List<Map<String, String>> listOfMappedRecords){
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