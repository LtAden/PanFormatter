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

public class PanFormatter {
  private static final String COMMA_SEPARATOR = ",";
  private final String confFile;
  private static final Logger LOGGER = LogManager.getLogger(PanFormatter.class);
  private static final String EXPECTED_PATTERN_REGEX = "^#[#\\s]*$";
  private static final String CONFIG_FILE_SEPARATOR = ";";
  private static final char PATTERN_PLACEHOLDER_CHARACTER = '#';
  private List<InnConf> configs;

  public PanFormatter(String configFileName) {
    this.confFile = configFileName;
  }

  /**
   * Validates PAN Number. If it is supported returns formatted PAN. Otherwise, raises exception.
   *
   * @param panNumber - pan Number which is to be formatted
   * @return PAN number formatted to found format
   * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
   */
  public String formatPan(String panNumber){
    if (this.configs == null) {
      this.configs = getConfiguration();
    }
    String pattern = findPatternFromRecordThatMatchesPanOrThrowException(configs, panNumber);
    try {
      return formatPanWithGivenPattern(panNumber, pattern);
    } catch (ParseException e) {
      throw new UnsupportedOperationException("Failed to parse pan number to format: ", e);
    }
  }

  /**
   * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
   *
   * @return Configuration of supported patterns related to IIN Ranges.
   * @throws IllegalStateException - when config file could not be found or is empty
   */
  private List<InnConf> getConfiguration() {
    List<Map<String, String>> listOfMappedRecords;
    try {
      listOfMappedRecords = getListOfMappedRecordsFromConfigFile();
    } catch (Exception e) {
      throw new IllegalStateException("Config file empty or does not exist");
    }
    return getListOfValidInnConfObjectsFromMappedRecords(listOfMappedRecords);
  }

