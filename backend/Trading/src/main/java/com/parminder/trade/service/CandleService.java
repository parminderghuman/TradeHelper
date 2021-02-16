package com.parminder.trade.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.h2.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mysql.cj.x.protobuf.MysqlxCrud.Limit;
import com.parminder.trade.TradingApplication;
import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.CandleDataMinuteKey;
import com.parminder.trade.bo.DailyCandleData;
import com.parminder.trade.bo.Instrument;
import com.parminder.trade.bo.WorkingDays;
import com.parminder.trade.dto.CandleResponse;
import com.parminder.trade.dto.UiCandle;
import com.parminder.trade.repository.CandleDataRepository;
import com.parminder.trade.repository.DailyCandleDataRepository;
import com.parminder.trade.repository.InstrumentRepository;
import com.parminder.trade.repository.WorkingDayRepository;
import com.parminder.trade.repository.custom.MinuteInstrumentDataCandleRepository;
import com.parminder.trade.utils.DateUtils;

@Service
public class CandleService {

	/*
	 * 
	 * select YEAR(complete_date) ,MONTH(complete_date),
	 * SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,
	 * MAX(hight), MIN(low) ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',
	 * close)), '_', -1) AS `close` from daily_candle_data where
	 * instrument_token=5633 group by YEAR(complete_date), MONTH(complete_date) ;
	 * 
	 * 
	 *
	 * 
	 * select MAX(complete_date) ,MONTH(complete_date),
	 * SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,
	 * MAX(hight), MIN(low) ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',
	 * close)), '_', -1) AS `close` , sum(volume) as volume , sum(open_interest) as
	 * interest from daily_candle_data where instrument_token=5633 group by
	 * WEEK(complete_date) ;
	 * 
	 * select MAX(complete_date) ,MONTH(complete_date),
	 * SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,
	 * MAX(hight), MIN(low) ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',
	 * close)), '_', -1) AS `close` , sum(volume) as volume , sum(open_interest) as
	 * interest from 98049inminute_candle_data group by
	 * FLOOR(day_minute_value/5),date,month,year limit 100
	 * 
	 */
	@Autowired
	KiteService kiteService;

	@Autowired
	WorkingDayRepository workingDayRepository;

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CandleDataRepository candleDataRepository;
	@Autowired
	DailyCandleDataRepository dailyCandleDataRepository;

	@Autowired
	MemoryService memoryService;

	@Autowired
	MinuteInstrumentDataCandleRepository minuteInstrumentDataCandleRepository;

	public void validateInstruments() {
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		for (Instrument i : instruments) {
			validateInstruments(i.getInstrumentToken());
			System.out.println("done" + (instruments.indexOf(i) + 1) + " of " + instruments.size());
		}
	}

