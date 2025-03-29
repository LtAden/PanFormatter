package innConfValidator.validators;

import innConfValidator.InnConfValidatorHandler;
import model.InnConf;

import java.util.List;

public class PrefixLengthValidator extends InnConfValidatorHandler {
    private final String ERROR_MESSAGE = "InnRange sizes doesn't match prefix size";

    @Override
    public void validate(InnConf innConf, List<String> issues){
        int innRangeLowSize = String.valueOf(innConf.getInnPrefixLow()).length();
        int innRangeHighSize = String.valueOf(innConf.getInnPrefixHigh()).length();
        int innPrefixSize = innConf.getPrefixLength();
        if(!(innRangeLowSize == innPrefixSize) && !(innRangeHighSize == innPrefixSize)){
            issues.add(ERROR_MESSAGE);
        }
        super.validate(innConf, issues);
    }
}