  /**
   * Reads config file from resources, takes headers line as a source of map keys, and then converts
   * all the lines in the file to arrays, maps them using headers and collects them to a list.
   *
   * @throws IOException - when there was a problem reading a file
   * @throws NullPointerException - when file is missing or empty
   * @return List of Maps from provided config CSV
   */
  private List<Map<String, String>> getListOfMappedRecordsFromConfigFile() throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(confFile)))) {
      String[] csvHeaders = reader.readLine().split(CONFIG_FILE_SEPARATOR);
      return reader
          .lines()
          .map(line -> line.split(CONFIG_FILE_SEPARATOR))
          .map(
              csvLineArray ->
                  IntStream.range(0, csvLineArray.length)
                      .boxed()
                      .collect(toMap(i -> csvHeaders[i], i -> csvLineArray[i])))
          .toList();
    }
  }

  private List<InnConf> getListOfValidInnConfObjectsFromMappedRecords(
      List<Map<String, String>> listOfMappedRecords) {
    InnConf innConf;
    List<InnConf> result = new ArrayList<>();
    for (Map<String, String> map : listOfMappedRecords) {
      innConf = getInnConfObjectFromMap(map);
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

  private InnConf getInnConfObjectFromMap(Map<String, String> map) {
    return new InnConf(
        map.get("Issuer Name"),
        Integer.parseInt(map.get("supported pan length")),
        Integer.parseInt(map.get("prefixLength")),
        Integer.parseInt(map.get("innRangeLow")),
        Integer.parseInt(map.get("innRangeHigh")),
        map.get("pattern"));
  }

  /**
   * Iterates through the list of InnConfs, and tries to match provided pan number to any of them.
   * It adds all Patterns from InnConf objects that match given pan to a list.
   *
   * @param panNumber - pan number for which a formatting pattern should be found
   * @param listOfInnConfs - list of InnConf objects read from configuration to be checked against
   * @throws UnsupportedOperationException - if none of InnConf match given pan number (empty map)
   * @throws IllegalStateException - if more than one InnConf matches given pan number (map size
   *     bigger than 1)
   * @return Pattern from InnConf object that matches given pan
   */
  private String findPatternFromRecordThatMatchesPanOrThrowException(
      List<InnConf> listOfInnConfs, String panNumber) {
    List<String> result = new ArrayList<>();
    for (InnConf innConf : listOfInnConfs) {
      if (doesPanMatchInnConfConstraints(panNumber, innConf)) {
        LOGGER.info("Pan number {} was found to match {} card", panNumber, innConf.getIssuerName());
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

  private boolean doesPanMatchInnConfConstraints(String panNumber, InnConf innConf) {
    boolean result = true;
    if (!(panNumber.length() == innConf.getSupportedLength())) {
      result = false;
    } else if (!isPanNumberPrefixInRange(panNumber, innConf)) {
      result = false;
    }
    return result;
  }

  private boolean isPanNumberPrefixInRange(String panNumber, InnConf innConf) {
    String panNumberPrefix = panNumber.substring(0, innConf.getPrefixLength());
    int panNumberPrefixValue = Integer.parseInt(panNumberPrefix);
    return (panNumberPrefixValue >= innConf.getInnPrefixLow())
        && (panNumberPrefixValue <= innConf.getInnPrefixHigh());
  }

  /**
   * Preforms a few checks on InnConf objects to confirm it's valid. It checks that InnObject: - Pan
   * Patter is a series of Placeholer characters (#) and Spaces; - That amount of Placeholder
   * characters in Pan Pattern match the supported pan length - that prefix size is just as long as
   * both inn range parameters - that prefix is not longer than supported pan length
   *
   * <p>Any failure is logged, but following checks are still evaluated - this way all mistakes for
   * given record can be fixed right away.
   *
   * @param innConf - innConf object to be validated
   * @return true if no problems were found, false otherwise
   */
  private boolean isInnConfObjectValid(InnConf innConf) {
    boolean result = true;
    StringBuilder issuesFound = new StringBuilder();
    if (!doesPanPatternMatchExpectedRegex(innConf)) {
      issuesFound.append("Invalid pan pattern").append(COMMA_SEPARATOR);
      result = false;
    } else if (!doesAmountOfPatternPlaceholderCharactersMatchSupportedPanLength(innConf)) {
      issuesFound
          .append("Amount of placeholder characters doesn't match supported pan size ")
          .append(COMMA_SEPARATOR);
      result = false;
    }
    if (!doesInnRangeSizesMatchPrefixSize(innConf)) {
      issuesFound.append("InnRange sizes doesn't match prefix size").append(COMMA_SEPARATOR);
      result = false;
    }
    if (innConf.getPrefixLength() > innConf.getSupportedLength()) {
      issuesFound.append("Prefix size is bigger than supported pan length").append(COMMA_SEPARATOR);
      result = false;
    }
    if (!result) {
      LOGGER.info("Following issues were found for InnConf object {}: {}", innConf, issuesFound);
    }
    return result;
  }

  private boolean doesPanPatternMatchExpectedRegex(InnConf innConf) {
    return innConf.getPanPattern().matches(EXPECTED_PATTERN_REGEX);
  }

  private boolean doesAmountOfPatternPlaceholderCharactersMatchSupportedPanLength(InnConf innConf) {
    long xCountInPattern =
        innConf.getPanPattern().chars().filter(ch -> ch == PATTERN_PLACEHOLDER_CHARACTER).count();
    return xCountInPattern == innConf.getSupportedLength();
  }

  private boolean doesInnRangeSizesMatchPrefixSize(InnConf innConf) {
    int innRangeLowSize = String.valueOf(innConf.getInnPrefixLow()).length();
    int innRangeHighSize = String.valueOf(innConf.getInnPrefixHigh()).length();
    int innPrefixSize = innConf.getPrefixLength();
    return (innRangeLowSize == innPrefixSize) && (innRangeHighSize == innPrefixSize);
  }

  private String formatPanWithGivenPattern(String panNumber, String pattern) throws ParseException {
    MaskFormatter formatter = new MaskFormatter(pattern);
    formatter.setValueContainsLiteralCharacters(false);
    return formatter.valueToString(panNumber);
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