	public void validateInstruments(long instrumentToekn) {
		Instrument instrument = instrumentRepository.findByInstrumentToken(instrumentToekn);
		Page<DailyCandleData> listPage = dailyCandleDataRepository.findByCandleDataDailyKeyInstrumentTokenAndVerified(
				instrumentToekn, false,
				PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "candleDataDailyKeyCompleteDate")));
		for (DailyCandleData dc : listPage) {
			Date startDate = new Date(dc.getCompleteDate().getTime());
			Date endDate = new Date(dc.getCompleteDate().getTime());
			startDate.setHours(00);
			startDate.setMinutes(00);
			startDate.setSeconds(00);
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);

			long count = minuteInstrumentDataCandleRepository
					.countByCandleDataMinuteKeyInstrumentTokenAndCandleDataMinuteKeyCompleteDateLessThanEqualAndCandleDataMinuteKeyCompleteDateGreaterThanEqual(
							instrumentToekn, endDate, startDate);
			if (count == 375) {
				dc.setVerified(true);
				dailyCandleDataRepository.save(dc);
			} else if (count == 0) {
				try {
					loadMinuteData(instrument, dc.getCompleteDate());
					count = minuteInstrumentDataCandleRepository
							.countByCandleDataMinuteKeyInstrumentTokenAndCandleDataMinuteKeyCompleteDateLessThanEqualAndCandleDataMinuteKeyCompleteDateGreaterThanEqual(
									instrumentToekn, endDate, startDate);
					if (count == 375) {
						dc.setVerified(true);
						dailyCandleDataRepository.save(dc);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
//				long deleteCount = candleDataRepository
//						.deleteByInstrumentTokenAndCompleteDateLessThanEqualAndCompleteDateGreaterThanEqual(instrumentToekn,
//								endDate, startDate);
//				try {
//					loadMinuteData(instrument, dc.getCompleteDate());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			System.out.println(startDate + " " + endDate + " " + count + " " + instrument.getTradingSymbol());

		}
		System.out.println("done" + instrument.getTradingSymbol());
	}

	private void loadMinuteData(Instrument instrument, Date date) throws Exception {
		List<MinuteCandleData> cds = new ArrayList<MinuteCandleData>();
		String from = DateUtils.parseDayFormate(date);
		String to = DateUtils.parseDayFormate(date);
		CandleResponse cr = kiteService.getHistoryData(instrument.getInstrumentToken(), from, to, "minute");
		for (List<String> candle : cr.getData().get("candles")) {
			String timestamp = candle.get(0);
			MinuteCandleData cd = new MinuteCandleData(instrument, candle);
			cds.add(cd);
		}
		minuteInstrumentDataCandleRepository.saveAll(cds, instrument.getInstrumentToken(), true);

	}

	public void loadInstrumentsDaily() throws Exception {
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		for (Instrument i : instruments) {
			loadInstrumentsDaily(i.getInstrumentToken());
			System.out.println("done" + (instruments.indexOf(i) + 1) + " of " + instruments.size());
		}
	}

	public void loadInstrumentsDaily(long instrumentToekn) throws Exception {
		Long count = workingDayRepository.count();
		Instrument instrument = instrumentRepository.findByInstrumentToken(instrumentToekn);
		System.out.println(instrument);
		Page<DailyCandleData> lDCDPage = dailyCandleDataRepository.findByCandleDataDailyKeyInstrumentToken(
				instrumentToekn, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "candleDataDailyKeyCompleteDate")));

		Page<WorkingDays> p = workingDayRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "date")));
		WorkingDays d = p.getContent().get(0);
		System.out.println(d.getDate());
		Date fromDate = d.getDate();
		List<DailyCandleData> dcd = new ArrayList<DailyCandleData>();
		if (lDCDPage.getNumberOfElements() > 0) {
			DailyCandleData lDCD = lDCDPage.getContent().get(0);
			fromDate = lDCD.getCompleteDate();

		}
		while (true) {
			boolean isbreak = false;
			System.out.println(fromDate);
			String from = DateUtils.parseDayFormate(fromDate);

			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.add(Calendar.YEAR, 05);
			fromDate = c.getTime();

			System.out.println(fromDate);
			String to = DateUtils.parseDayFormate(fromDate);

			if (fromDate.getTime() > new Date().getTime()) {
				to = DateUtils.parseDayFormate(new Date());
				isbreak = true;
			}

			System.out.println(from + " : " + to);

			CandleResponse cr = kiteService.getHistoryData(instrument.getInstrumentToken(), from, to, "day");
			for (List<String> candle : cr.getData().get("candles")) {
				String timestamp = candle.get(0);
				DailyCandleData cd = new DailyCandleData(instrument, candle);

				dcd.add(cd);
			}
			if (isbreak) {
				break;
			}

		}
		dailyCandleDataRepository.saveAll(dcd);
	}

	public void loadInstrumentsMinute() throws Exception {
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		for (Instrument i : instruments) {
			loadInstrumentsMinute(i.getInstrumentToken());
			System.out.println("done" + (instruments.indexOf(i) + 1) + " of " + instruments.size());
		}
	}

