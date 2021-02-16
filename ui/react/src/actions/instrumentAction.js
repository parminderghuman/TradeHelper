const { instruentService } = require("../service/instrumentService");
const { END_LOADING, CANDLES_DATA } = require("../reducer/InstrumentsReducer");


const loadCandle = (time, instrumentToken, startDate, endDate) => {
    return dispatch => {
        let res = instruentService.loadCandle(time, instrumentToken, startDate, endDate).then(res => {
            console.log(res);
            dispatch({
                type: CANDLES_DATA, payload: res
            })
        }, error => {

        })

    }
}
const load = () => {
    return dispatch => {
        let res = instruentService.load().then(res => {
            console.log(res);
            dispatch({
                type: END_LOADING, payload: res
            })
        }, error => {

        })

    }

};
const loadWorkingDays = () => {
    return dispatch => {
        let res = instruentService.loadWorkingDays().then(res => {
            console.log(res);

        }, error => {

        })

    }
}
const loadDailyData = (token) => {
    return dispatch => {
        let res = instruentService.loadDailyData(token).then(res => {
            console.log(res);

        }, error => {

        })

    }
}
const loadAllInstrument = () => {
    return dispatch => {
        let res = instruentService.loadAllInstrument().then(res => {
            console.log(res);

        }, error => {

        })

    }
}
const validateAllInstrument = () => {
    return dispatch => {
        let res = instruentService.validateAllInstrument().then(res => {
            console.log(res);

        }, error => {

        })

    }
}

const validateInstrument = (token) => {
    return dispatch => {
        let res = instruentService.validateInstrument(token).then(res => {
            console.log(res);

        }, error => {

        })

    }
}
export const InstrumentActions = {
    load,
    loadWorkingDays,
    loadDailyData,
    loadAllInstrument,
    loadCandle,
    validateInstrument,
    validateAllInstrument
}