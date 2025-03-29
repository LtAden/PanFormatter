package innConfValidator;

import lombok.Setter;
import model.InnConf;

import java.util.List;

@Setter
public abstract class InnConfValidatorHandler {
    protected InnConfValidatorHandler nextHandler;


    public void validate(InnConf innConf, List<String> issues) {
        if (nextHandler != null) {
            nextHandler.validate(innConf, issues);
        }
    }
}
