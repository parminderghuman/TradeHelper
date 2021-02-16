import React from 'react'
import { Route, HashRouter, Switch } from 'react-router-dom'
import Dashboard from './components/screens/Dashboard'

import ScrollToTop from './components/ScrollTop'
import InstrumentDetail from './components/screens/InstrumentDetail'

export default props => (
    <HashRouter>
      <ScrollToTop>
        <Switch>
          <Route exact path='/' component={ Dashboard } />
          <Route exact path='/instrument/:instrumentToken' component={ InstrumentDetail } />

        </Switch>
      </ScrollToTop>
    </HashRouter>
  )