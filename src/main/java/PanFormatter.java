import java.text.ParseException;
import java.util.*;

import innConfValidator.configurationProvider.ConfigurationProvider;
import model.InnConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.MaskFormatter;

public class PanFormatter {
  private static final Logger LOGGER = LogManager.getLogger(PanFormatter.class);
  private List<InnConf> configs;

  public PanFormatter(ConfigurationProvider configurationProvider, String configFileName) {
    this.configs = configurationProvider.getConfiguration(configFileName);
  }

  /**
   * Validates PAN Number. If it is supported returns formatted PAN. Otherwise, raises exception.
   *
   * @param panNumber - pan Number which is to be formatted
   * @return PAN number formatted to found format
   * @throws UnsupportedOperationException - when PAN Number is not supported by configuration
   */
  public String formatPan(String panNumber) {
    String pattern = findPatternFromRecordThatMatchesPanOrThrowException(configs, panNumber);
    try {
      return formatPanWithGivenPattern(panNumber, pattern);
    } catch (ParseException e) {
      throw new UnsupportedOperationException("Failed to parse pan number to format: ", e);
    }
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
    return (panNumber.length() == innConf.getSupportedLength())
        && isPanNumberPrefixInRange(panNumber, innConf);
  }

  private boolean isPanNumberPrefixInRange(String panNumber, InnConf innConf) {
    String panNumberPrefix = panNumber.substring(0, innConf.getPrefixLength());
    int panNumberPrefixValue = Integer.parseInt(panNumberPrefix);
    return (panNumberPrefixValue >= innConf.getInnPrefixLow())
        && (panNumberPrefixValue <= innConf.getInnPrefixHigh());
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
}
