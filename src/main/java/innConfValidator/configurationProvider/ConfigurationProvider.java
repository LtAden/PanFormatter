package innConfValidator.configurationProvider;

import model.InnConf;

import java.util.List;

public interface ConfigurationProvider {
    List<InnConf> getConfiguration(String configFile);
}
