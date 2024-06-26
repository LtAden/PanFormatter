import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PanFormatterTest {

  @Test
  void formatPanWhenNumberCanBeMatchedInGivenConfiguration() {
    PanFormatter formatter = new PanFormatter("conf.csv");
    String input = "4444444444444444";
    assertThat(formatter.formatPan(input))
        .as("Check if input will be correctly formatted when there is a match in config file")
        .isEqualTo("4444 4444 4444 4444");
  }

  @Test
  void formatPanFailsWhenThereIsNoMatchingRecordFoundInConfiguration() {
    PanFormatter formatter = new PanFormatter("conf.csv");
    String input = "23";
    assertThatThrownBy(() -> formatter.formatPan(input))
        .as("Check if exception is thrown when no match could be found in provided config")
        .hasMessage("Failed to find pattern matching given pan number")
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void formatPanFailsWhenThereIsMoreThanOneMatchOnConfigurationRecords() {
    PanFormatter formatter = new PanFormatter("configWithDuplicatedRecord.csv");
    String input = "4444444444444444";
    assertThatThrownBy(() -> formatter.formatPan(input))
        .as(
            "Check if exception is thrown when there is more than one match found in provided config")
        .hasMessage("More than one match found for given pan number. Configuration is invalid")
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void formatPanThrowsExceptionWhenThereAreNoValidRecordsInConfig() {
    PanFormatter formatter = new PanFormatter("configWithNoValidRecords.csv");
    String input = "4444444444444444";
    assertThatThrownBy(() -> formatter.formatPan(input))
        .as("Check if exception is thrown when there are no valid records in config file")
        .hasMessage("No valid InnConf objects could be collected from provided config file")
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void formatPanThrowsExceptionWhenConfigDoesNotExist() {
    PanFormatter formatter = new PanFormatter("configFileThatDoesNotExist.csv");
    String input = "4444444444444444";
    assertThatThrownBy(() -> formatter.formatPan(input))
        .as("Check if exception is thrown when there is no config file")
        .hasMessage("Config file empty or does not exist")
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void formatPanThrowsExceptionWhenConfigFileIsEmpty() {
    PanFormatter formatter = new PanFormatter("emptyConfig.csv");
    String input = "4444444444444444";
    assertThatThrownBy(() -> formatter.formatPan(input))
        .as("Check if exception is thrown when config file is empty")
        .hasMessage("Config file empty or does not exist")
        .isInstanceOf(IllegalStateException.class);
  }
}
