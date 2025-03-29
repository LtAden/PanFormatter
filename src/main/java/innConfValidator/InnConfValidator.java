package innConfValidator;

import innConfValidator.validators.ExpectedPatternRegexValidator;
import innConfValidator.validators.PlaceholderCountToSupportedLengthValidator;
import innConfValidator.validators.PrefixLengthValidator;
import innConfValidator.validators.SupportedLengthValidator;

public class InnConfValidator {
    public static InnConfValidatorHandler getValidationChain(){
        InnConfValidatorHandler expectedPatternRegexValidator = new ExpectedPatternRegexValidator();
        InnConfValidatorHandler placeholderCountToSupportedLengthValidator = new PlaceholderCountToSupportedLengthValidator();
        InnConfValidatorHandler prefixLengthValidator = new PrefixLengthValidator();
        InnConfValidatorHandler supportedLengthValidator = new SupportedLengthValidator();

        expectedPatternRegexValidator.setNextHandler(placeholderCountToSupportedLengthValidator);
        placeholderCountToSupportedLengthValidator.setNextHandler(prefixLengthValidator);
        prefixLengthValidator.setNextHandler(supportedLengthValidator);

        return expectedPatternRegexValidator;
    }
}
