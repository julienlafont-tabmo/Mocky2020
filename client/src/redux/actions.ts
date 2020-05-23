import { TRIGGER_ERROR, RESET_ERROR } from './actionsTypes';

export const resetError = () => ({
  type: RESET_ERROR,
});

export const triggerError = (message: String) => ({
  type: TRIGGER_ERROR,
  payload: { message },
});
