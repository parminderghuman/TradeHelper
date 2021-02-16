
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
import { Button, Container } from '@material-ui/core';
import { useParams } from 'react-router-dom';
function ToolTip(props) {
    var d = "";
    var md="";
    try {
         d = new Date(props.date);
         d= d.toLocaleDateString()+" "+d.toLocaleTimeString();
         md = new Date(props.minDate);
         md= md.toLocaleDateString()+" "+md.toLocaleTimeString();
    } catch (error) {
        
    }
return(
    <div style={{position:"absolute",background:"#000000aa",color:"white",display:props.display}}>
     <table>
         <tr> 
             <td>
             open :
             </td>
             <td>
             {props.open}
             </td>
             <td>
             high :
             </td>
             <td>
             {props.high}
             </td>
         </tr> 
         <tr> 
             <td>
             close :
             </td>
             <td>
             {props.close}
             </td>
             <td>
             low :
             </td>
             <td>
             {props.low}
             </td>
             </tr><tr> 
             <td>
             date :
             </td>
             <td>
             {d}
             </td>
            
         </tr>  <tr> 
             <td>
            m date :
             </td>
             <td>
             {md}
             </td>
            
         </tr>
     </table>
    
 </div>)
}

export default ToolTip;