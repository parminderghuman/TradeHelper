package com.parminder.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parminder.trade.bo.Instrument;
import com.parminder.trade.service.CandleService;
import com.parminder.trade.service.InstrumentService;

@RestController
@RequestMapping(path = "instruments")
@CrossOrigin(origins = {"*"})
public class InstrumentController {
	@Autowired
	InstrumentService instrumentService;
	

	@Autowired 
	CandleService candleService ;

	@RequestMapping(path = "/load",method = RequestMethod.GET)
	public void getIntsruments() {
		instrumentService.loadInstruments();
		
	}
	@RequestMapping(path = "/load/{instrumentToekn}",method = RequestMethod.GET)
	public void getIntsruments(@PathVariable(name = "instrumentToekn") Long instrumentToekn) throws Exception {
		candleService.loadInstrumentsDaily(instrumentToekn);
		
	}
	
	@RequestMapping(path = "/loadAll",method = RequestMethod.GET)
	public void getAllIntsruments() throws Exception {
		candleService.loadInstrumentsDaily();
		
	}
	
	
	@RequestMapping(path = "/loadAll/minute",method = RequestMethod.GET)
	public void getAllIntsrumentsMinute() throws Exception {
		candleService.loadInstrumentsMinute();
		
	}
	
	@RequestMapping(path = "/loadAll/saveminute",method = RequestMethod.GET)
	public void getAllIntsrumentsSaveMinute() throws Exception {
		candleService.readList();
		
	}
	
	@RequestMapping(path = "/validate/{instrumentToekn}",method = RequestMethod.GET)
	public void getValidate(@PathVariable(name = "instrumentToekn") Long instrumentToekn) throws Exception {
		candleService.validateInstruments(instrumentToekn);
		
	}
	@RequestMapping(path = "/validateAll",method = RequestMethod.GET)
	public void getValidateAll() throws Exception {
		candleService.validateInstruments();
		
	}
	
	
	@RequestMapping(path = "/",method = RequestMethod.GET)
	public List<Instrument> get() {
		return instrumentService.get();
		
	}
	
	
}
