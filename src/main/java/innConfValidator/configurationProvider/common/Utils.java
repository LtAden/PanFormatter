package innConfValidator.configurationProvider.common;

import innConfValidator.InnConfValidator;
import innConfValidator.InnConfValidatorHandler;
import model.InnConf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static List<InnConf> getListOfValidInnConfObjectsFromMappedRecords(
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

    private static InnConf getInnConfObjectFromMap(Map<String, String> map) {
        return new InnConf(
                map.get("Issuer Name"),
                Integer.parseInt(map.get("supported pan length")),
                Integer.parseInt(map.get("prefixLength")),
                Integer.parseInt(map.get("innRangeLow")),
                Integer.parseInt(map.get("innRangeHigh")),
                map.get("pattern"));
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
    private static boolean isInnConfObjectValid(InnConf innConf) {
        List<String> issuesFound = new ArrayList<>();
        InnConfValidatorHandler validatorChain = InnConfValidator.getValidationChain();
        validatorChain.validate(innConf, issuesFound);
        if (issuesFound.size() > 0) {
            LOGGER.info("Following issues were found for InnConf object {}: {}", innConf, issuesFound);
            return false;
        }
        return true;
    }

}
