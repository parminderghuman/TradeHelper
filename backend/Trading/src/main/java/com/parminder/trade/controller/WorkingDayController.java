package com.parminder.trade.controller;

import java.text.ParseException;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parminder.trade.service.InstrumentService;
import com.parminder.trade.service.WorkingDayService;

@RestController
@RequestMapping(value = "workingdays")
@CrossOrigin(origins = {"*"})
public class WorkingDayController {
	@Autowired
	WorkingDayService workingDayService;
	


	
	@RequestMapping(path = "/load",method = RequestMethod.GET)
	public Long loadWorkinDays() throws Exception {
		workingDayService.loadWorkingDay();
		return workingDayService.getCount();

	}
	
	@RequestMapping(path = "/count",method = RequestMethod.GET)
	public Long getCount() throws Exception {
		return workingDayService.getCount();
		
	}
	
	
	@RequestMapping(path = "/loadInsytuteHistory",method = RequestMethod.GET)
	public void loadInsytuteHistory() {
		workingDayService.loadInsytuteHistory();
	}

	
	@RequestMapping(path = "/loadInsytuteHistory/{date}/{to}",method = RequestMethod.GET)
	public void loadInsytuteHistory(@PathVariable(value = "date") String date,@PathVariable(value = "to") String to) {
		workingDayService.loadInsytuteHistory(date,to);
		return;
	}
}
