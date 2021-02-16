package com.parminder.trade.tasks;

import java.net.http.HttpHeaders;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.parminder.trade.TradingApplication;
import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.Instrument;
import com.parminder.trade.bo.WorkingDays;
import com.parminder.trade.dto.CandleResponse;
import com.parminder.trade.repository.CandleDataRepository;
import com.parminder.trade.repository.InstrumentRepository;
import com.parminder.trade.repository.WorkingDayRepository;
import com.parminder.trade.service.CandleService;
import com.parminder.trade.service.WorkingDayService;
import com.parminder.trade.utils.DateUtils;

@Component
public class FetchDataForSymbol {

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	CandleDataRepository candleDataRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	WorkingDayRepository workingDayRepository;
	
	@Autowired
	WorkingDayService workingDayService;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	CandleService candleService;
	
	
	SimpleDateFormat simpleDateFormate = new SimpleDateFormat("yyyy-MM-dd");
	
	@Scheduled(cron = "0 57 17 * * *")
	public void syncDailyData() {
		try {
			candleService.loadInstrumentsDaily();
			candleService.loadInstrumentsMinute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
	


}
