package innConfValidator.validators;

import innConfValidator.InnConfValidatorHandler;
import model.InnConf;

import java.util.List;

public class PlaceholderCountToSupportedLengthValidator extends InnConfValidatorHandler {
    private final char PATTERN_PLACEHOLDER_CHARACTER = '#';
    private final String ERROR_MESSAGE = "Amount of placeholder characters doesn't match supported pan size";

    @Override
    public void validate(InnConf innConf, List<String> issues){
        long xCountInPattern =
                innConf.getPanPattern().chars().filter(ch -> ch == PATTERN_PLACEHOLDER_CHARACTER).count();
        if(xCountInPattern != innConf.getSupportedLength()){
            issues.add(ERROR_MESSAGE);
        }
        super.validate(innConf, issues);
    }
}
