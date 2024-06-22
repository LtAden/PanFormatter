import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

public class PanFormatter {
  private static final String CONF_FILE = "conf.csv";
  private static final Logger LOGGER = LogManager.getLogger(PanFormatter.class);

  /**
   * Validates PAN Number. If it is supported returns formatted PAN. Otherwise raises exception.
   *
   * @return
   * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
   */
  public String formatPan(String panNumber) {
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
    } catch (IOException e) {
      throw new IllegalStateException(
          "Unable to read the records from config file. Stacktrace: " + e);
    }

    List<InnConf> result = getListOfInnConfFromMappedRecords(listOfMappedRecords);
    return null;
  }

  private List<Map<String, String>> getListOfMappedRecords() throws IOException {
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(CONF_FILE)))) {
      String[] headers = br.readLine().split(";");
      return br.lines()
          .map(line -> line.split(";"))
          .map(
              lineArray ->
                  IntStream.range(0, lineArray.length)
                      .boxed()
                      .collect(toMap(i -> headers[i], i -> lineArray[i])))
          .toList();
    }
  }

  private List<InnConf> getListOfInnConfFromMappedRecords(
      List<Map<String, String>> listOfMappedRecords) {
    InnConf innConf;
    for (Map<String, String> map : listOfMappedRecords) {
      innConf = getObjectFromMap(map);
    }
  }

  private InnConf getObjectFromMap(Map<String, String> map) {
    return new InnConf(
        map.get("Issuer Name"),
        Integer.parseInt(map.get("supported pan length")),
        Integer.parseInt(map.get("prefixLength")),
        Integer.parseInt(map.get("innRangeLow")),
        Integer.parseInt(map.get("innRangeHigh")),
        map.get("pattern"));
  }

  @Data
  @Getter
  @AllArgsConstructor
  private static class InnConf {

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
}
