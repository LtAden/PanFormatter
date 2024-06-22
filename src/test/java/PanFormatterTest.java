import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

class PanFormatterTest {

  @Test
  void formatPanWhenNumberCanBeMatchedInGivenConfiguration() throws ParseException {
    PanFormatter formatter = new PanFormatter("conf.csv");
    String input = "4444444444444444";
    assertThat(formatter.formatPan(input))
        .as("Check if input will be correctly formatted when there is a match in config file")
        .isEqualTo("4444 4444 4444 4444");
  }
}
