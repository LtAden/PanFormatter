package innConfValidator.validators;

import innConfValidator.InnConfValidatorHandler;
import model.InnConf;

import java.util.List;

public class SupportedLengthValidator extends InnConfValidatorHandler {
    private final String ERROR_MESSAGE = "Prefix size is bigger than supported pan length";

    @Override
    public void validate(InnConf innConf, List<String> issues){
        if(innConf.getPrefixLength() > innConf.getSupportedLength()){
            issues.add(ERROR_MESSAGE);
        }
        super.validate(innConf, issues);
    }
}
