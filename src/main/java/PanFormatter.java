import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.MaskFormatter;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

// TODO document more complex methods
public class PanFormatter {
  private final String confFile;
  private static final Logger LOGGER = LogManager.getLogger(PanFormatter.class);

  public PanFormatter(String configFileName) {
    this.confFile = configFileName;
  }

  /**
   * Validates PAN Number. If it is supported returns formatted PAN. Otherwise raises exception.
   *
   * @return
   * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
   */
  public String formatPan(String panNumber) throws ParseException {
    List<InnConf> configs = getConfiguration();
    String pattern = findMatchingPatternInConfigOrThrowException(configs, panNumber);
    return formatPanWithGivenPattern(panNumber, pattern);
  }

  private String formatPanWithGivenPattern(String panNumber, String pattern) throws ParseException {
    MaskFormatter formatter = new MaskFormatter(pattern);
    formatter.setValueContainsLiteralCharacters(false);
    return formatter.valueToString(panNumber);
  }

  private String findMatchingPatternInConfigOrThrowException(
      List<InnConf> listOfInnConfs, String panNumber) {
    List<String> result = new ArrayList<>();
    for (InnConf innConf : listOfInnConfs) {
      if (panMatchesInnConf(panNumber, innConf)) {
        result.add(innConf.getPanPattern());
      }
    }
    if (result.size() == 0) {
      throw new UnsupportedOperationException("Failed to find pattern matching given pan number");
    } else if (result.size() > 1) {
      throw new IllegalStateException(
          "More than one match found for given pan number. Configuration is invalid");
    }
    return result.get(0);
  }

  private boolean panMatchesInnConf(String panNumber, InnConf innConf) {
    boolean result = true;
    if (!(panNumber.length() == innConf.getSupportedLength())) {
      result = false;
    } else if (!panNumberMatchesPrefixRange(panNumber, innConf)) {
      result = false;
    }
    return result;
  }

  private boolean panNumberMatchesPrefixRange(String panNumber, InnConf innConf) {
    String panNumberPrefix = panNumber.substring(0, innConf.getPrefixLength());
    int panNumberPrefixValue = Integer.parseInt(panNumberPrefix);
    return (panNumberPrefixValue >= innConf.getInnPrefixLow())
        && (panNumberPrefixValue <= innConf.getInnPrefixHigh());
  }

  /**
   * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
   *
   * @return Configuration of supported patterns related to IIN Ranges.
   */
  private List<InnConf> getConfiguration() {
    List<Map<String, String>> listOfMappedRecords;
    try {
      listOfMappedRecords = getListOfMappedRecordsFromConfigFile();
    } catch (Exception e) {
      throw new IllegalStateException("Config file empty or does not exist");
    }
    return getListOfInnConfFromMappedRecords(listOfMappedRecords);
  }

  private List<Map<String, String>> getListOfMappedRecordsFromConfigFile() throws IOException {
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(confFile)))) {
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
    List<InnConf> result = new ArrayList<>();
    for (Map<String, String> map : listOfMappedRecords) {
      innConf = getObjectFromMap(map);
      if (isInnConfObjectValid(innConf)) {
        result.add(innConf);
      }
    }
    if (result.isEmpty()) {
      throw new IllegalStateException(
          "No valid InnConf objects could be collected from provided config file");
    }
    return result;
  }

  private boolean isInnConfObjectValid(InnConf innConf) {
    boolean result = true;
    if (!isPanPatternValid(innConf)) {
      LOGGER.info("Invalid pattern for InnConf {}", innConf);
      result = false;
    } else if (!doesPatternHaveRequiredAmountOfPlaceholders(innConf)) {
      LOGGER.info(
          "Amount of placeholder characters doesn't match supported size for InnConf {}", innConf);
      result = false;
    }
    if (!doesInnRangeSizesMatchPrefix(innConf)) {
      LOGGER.info("InnRange size doesn't match prefix size for InnConf {}", innConf);
      result = false;
    }
    if (innConf.getPrefixLength() > innConf.getSupportedLength()) {
      LOGGER.info("Prefix size bigger than supported pan length for InnConf {}", innConf);
      result = false;
    }
    return result;
  }

  private boolean isPanPatternValid(InnConf innConf) {
    String regexToMatch = "^#[#\\s]*$";
    return innConf.getPanPattern().matches(regexToMatch);
  }

  private boolean doesPatternHaveRequiredAmountOfPlaceholders(InnConf innConf) {
    long xCountInPattern = innConf.getPanPattern().chars().filter(ch -> ch == '#').count();
    return xCountInPattern == innConf.getSupportedLength();
  }

  private boolean doesInnRangeSizesMatchPrefix(InnConf innConf) {
    int innRangeLowSize = String.valueOf(innConf.getInnPrefixLow()).length();
    int innRangeHighSize = String.valueOf(innConf.getInnPrefixHigh()).length();
    int innPrefixSize = innConf.getPrefixLength();
    return (innRangeLowSize == innPrefixSize) && (innRangeHighSize == innPrefixSize);
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