// fix data
	public void loadInstrumentsMinute(long instrumentToekn) throws Exception {
		Long count = workingDayRepository.count();
		Instrument instrument = instrumentRepository.findByInstrumentToken(instrumentToekn);
		System.out.println(instrument);
//		Page<MinuteCandleData> lDCDPage = candleDataRepository.findByCandleDataMinuteKeyInstrumentToken(instrumentToekn,
//				PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "candleDataMinuteKeyCompleteDate")));
		minuteInstrumentDataCandleRepository.createTableIfNotExist(instrumentToekn);
		Date date = minuteInstrumentDataCandleRepository.findLargestDate(instrumentToekn);
		Page<WorkingDays> p = workingDayRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "date")));
		Date startDate = new Date();
		startDate.setDate(0);
		startDate.setMonth(0);
		startDate.setYear(2018 - 1900);
		WorkingDays d = new WorkingDays(startDate);
		System.out.println(d.getDate());
		Date fromDate = d.getDate();
		if (date != null) {
			System.out.println("prevoisu date " + date);
			fromDate = date;

		}
		while (true) {
			List<MinuteCandleData> dcd = new ArrayList<MinuteCandleData>();
			boolean isbreak = false;
			System.out.println(fromDate);
			String from = DateUtils.parseDayFormate(fromDate);
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.add(Calendar.MONTH, 02);
			c.add(Calendar.DAY_OF_YEAR, -2);
			fromDate = c.getTime();

			System.out.println(fromDate);
			String to = DateUtils.parseDayFormate(fromDate);

			if (fromDate.getTime() > new Date().getTime()) {
				to = DateUtils.parseDayFormate(new Date());
				isbreak = true;
			}

			System.out.println(from + " : " + to);
			CandleResponse cr = kiteService.getHistoryData(instrument.getInstrumentToken(), from, to, "minute");
			File file = new File("/home/parminder/Desktop/backup/trade/" + instrumentToekn);
			if (!file.isDirectory() || !file.exists()) {
				file.mkdirs();
			}
			// WriteObjectToFile(cr, "/home/parminder/Desktop/backup/trade/" +
			// instrumentToekn + "/" + from);
			for (List<String> candle : cr.getData().get("candles")) {
				String timestamp = candle.get(0);
				MinuteCandleData cd = new MinuteCandleData(instrument, candle);

				dcd.add(cd);

			}
			minuteInstrumentDataCandleRepository.saveAll(dcd, instrumentToekn, true);
			if (isbreak) {
				break;
			}

		}
		// candleDataRepository.saveAll(dcd);
		// saveAll(dcd);

	}

	public void readList() throws SQLException {
		File f = new File("/home/parminder/Desktop/backup/trade/");
		int i = 0;
		for (File mf : f.listFiles()) {
			System.out.println("processing file" + mf.getName());
			if (mf.listFiles() != null) {
				for (File ff : mf.listFiles()) {
					if (ff.getName().contains("done")) {
						System.out.println("skipping file" + ff.getName());
						i++;
						continue;
					}
					Long instrumentToken = Long.parseLong(mf.getName());
					Instrument instrument = instrumentRepository.findByInstrumentToken(instrumentToken);
					System.out.println("insrument file" + instrument);
					CandleResponse cr = (CandleResponse) ReadObjectFromFile(ff);
					List<MinuteCandleData> mcd = new ArrayList<MinuteCandleData>();
					for (List<String> candle : cr.getData().get("candles")) {
						String timestamp = candle.get(0);
						MinuteCandleData cd;
						try {
							cd = new MinuteCandleData(instrument, candle);
							mcd.add(cd);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					minuteInstrumentDataCandleRepository.saveAll(mcd, instrumentToken, false);
					System.out.println("processing done" + ff.getName() + "  saved " + mcd.size());
					if (ff.renameTo(new File("/home/parminder/Desktop/backup/trade/" + ff.getName() + "-done"))) {
						System.out.println("file reanmed " + ff.getName() + "-done");
					} else {
						System.out.println("file reanmed error" + ff.getName() + "-done");

					}
				}
			}
			System.gc();
			System.out.println("processing done" + mf.getName() + " done" + (i++) + " / " + f.listFiles().length);
		}
	}

	public void WriteObjectToFile(Object serObj, String filepath) {

		try {

			FileOutputStream fileOut = new FileOutputStream(filepath);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(serObj);
			objectOut.close();
			System.out.println("The Object  was succesfully written to a file");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object ReadObjectFromFile(File filepath) {

		try {

			FileInputStream fileOut = new FileInputStream(filepath);
			ObjectInputStream objectIn = new ObjectInputStream(fileOut);
			Object obj = objectIn.readObject();

			System.out.println("The Object has been read from the file");
			objectIn.close();
			return obj;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public List<UiCandle> fetchMonthlyData(Long instrumenToken, Date startDate, Date endDate) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date,  MIN(complete_date) as minDate,  SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ? and complete_date <= ? group by YEAR(complete_date), MONTH(complete_date) ",
				new Object[] { instrumenToken, startDate, endDate });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}
	public List<UiCandle> fetchMonthDataByCompleteDateEqaulAndGraeterThanLimit(Long instrumentToken, Date startDate,
			int i) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date,  MIN(complete_date) as minDate,  SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ?  group by YEAR(complete_date), MONTH(complete_date) limit ?",
				new Object[] { instrumentToken, startDate, i });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}

	public List<UiCandle> fetchWeeklyData(Long instrumenToken, Date startDate, Date endDate) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date,  MIN(complete_date) as minDate,  SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ? and complete_date <= ? group by YEARWEEK(complete_date)",
				new Object[] { instrumenToken, startDate, endDate });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}
	public List<UiCandle> fetchWeeklyDataByCompleteDateEqaulAndGraeterThanLimit(Long instrumenToken, Date startDate, int limit) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date, MIN(complete_date) as minDate,  SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ?  group by YEARWEEK(complete_date) limit ?",
				new Object[] { instrumenToken, startDate, limit });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}

	public List<UiCandle> getIntsrumentsQuartly(Long instrumenToken, Date startDate, Date endDate) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date, MIN(complete_date) as minDate,   SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ? and complete_date <= ? group by YEAR(complete_date), QUARTER(complete_date) ",
				new Object[] { instrumenToken, startDate, endDate });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}
	public List<UiCandle> fetchQuarterDataByCompleteDateEqaulAndGraeterThanLimit(Long instrumentToken, Date startDate, int i) {
		// TODO Auto-generated method stub
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date, MIN(complete_date) as minDate,   SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ? group by YEAR(complete_date), QUARTER(complete_date) limit ?",
				new Object[] { instrumentToken, startDate, i });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;
	}

	public List<UiCandle> getIntsrumentsYear(Long instrumenToken, Date startDate, Date endDate) {
		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date,  MIN(complete_date) as minDate,  SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`, MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_', close)), '_', -1) AS `close`, sum(volume) as volume , sum(open_interest) as interest  from daily_candle_data where instrument_token=? and complete_date >= ? and complete_date <= ? group by YEAR(complete_date)",
				new Object[] { instrumenToken, startDate, endDate });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}

		return uiCandles;

	}

	public List<UiCandle> getIntsrumentsDaily(Long instrumenToken, Date startDate, Date endDate) {

		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select complete_date as date,  complete_date as minDate, open AS `open`, hight as hight, low as low ,close AS `close`, volume as volume , open_interest as interest  from daily_candle_data where instrument_token=? and complete_date >= ? and complete_date <= ? ",
				new Object[] { instrumenToken, startDate, endDate });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}
		if (DateUtils.isDateAreOnSameDay(endDate, new Date())) {
			List<UiCandle> meCandles = memoryService.getData(instrumenToken, 500);
			if(meCandles != null && meCandles.size() > 0) {
//				if(lastCandle != null) {
//					if(lastCandle.getDate().getTime() == meCandles.get(0).getDate().getTime()) {
//						lastCandle.get
//					}
//				}
				uiCandles.addAll(meCandles);
			}
			
		}

		return uiCandles;

	}

	public List<UiCandle> fetchDayDataByCompleteDateEqaulAndGraeterThanLimit(Long instrumentToken, Date startDate,
			int i) {

		List<UiCandle> uiCandles = new ArrayList<UiCandle>();

		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select complete_date as date,   complete_date as minDate, open AS `open`, hight as hight, low as low ,close AS `close`, volume as volume , open_interest as interest  from daily_candle_data where instrument_token=? and complete_date >= ?  limit ?",
				new Object[] { instrumentToken, startDate, i });
		for (Map row : rows) {
			uiCandles.add(new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), (Date) row.get("date"),(Date) row.get("minDate")));
		}
	

		return uiCandles;

	}

	public List<UiCandle> fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(int minute, Long instrumentToken,
			Date startDate, int limit) {

		List<UiCandle> uiCandles = new ArrayList<UiCandle>();
		UiCandle lastCandle = null;
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date ,  MIN(complete_date) as minDate,   SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,   MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',   close)), '_', -1) AS `close` , sum(volume) as volume , sum(open_interest) as   interest from "
						+ instrumentToken + "inminute_candle_data "
						+ " where  complete_date >= ?  group by  FLOOR(day_minute_value/" + minute
						+ "),DATE(complete_date) limit ?",
				new Object[] { startDate, limit });
		for (Map row : rows) {
			System.out.println(row.get("date"));
			System.out.println(new Date(((Timestamp) row.get("date")).getTime()));
			UiCandle candle = new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), new Date(((Timestamp) row.get("date")).getTime()),new Date(((Timestamp) row.get("minDate")).getTime())); 
			uiCandles.add(candle);
			lastCandle = candle;
		}
		if(uiCandles.size() <limit) {
		
			List<UiCandle> meCandles = memoryService.getData(instrumentToken, minute);
			if(meCandles != null && meCandles.size() > 0) {
//				if(lastCandle != null) {
//					if(lastCandle.getDate().getTime() == meCandles.get(0).getDate().getTime()) {
//						lastCandle.get
//					}
//				}
				uiCandles.addAll(meCandles);
			}
		}
		

		return uiCandles;

	}

	
	public List<UiCandle> getIntsrumentsMintes(int minute, Long instrumenToken, Date startDate, Date endDate) {

		List<UiCandle> uiCandles = new ArrayList<UiCandle>();
		UiCandle lastCandle = null;
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date ,  MIN(complete_date) as minDate,   SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,   MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',   close)), '_', -1) AS `close` , sum(volume) as volume , sum(open_interest) as   interest from "
						+ instrumenToken + "inminute_candle_data "
						+ " where  complete_date >= ? and complete_date <= ? group by  FLOOR(day_minute_value/" + minute
						+ "),DATE(complete_date)",
				new Object[] { startDate, endDate });
		for (Map row : rows) {
			System.out.println(row.get("date"));
			System.out.println(new Date(((Timestamp) row.get("date")).getTime()));
			UiCandle candle = new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), new Date(((Timestamp) row.get("date")).getTime()),new Date(((Timestamp) row.get("minDate")).getTime())); 
			uiCandles.add(candle);
			lastCandle = candle;
		}
		if (DateUtils.isDateAreOnSameDay(endDate, new Date())) {
			List<UiCandle> meCandles = memoryService.getData(instrumenToken, minute);
			if(meCandles != null && meCandles.size() > 0) {
//				if(lastCandle != null) {
//					if(lastCandle.getDate().getTime() == meCandles.get(0).getDate().getTime()) {
//						lastCandle.get
//					}
//				}
				uiCandles.addAll(meCandles);
			}
			
		}

		return uiCandles;

	}
	public List<UiCandle>  fetchMinuteDataByCompleteDateEqaulAndLessThanLimit(int minute, Long instrumentToken, Date endDate,
			int limit) {

		List<UiCandle> uiCandles = new ArrayList<UiCandle>();
		UiCandle lastCandle = null;
		List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(
				"select MAX(complete_date) as date ,  MIN(complete_date) as minDate,   SUBSTRING_INDEX(MIN(CONCAT(`complete_date`, '_', open)), '_', -1) AS `open`,   MAX(hight) as hight, MIN(low) as low ,SUBSTRING_INDEX(MAX(CONCAT(`complete_date`, '_',   close)), '_', -1) AS `close` , sum(volume) as volume , sum(open_interest) as   interest from "
						+ instrumentToken + "inminute_candle_data "
						+ " where  complete_date <= ?  group by  FLOOR(day_minute_value/" + minute
						+ "),DATE(complete_date) limit ?",
				new Object[] { endDate, limit });
		for (Map row : rows) {
			System.out.println(row.get("date"));
			System.out.println(new Date(((Timestamp) row.get("date")).getTime()));
			UiCandle candle = new UiCandle(Double.parseDouble(row.get("open").toString()),
					Double.parseDouble(row.get("hight").toString()), Double.parseDouble(row.get("close").toString()),
					Double.parseDouble(row.get("low").toString()), Long.parseLong(row.get("volume").toString()),
					Long.parseLong(row.get("interest").toString()), new Date(((Timestamp) row.get("date")).getTime()),new Date(((Timestamp) row.get("minDate")).getTime())); 
			uiCandles.add(candle);
			lastCandle = candle;
		}
		return uiCandles;

	}
	
	private void saveAll(List<DailyCandleData> candles) {
		int[] dp = this.jdbcTemplate.batchUpdate(
				"insert into daily_candle_data (complete_date,exchange,symbol,instrument_token,hight,open,close,low,year,month,date,hour,minute,day_minute_value,open_interest,volume) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE hight = VALUES(hight),open = VALUES(open),close = VALUES(close),low = VALUES(low),open_interest = VALUES(open_interest),volume = VALUES(volume)",
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						int j = 0;
						// System.out.println(new
						// Timestamp(candles.get(i).getCompleteDate().getTime()));
						ps.setTimestamp(++j, new Timestamp(candles.get(i).getCompleteDate().getTime()));
						ps.setString(++j, candles.get(i).getExchange());
						ps.setString(++j, candles.get(i).getSymbol());
						ps.setLong(++j, candles.get(i).getInstrumentToken());

						ps.setFloat(++j, candles.get(i).getHight());
						ps.setFloat(++j, candles.get(i).getOpen());
						ps.setFloat(++j, candles.get(i).getClose());
						ps.setFloat(++j, candles.get(i).getLow());

						ps.setInt(++j, candles.get(i).getYear());
						ps.setInt(++j, candles.get(i).getMonth());
						ps.setInt(++j, candles.get(i).getDate());
						ps.setInt(++j, candles.get(i).getHour());

						ps.setInt(++j, candles.get(i).getMinute());
						ps.setInt(++j, candles.get(i).getDayMinute());
						ps.setLong(++j, candles.get(i).getOpenInterest());
						ps.setLong(++j, candles.get(i).getVolume());

					}

					@Override
					public int getBatchSize() {
						// TODO Auto-generated method stub
						return candles.size();
					}
				});
		for (int i : dp) {
			// System.out.print(i+ " ,");
		}
		System.out.println();

	}

	

	


	

}
