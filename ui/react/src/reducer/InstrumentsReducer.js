let initialState = {
    instruments: [],
    isLoading: false,
    candles: []
}

export const START_LOADING = "InstrumentsReducer/START_LOADING";
export const END_LOADING = "InstrumentsReducer/END_LOADING";
export const CANDLES_DATA = "InstrumentsReducer/CANDLES_DATA";


export default function instrumentsReducer(state = initialState, action) {
    switch (action.type) {

        case START_LOADING:
            return { ...state, isLoading: true }

        case END_LOADING:
            return { ...state, instruments: action.payload, isLoading: false }
        case CANDLES_DATA:
            return { ...state, candles: action.payload, isLoading: false }
        default:
            return state;
    }

}
