package com.parminder.trade.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parminder.trade.H2Jdbc;
import com.parminder.trade.bo.Zone;
import com.parminder.trade.bo.Zone.TimePeriod;
import com.parminder.trade.bo.ZoneCompositeKey;
import com.parminder.trade.service.ZoneService;

@RestController
@RequestMapping(value = "zones")
@CrossOrigin(origins = {"*"})
public class ZoneController {
	
	@Autowired
	ZoneService zoneService ;
	
	@Autowired
	H2Jdbc h2Jdbc ;
	@RequestMapping(path = "/load/{token}",method = RequestMethod.GET)
	public void loadWorkinDays(@PathVariable(name = "token") Long token) throws Exception {
		zoneService.detectZone(token,false);

	}
	
	@RequestMapping(path = "/loadall",method = RequestMethod.GET)
	public void loadAll() throws Exception {
		zoneService.detectZones(false);

	}
	@RequestMapping(path = "/loadallfirst",method = RequestMethod.GET)
	public void loadAllFirst() throws Exception {
		zoneService.detectZones(true);

	}
	@RequestMapping(path = "/createTable",method = RequestMethod.GET)
	public List<Zone> h2Jdbc() throws Exception {
		System.out.println("rest "+h2Jdbc.createTable());
		Zone zone = new Zone();
		zone.setZoneCompositeKey(new ZoneCompositeKey(12l, new Date(), TimePeriod.DAY));
		zone.setBaseCandleCount(1);
		zone.setBaseMaxDate(new Date());
		zone.setCreatedDate(new Date());
		zone.setDistalLine(7.8);
		zone.setEndDate(new Date());
		zone.setEntry(8.9);
		zone.setHightValue(9.9);
		zone.setLowValue(9.0);
		zone.setMaxTarget(9.8);
		
		zone.setStartDate(new Date());
		h2Jdbc.insert(zone);
		zone.setMaxTarget(19.8);
		h2Jdbc.insert(zone);
	return	h2Jdbc.select();
		
	}
}
