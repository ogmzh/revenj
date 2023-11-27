import {
  INT_MAX_VALUE,
  INT_MIN_VALUE,
} from '../../constants';
import { formatNumberToDecimals } from '../../util/Formatters/NumberFormatter';
import { Numeric, parseBigNum } from '../../util/NumberUtils/NumberUtils';
import { ValidateTrigger } from './interfaces';
import { validatorCreatorFactory } from './validatorCreatorFactory';

////////////////////////////////////////////////////////////////////////////////
// Type validators                                                            //
////////////////////////////////////////////////////////////////////////////////
export const isIntegerCreator =
  validatorCreatorFactory<void, Numeric, any, any>({
    getValidatorErrorMessage: (_, input) => !(Number(input) <= INT_MAX_VALUE && Number(input) >= INT_MIN_VALUE) ?
      `Must be a whole number between ${formatNumberToDecimals(INT_MIN_VALUE, 0)} and ${formatNumberToDecimals(INT_MAX_VALUE, 0)}` :
      'Must be a whole number',
    operator: () => (input) => {
      try {
        const aNumber = parseBigNum(input);
        // string can be of form 0.00 which is an integer, but the string value is written in a non-integer notation
        const isInteger = !aNumber.isNaN() && aNumber.isInteger() &&  Number(input) <= INT_MAX_VALUE && Number(input) >= INT_MIN_VALUE;
        if (typeof input === 'string') {
          return isInteger && /^(?:\+|-)?\d+$/.test(input);
        }

        return isInteger;
      } catch {
        return false;
      }
    },
    validate: ValidateTrigger.IfInputIsSet,
  });

export const isNumberCreator =
  validatorCreatorFactory<void, Numeric, any, any>({
    getValidatorErrorMessage: () => `Value must be a number.`,
    operator: () => (input) => {
      try {
        const aNumber = parseBigNum(input);
        return !aNumber.isNaN();
      } catch {
        return false;
      }
    },
    validate: ValidateTrigger.IfInputIsSet,
  });
