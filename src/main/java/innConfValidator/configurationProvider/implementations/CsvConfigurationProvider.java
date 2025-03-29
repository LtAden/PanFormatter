package innConfValidator.configurationProvider.implementations;

import innConfValidator.configurationProvider.ConfigurationProvider;
import model.InnConf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static innConfValidator.configurationProvider.common.Utils.getListOfValidInnConfObjectsFromMappedRecords;
import static java.util.stream.Collectors.toMap;

/**
 * Reads configuration from CSV file and map it to list of {@link InnConf} objects.
 *
 * @return Configuration of supported patterns related to IIN Ranges.
 * @throws IllegalStateException - when config file could not be found or is empty
 */
public class CsvConfigurationProvider implements ConfigurationProvider {
  private static final String CONFIG_FILE_SEPARATOR = ";";

  @Override
  public List<InnConf> getConfiguration(String configFile) {
    List<Map<String, String>> listOfMappedRecords;
    try {
      listOfMappedRecords = getListOfMappedRecordsFromConfigFile(configFile);
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
  private List<Map<String, String>> getListOfMappedRecordsFromConfigFile(String configFileName)
      throws IOException {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(configFileName)))) {
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
}
