import { config } from "../config";

export const instruentService = {
    load,
    loadWorkingDays,
    loadDailyData,
    loadAllInstrument,
    loadCandle,
    validateInstrument,
    validateAllInstrument
}

function validateInstrument(token){

    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/instruments/validate/${token}`, requestOptions).then(handleResponse);

}

function validateAllInstrument(){

    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/instruments/validateAll`, requestOptions).then(handleResponse);

}
function loadCandle(time,instrumentToken,startDate,endDate){
    const requestOptions = {
        method: 'GET',
    };
    if(time.indexOf("min") != -1){
        time = time.substr(0,time.indexOf("min"));
        return fetch(`${config.API_URL}/candle/minute/${time}/${instrumentToken}/${startDate}/${endDate}`, requestOptions).then(handleResponse);

    }else{
        return fetch(`${config.API_URL}/candle/${time}/${instrumentToken}/${startDate}/${endDate}`, requestOptions).then(handleResponse);
    }

}

function loadAllInstrument(){

    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/instruments/loadAll`, requestOptions).then(handleResponse);

}

function loadDailyData(token){

    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/instruments/load/${token}`, requestOptions).then(handleResponse);

}

function load(){

    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/instruments/`, requestOptions).then(handleResponse);

}

function loadWorkingDays(){
    const requestOptions = {
        method: 'GET',
    };


return fetch(`${config.API_URL}/workingdays/load`, requestOptions).then(handleResponse);

}
function handleResponse(response) {
    return response.text().then(text => {
        const data = text && JSON.parse(text);
        if (!response.ok) {
            if (response.status === 401) {
                // auto logout if 401 response returned from api
             //   logout();
             //   location.reload(true);
            }

            const error = (data && data.message) || response.statusText;
            return Promise.reject(error);
        }

        return data;
    });
}