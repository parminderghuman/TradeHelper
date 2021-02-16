
import React, { useEffect, useState, createRef, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { InstrumentActions } from '../../actions/instrumentAction';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import { Button, Container, Fab, Slider } from '@material-ui/core';
import { useParams } from 'react-router-dom';
import ToolTip from './tooltip';
import { orange, grey } from '@material-ui/core/colors';
import AddIcon from '@material-ui/icons/Add';
import RemoveIcon from '@material-ui/icons/Remove';
import Icon from '@material-ui/core/Icon';

function CandleView(props) {
    var [values, setValues] = useState({ open: 0, close: 0, high: 0, low: 0, date: 0, display: "none",minDate:0 })
    var [point, setPoint] = useState({ x: 0, y: 0 })
    var [scroll, setScroll] = useState({ x: -100, y: -100 })
    var [rationWidth, setRationWidth] = useState(50)
    var [rationHeight, setRationHeight] = useState(0)
    var [isScroll, setIsScroll] = useState(false)

var mainDiv = useRef()

    var low = 999999999;
    var high = 0;
    props.candles.forEach(element => {
        if (low > element.low) {
            low = element.low
        }
        if (high < element.high) {
            high = element.high
        }

    });
   var varableH = rationHeight*props.height/100;
   if(isNaN(varableH)){
       varableH = 0;
   }
    var heightOfAraea = props.height - 100 +varableH;//+(ratioHight*(props.height/100)) ;
    var widthOfAraea = props.width - 100;
    var diff = high - low;
    var ratioHight = heightOfAraea / (diff)
   // var rationWidth = 50;// parseInt((widthOfAraea) / props.candles.length);
    widthOfAraea = rationWidth * props.candles.length;
    var priceDiif = parseInt(heightOfAraea / 50);
    let priceArray = [];
    for (let i = 0; i <= priceDiif; i++) {
        priceArray.push(i)
    }
    if(mainDiv &&  mainDiv.current !=null && widthOfAraea>100 && !isScroll){
        mainDiv.current.scrollTo(widthOfAraea,0)
        setIsScroll(true)

    }

    const getCanndleColor = (elm,b=true) => {
        if (elm.upCandle) {
            if (elm.excited ) {
                
                return "#62a711"
            } else {
                
                return b==true ? "#6bf76b":"black";
            }

        } else {
            if (elm.excited ) {
                return "#d02c20"
            } else {
                return b==true ? "#e96b6b":"black";
            }
        }
    }
    const svgWidth = (rationWidth * props.candles.length) + 100

    return (
        <div style={{position:"relative"}}>
              <Slider
        defaultValue={rationWidth}
        
        aria-labelledby="discrete-slider"
        valueLabelDisplay="auto"
        
        marks
        min={5}
        onChange={(e,number)=>{
            setRationWidth(number)
        }}
        max={110}
        style={{width:300,position:"absolute",top:10,left:((props.width/2-150))}}
      />
         <Slider
        defaultValue={rationHeight}
        orientation="vertical"

        aria-labelledby="discrete-slider"
        valueLabelDisplay="auto"
        
        marks
        min={-100}
        onChange={(e,number)=>{
            setRationHeight(number)
        }}
        max={1000}
        style={{height:300,position:"absolute",top:((props.height/2-200)),left:-20}}
      />
        <div style={{ border: "2px grey solid", padding: 10, paddingBottom: 20, width: props.width + 20, height: props.height + 20 , overflow: "auto" }} 
            onScroll={(e) => {
              //  console.log(e)
                setScroll({x:e.target.scrollLeft - 100,y:e.target.scrollTop});
            }}
            ref={mainDiv}
        >
            <ToolTip {...values}></ToolTip>
            <div>
          
       
       </div>
            <svg width={svgWidth} height={heightOfAraea+100} style={{ background: "" }}
                onMouseMove={(e) => {
                    try {


                        setPoint({ x: e.nativeEvent.offsetX, y: e.nativeEvent.offsetY })
                        let elm = props.candles[parseInt(e.nativeEvent.offsetX / (widthOfAraea / props.candles.length))];

                        setValues({ open: elm.open, close: elm.close, high: elm.high, low: elm.low, date: elm.date, display: "",minDate:elm.minDate })
                    } catch (error) {
                        setValues({ open: 0, close: 0, high: 0, low: 0, date: 0, display: "none" })

                    }
                }}

                onMouseLeave={() => {
                    setPoint({ x: 0, y: 0 })
                    setValues({ open: 0, close: 0, high: 0, low: 0, date: 0, display: "none" })

                }}
            >



                {// candles 

                
                props.candles.map((elm, i) => {
                    let dl = new Date(elm.date);

                    return <> <rect width={rationWidth}
                        height={heightOfAraea}
                        x={i * rationWidth} y={0} style={{ fill: "rgb(255,255,255)", strokeWidth: .5, stroke: "rgb(0,0,0,.1)" }}
                        onMouseEnter={(e) => {
                            //    setValues({ open: elm.open, close: elm.close, high: elm.high, low: elm.low, date: elm.date, display: "" })
                        }}

                        onMouseLeave={() => {
                            //     setValues({ open: 0, close: 0, high: 0, low: 0, date: 0, display: "none" })

                        }}

                      
                    >


                    </rect>
                        <rect width={rationWidth - 2}
                            height={elm.open < elm.close ? (elm.close - elm.open) * ratioHight : (elm.open - elm.close) * ratioHight}
                            x={i * rationWidth} y={elm.open < elm.close ? (high - elm.close) * ratioHight : (high - elm.open) * ratioHight}
                            style={{ fill: getCanndleColor(elm), strokeWidth: .5, stroke: "rgb(0,0,0,.1)" }}
                        />
                        <rect width={1}
                            height={(elm.high - elm.low) * ratioHight}
                            x={i * rationWidth + ((rationWidth - 4) / 2)}
                            y={(high - elm.high) * ratioHight}
                            style={{ fill: getCanndleColor(elm,false), strokeWidth: .5, stroke: "rgb(0,0,0,.1)" }}
                        />
                        {
                            (i % parseInt(50 / rationWidth)) == 0 ? <line x1={i * rationWidth + ((rationWidth - 4) / 2)} y1={props.height-100+scroll.y} x2={i * rationWidth + ((rationWidth - 4) / 2)} y2={props.height-100+scroll.y + 5} style={{ stroke: "grey", strokeWidth: 3 }} />
                                : <></>
                        }
                        {

                            (i % parseInt(50 / rationWidth)) == 0 ? <text x={i * rationWidth + ((rationWidth - 4) / 2)} y={props.height-100+scroll.y-15} transform={"rotate(90 " + ((i * rationWidth + ((rationWidth - 4) / 2)) - 20) + "," + (props.height -100+scroll.y)+ ")"} style={{ fontWeight: "bold", fill: "grey" }}>{elm.date}</text>
                                : <></>
                        }
                    </>

                })}

                <line x1={0} y1={0} x2={0} y2={heightOfAraea} style={{ stroke: "grey", strokeWidth: 3 }} />

                <line x1={0} y1={props.height-100+scroll.y} x2={widthOfAraea} y2={props.height-100+scroll.y} style={{ stroke: "grey", strokeWidth: 3 }} />

                <rect width={100} height={props.height} x={props.width + scroll.x}
                    y={0}
                    style={{ fill: "#ffffff" }}
                ></rect>
                {priceArray.map((val, i) => {

                    return <>
                        <line x1={props.width + scroll.x} y1={val * (heightOfAraea / priceDiif)} x2={props.width + scroll.x + 5} y2={val * (heightOfAraea / priceDiif)} style={{ stroke: "grey", strokeWidth: 1 }} />
                        <text x={props.width + scroll.x+ 10} y={(val * (heightOfAraea / priceDiif)) + 5} style={{ fontWeight: "bold", fill: "grey" }}>{parseFloat(high - ((high - low) / priceDiif) * val).toFixed(2)}</text>
                    </>


                })

                }


                <line x1={props.width + scroll.x} y1={heightOfAraea} x2={props.width + scroll.x} y2={0} style={{ stroke: "grey", strokeWidth: 3 }} />

                <line x1={0} y1={point.y} x2={widthOfAraea} y2={point.y} style={{ stroke: "grey", strokeWidth: 1 }} ></line>
                <line x1={point.x} y1={0} x2={point.x} y2={heightOfAraea} style={{ stroke: "grey", strokeWidth: 1 }} ></line>
                <line x1={widthOfAraea} y1={point.y} x2={widthOfAraea + 5} y2={point.y} style={{ stroke: "black", strokeWidth: 1 }} ></line>
                <rect x={props.width + scroll.x + 5} y={point.y - 10} height={20} width={60} />
                <text x={props.width + scroll.x + 10} y={point.y + 5} style={{ fill: "white", strokeColor: "pink" }}>{parseFloat(high - ((high - low) / heightOfAraea * point.y), 2).toFixed(2)}</text>

            </svg>
        </div>
        </div>
        /*
       <div style={{height:props.height,width:props.width, background:"",position:"relative",paddingTop:5,paddingLeft:5,border:"1px solid black"}}>
         
           {props.candles.map((elm,i)=>{
               return<div style={{position:"absolute",
               background:"",
               width:rationWidth,
               height:props.height,
               left:i*rationWidth}}
               onMouseEnter={(e)=>{
                   setValues({open:elm.open,close:elm.close,high:elm.high,low:elm.low,date:elm.date,display:""})
               }}
     
               onMouseLeave={()=>{
                 setValues({open:0,close:0,high:0,low:0,date:0,display:"none"})
     
               }}
               >
                    <div style={{position:"absolute",
               background: elm.open<elm.close  ? "green":"red",
               width:rationWidth-2,
               height: elm.open<elm.close ? (elm.close-elm.open)*ratioHight: (elm.open-elm.close)*ratioHight,
               minHeight:1,
               top:elm.open<elm.close ? (high-elm.close)*ratioHight : (high-elm.open)*ratioHight}}>
     
               </div>
               <div style={{position:"absolute",
               background:elm.open<elm.close  ? "green":"red",
               width:2,
               height: (elm.high-elm.low)*ratioHight,
               top:(high-elm.high)*ratioHight,
               marginLeft:(rationWidth-4)/2
               }}>
     
               </div>
               </div>
           })}
       </div>
       */

    );

}

export default CandleView;