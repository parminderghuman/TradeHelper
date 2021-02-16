package com.parminder.trade.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parminder.trade.dto.UiCandle;
import com.parminder.trade.service.CandleService;

@RestController
@RequestMapping(path = "candle")
@CrossOrigin(origins = {"*"})
public class CandleController {

	@Autowired
	CandleService candleService;
	@RequestMapping(path = "/month/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsMonthly(@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		
		return candleService.fetchMonthlyData(instrumentToekn, new Date(startDate),new Date( endDate));
		
	}
	
	
	@RequestMapping(path = "/quarter/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsQuartly(@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		return candleService.getIntsrumentsQuartly(instrumentToekn, new Date(startDate),new Date( endDate));
		
	}
	
	@RequestMapping(path = "/year/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsYear(@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		return candleService.getIntsrumentsYear(instrumentToekn, new Date(startDate),new Date( endDate));
		
	}
	
	@RequestMapping(path = "/week/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsWeekly(@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		return candleService.fetchWeeklyData(instrumentToekn, new Date(startDate),new Date( endDate));
	}
	@RequestMapping(path = "/daily/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsDaily(@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		return candleService.getIntsrumentsDaily(instrumentToekn, new Date(startDate),new Date( endDate));
	}
	@RequestMapping(path = "/minute/{minute}/{instrumentToekn}/{startDate}/{endDate}",method = RequestMethod.GET)
	public List<UiCandle> getIntsrumentsMinute(@PathVariable(name = "minute") int minute,@PathVariable(name = "instrumentToekn") Long instrumentToekn,@PathVariable(name = "startDate")Long startDate,@PathVariable(name = "endDate")Long endDate) throws Exception {
		return candleService.getIntsrumentsMintes(minute,instrumentToekn, new Date(startDate),new Date( endDate));
	}
	
}
