package innConfValidator.validators;

import innConfValidator.InnConfValidatorHandler;
import model.InnConf;

import java.util.List;

public class ExpectedPatternRegexValidator extends InnConfValidatorHandler {
    private final String EXPECTED_PATTERN_REGEX = "^#[#\\s]*$";
    private final String ERROR_MESSAGE = "Invalid pan pattern";

    @Override
    public void validate(InnConf innConf, List<String> issues){
        if(!innConf.getPanPattern().matches(EXPECTED_PATTERN_REGEX)){
            issues.add(ERROR_MESSAGE);
        }
        super.validate(innConf, issues);
    }
}
