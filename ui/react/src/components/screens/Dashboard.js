
import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { InstrumentActions } from '../../actions/instrumentAction';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import { Button } from '@material-ui/core';
import { Link } from 'react-router-dom';
function Dashboard() {
  const dispatch = useDispatch();
  const instruments = useSelector(state => state.instruments.instruments)
  useEffect(() => {
    dispatch(InstrumentActions.load());
  }, []);

  const refreshWorkingDays = () => {
    dispatch(InstrumentActions.loadWorkingDays())
  }

  const loadDailyData = (token) => {
    dispatch(InstrumentActions.loadDailyData(token))

  }

  const loadAllInstrument = () => {
    dispatch(InstrumentActions.loadAllInstrument())

  }

  const validate = (token) => {
    dispatch(InstrumentActions.validateInstrument(token))

  }

  const validateAllInstrument = () => {
    dispatch(InstrumentActions.validateAllInstrument())

  }


  const viewDetal = () => {
    // /instrument/:instrumentToken
  }
  return (
    <div >
      <Button variant="contained" onClick={() => refreshWorkingDays()}>Refresh Working Days</Button>
      <Button variant="contained" onClick={() => loadAllInstrument()}>LoadAll</Button>
      <Button variant="contained" onClick={() => validateAllInstrument()}>Validate All</Button>

      <TableContainer component={Paper}>
        <Table aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell align="right">Instrument Token</TableCell>
              <TableCell align="right">Exchange</TableCell>
              <TableCell align="right">Lot Size</TableCell>
              <TableCell align="right">Last Verified Date</TableCell>
              <TableCell align="right">Load Daily Data </TableCell>

            </TableRow>
          </TableHead>
          <TableBody>
            {instruments.map((row) => (
              <TableRow key={row.instrumentToken}>
                <TableCell component="th" scope="row">
                  {row.tradingSymbol}
                </TableCell>
                <TableCell align="right">{row.instrumentToken}</TableCell>
                <TableCell align="right">{row.exchange}</TableCell>
                <TableCell align="right">{row.lotSize}</TableCell>
                <TableCell align="right">{row.lastVerifiedDate}</TableCell>
                <TableCell align="right">   
                   <Button variant="contained" onClick={() => loadDailyData(row.instrumentToken)}>Load Daily Data</Button>
                   <Button variant="contained" onClick={() => validate(row.instrumentToken)}>Load Daily Data</Button>

                  <Link to={"/instrument/" + row.instrumentToken} >Detail</Link>
                </TableCell>

              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default Dashboard;
