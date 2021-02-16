package com.parminder.trade.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.parminder.trade.TradingApplication;
import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.Instrument;
import com.parminder.trade.bo.WorkingDays;
import com.parminder.trade.bo.WorkingDays;
import com.parminder.trade.dto.CandleResponse;
import com.parminder.trade.repository.CandleDataRepository;
import com.parminder.trade.repository.InstrumentRepository;
import com.parminder.trade.repository.WorkingDayRepository;
import com.parminder.trade.utils.DateUtils;

@Service
public class WorkingDayService {

	@Autowired
	WorkingDayRepository workingDayRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	CandleDataRepository candleDataRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	KiteService kiteService;
	
	public void loadWorkingDay() throws Exception {
		Date fromDate = new Date();
		fromDate.setYear(1999-1900);
		while (true) {
			boolean isbreak =false;
		
			
			System.out.println(fromDate   );
			String from = DateUtils.parseDayFormate(fromDate);
			
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.add(Calendar.YEAR, 05);
			fromDate = c.getTime();
			
			System.out.println(fromDate );
			String to = DateUtils.parseDayFormate(fromDate);

			if(fromDate.getTime() > new Date().getTime()) {
				 to = DateUtils.parseDayFormate(new Date());
				 isbreak = true;
			}
			
			System.out.println(from + " : " + to);

			
			CandleResponse cr = kiteService.getHistoryData(5633l, from, to, "day");

			for (List<String> candle : cr.getData().get("candles")) {
				String timestamp = candle.get(0);
				Date completeDate = DateUtils.parseCompletFormate(timestamp);

				WorkingDays days = new WorkingDays(completeDate);
				workingDayRepository.save(days);

			}
			if(isbreak) {
				break;
			}
		}

	}

	public Long getCount() {
		return workingDayRepository.count();
	}
	public void loadInsytuteHistory() {
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		ExecutorService executor = Executors.newFixedThreadPool(8);

		for (Instrument instrument : instruments) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					System.out.println(instrument.getInstrumentToken());
					syncInstitude(instrument);
				}
			});

		}
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void syncInstitude(Instrument instrument) {
		try {
			List<MinuteCandleData> candleDatas = new ArrayList<MinuteCandleData>();

			List<WorkingDays> WorkingDays = new ArrayList<WorkingDays>();
			if (instrument.getLastVerifiedDate() == null) {
				WorkingDays = workingDayRepository.findAll(Sort.by(Direction.ASC, "date"));

			}
			for (WorkingDays wd : WorkingDays) {
				if (instrument.getLastVerifiedDate() != null
						&& instrument.getLastVerifiedDate().getTime() > wd.getDate().getTime()) {
					continue;
				}
			

				String from = DateUtils.parseDayFormate(wd.getDate());

				String to = DateUtils.parseDayFormate(wd.getDate());

				System.out.println(instrument.getTradingSymbol() + " : " + from + " : " + to);
			
				CandleResponse cr = kiteService.getHistoryData(instrument.getInstrumentToken(), from, to, "minute");

				if (cr.getData().get("candles").size() == 0) {
					continue;
				}
				for (List<String> candle : cr.getData().get("candles")) {
					try {

						MinuteCandleData cd = new MinuteCandleData(instrument, candle);
						if (cd.getDayMinute() < 0) {
							continue;
						}
						candleDatas.add(cd);
					} catch (Exception e) {
						String timestamp = candle.get(0);
						System.out.println(timestamp + " " + timestamp.substring(0, timestamp.length() - 5));
						System.out.println(instrument.getTradingSymbol() + " : " + instrument.getInstrumentToken()
								+ " : " + candle);
						throw new Exception(e);
					}
				}
				batchUpdateCandles(candleDatas);
				Integer i = this.jdbcTemplate.queryForObject(
						"select count(*) from candle_data where instrument_token = ? and DATE(complete_date) = ? group by DATE(complete_date)",
						new Object[] { instrument.getInstrumentToken(), from }, Integer.class);
				if (i == 375) {
					instrument.setLastVerifiedDate(wd.getDate());
					instrumentRepository.save(instrument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public void batchUpdateCandles(List<MinuteCandleData> candles) {
		int[] dp = this.jdbcTemplate.batchUpdate(
				"insert into candle_data (complete_date,exchange,symbol,instrument_token,hight,open,close,low,year,month,date,hour,minute,day_minute_value,open_interest,volume) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE hight = VALUES(hight),open = VALUES(open),close = VALUES(close),low = VALUES(low),open_interest = VALUES(open_interest),volume = VALUES(volume)",
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
//			for(int i:dp) {
//				//System.out.print(i+ " ,");
//			}
//			System.out.println();

	}

	public void loadInsytuteHistory(String date, String to) {

		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		ExecutorService executor = Executors.newFixedThreadPool(8);

		for (Instrument instrument : instruments) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					System.out.println(instrument.getInstrumentToken());
					syncInstitude(instrument, date);
				}

				private void syncInstitude(Instrument instrument, String date) {

					try {
						List<MinuteCandleData> candleDatas = new ArrayList<MinuteCandleData>();
						Calendar calendar = Calendar.getInstance();
						Date month = DateUtils.parseDayFormate(date);
						month.setDate(1);
						String from = date;
						calendar.setTime(month);
						month.setDate(calendar.getMaximum(Calendar.DATE));
						System.out.println(instrument.getTradingSymbol() + " : " + from + " : " + to);
						
						CandleResponse cr = kiteService.getHistoryData(instrument.getInstrumentToken(), from, to, "minute");
						for (List<String> candle : cr.getData().get("candles")) {
							try {

								MinuteCandleData cd = new MinuteCandleData(instrument, candle);
								if (cd.getDayMinute() < 0) {
									continue;
								}
								candleDatas.add(cd);
							} catch (Exception e) {
								String timestamp = candle.get(0);
								System.out.println(timestamp + " " + timestamp.substring(0, timestamp.length() - 5));
								System.out.println(instrument.getTradingSymbol() + " : "
										+ instrument.getInstrumentToken() + " : " + candle);
								throw new Exception(e);
							}
						}
						candleDataRepository.saveAll(candleDatas);

					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}

				}

			});

		}
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
