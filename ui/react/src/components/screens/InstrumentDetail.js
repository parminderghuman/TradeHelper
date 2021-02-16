
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { InstrumentActions } from '../../actions/instrumentAction';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import { Button, Container, TextField, Grid } from '@material-ui/core';
import { useParams } from 'react-router-dom';
import CandleView from '../candle/candle';
import Autocomplete from '@material-ui/lab/Autocomplete';

function InstrumentDetail() {
    const dispatch = useDispatch();
    const candles = useSelector(state => state.instruments.candles)
    const instruments = useSelector(state => state.instruments.instruments)
    const [time,setTime] = useState("week")
    const [instrumentToken,setInstrumentToken,] = useState()

const times=[
    "week","year","quarter","month","daily","1min","3min","5min","15min","25min","75min","125min"]
    let startDate="2019-01-01";
    let endDate ="2021-01-01"
    const params = useParams();


    useEffect(async() => {
        setInstrumentToken(params.instrumentToken)

       loadData(time,params.instrumentToken);
       dispatch(InstrumentActions.load());

       // dispatch(InstrumentActions.loadCandle("week",params.instrumentToken,startDate,endDate));
      }, []);

   
     
      const loadData =(time,instrumentToken)=>{
          let d = new Date();
          d.setHours(23)
          let endDate = d.getTime()
          if(time == "year"){
            d.setTime(d.getTime()-1000*60*60*24*30*30*30)

          }
          else if(time == "quarter"){
            
            d.setTime(d.getTime()-1000*60*60*24*30*30*5)
          }
          else if(time == "month"){
            
            d.setTime(d.getTime()-1000*60*60*24*30*60)
          }
        
          else if(time == "week"){
            
            d.setTime(d.getTime()-1000*60*60*24*30*21)
            d.setYear(2000)
          }
        
         else  if(time == "daily"){
            
            d.setTime(d.getTime()-1000*60*60*24*365)
          }else{
            d.setTime(d.getTime()-1000*60*60*24*10)

          }
          d.setHours(0)
          
          let   startDate = d.getTime();

        
        dispatch(InstrumentActions.loadCandle(time,instrumentToken,startDate,endDate));
      }
    // instruments.map(el=>{
    //     if(el.instrumentToken == params.instrumentToken){
    //         setInstrumentToken(el)
            
    //     }
    // })
  return(<Container style={{height:window.innerHeight-150,width:window.innerWidth-500,background:"",padding:0}}>
          <Grid container style={{padding:10}} spacing={2}>

      <Grid item direction={"column"}> <Autocomplete item
         value={time}
      id="combo-box-demo"
      options={times}
      onInputChange={(event, newInputValue) => {
           setTime(newInputValue)
            loadData(newInputValue,instrumentToken);
        
       
      }}
      getOptionLabel={(option) => option}
      style={{ width: 300 }}
      renderInput={(params) => <TextField {...params} label="time" variant="outlined" />}
    />
    </Grid>
    <Grid item direction={"column"}>
     <Autocomplete
     
         value={instrumentToken}
      id="combo-box-demo"
      options={instruments}
      onInputChange={(event, newInputValue) => {
        console.log(newInputValue,event)
      }}
      onChange={(event, newValue) => {
          try {
            console.log(newValue,event)
            loadData(time,newValue.instrumentToken)
          } catch (error) {
              
          }
      }}
      getOptionLabel={(option) => option.tradingSymbol}
      style={{ width: 300 }}
      renderInput={(params) => <TextField {...params} label="Script" variant="outlined" />}
    /></Grid>  
    </Grid>
      <CandleView height={window.innerHeight-200}  width={window.innerWidth-500} candles={candles}></CandleView>
  </Container>
    );
}

export default InstrumentDetail;