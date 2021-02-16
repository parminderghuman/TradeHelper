
import instrumentsReducer from './InstrumentsReducer'

export default function rootReducer(state = {}, action) {
  // always return a new object for the root state
  return {
    // the value of `state.todos` is whatever the todos reducer returns
    instruments: instrumentsReducer(state.instruments, action),
    // For both reducers, we only pass in their slice of the state
  }
}