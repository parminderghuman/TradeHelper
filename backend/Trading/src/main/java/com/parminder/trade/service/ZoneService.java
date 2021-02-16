package com.parminder.trade.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.parminder.trade.H2Jdbc;
import com.parminder.trade.bo.Instrument;
import com.parminder.trade.bo.Zone;
import com.parminder.trade.bo.Zone.TimePeriod;
import com.parminder.trade.bo.Zone.ZoneSide;
import com.parminder.trade.bo.Zone.ZoneType;
import com.parminder.trade.dto.UiCandle;
import com.parminder.trade.repository.InstrumentRepository;
import com.parminder.trade.repository.ZoneRepository;
import com.parminder.trade.utils.CandleUtils;
import com.parminder.trade.utils.DateUtils;

@Service
public class ZoneService {

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	CandleService candleService;

	@Autowired
	ZoneRepository zoneRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	H2Jdbc h2Jdbc;

	public void detectZones(boolean isFirstTime) {
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		ExecutorService executorService = Executors.newFixedThreadPool(12);
		instruments.forEach(in -> {
			executorService.execute(new DetectZoneRunnable(in, isFirstTime));
//			for(TimePeriod tp:  TimePeriod.values()) {
//				detectZoneNew(in, tp);
//
//			}
//			detectZoneNew(in, TimePeriod.MONTH);
//			detectZoneNew(in, TimePeriod.WEEK);
//			detectZoneNew(in, TimePeriod.DAY);

		});
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void detectZone(Long instrument, boolean isFirstTime) {

		detectZoneNew(instrumentRepository.findByInstrumentToken(instrument), TimePeriod.WEEK, isFirstTime);

	}

	public List<UiCandle> getCandles(Instrument instrument, TimePeriod tp, boolean isFirstTime) {
		Date startDate = DateUtils.getPastFirstDate();
		if (isFirstTime) {
			Page<Zone> pZone = zoneRepository.findAllByZoneCompositeKeyTimePeriodAndZoneCompositeKeyInstrumentToken(tp,
					instrument.getInstrumentToken(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "startDate")));

			if (pZone.getNumberOfElements() > 0) {
				Zone lastZone = pZone.get().findFirst().get();
				startDate = lastZone.getStartDate();
			}
		} else {
			Zone lastZone = h2Jdbc.findLastByZoneCompositeKeyTimePeriodAndZoneCompositeKeyInstrumentToken(tp,
					instrument.getInstrumentToken());
			if (lastZone != null) {
				startDate = lastZone.getStartDate();
			}
		}
		int limit = 10000;
		switch (tp) {
		case QUARTER:
			return candleService.fetchQuarterDataByCompleteDateEqaulAndGraeterThanLimit(instrument.getInstrumentToken(),
					startDate, limit);

		case MONTH:
			return candleService.fetchMonthDataByCompleteDateEqaulAndGraeterThanLimit(instrument.getInstrumentToken(),
					startDate, limit);

		case WEEK:
			return candleService.fetchWeeklyDataByCompleteDateEqaulAndGraeterThanLimit(instrument.getInstrumentToken(),
					startDate, limit);

		case DAY:
			return candleService.fetchDayDataByCompleteDateEqaulAndGraeterThanLimit(instrument.getInstrumentToken(),
					startDate, limit);

		case MIN_125:
			return candleService.fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(125,
					instrument.getInstrumentToken(), startDate, limit);

		case MIN_75:
			return candleService.fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(75,
					instrument.getInstrumentToken(), startDate, limit);

		case MIN_25:
			return candleService.fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(25,
					instrument.getInstrumentToken(), startDate, limit);

		case MIN_15:
			return candleService.fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(15,
					instrument.getInstrumentToken(), startDate, limit);

		case MIN_5:
			return candleService.fetchMinuteDataByCompleteDateEqaulAndGraeterThanLimit(5,
					instrument.getInstrumentToken(), startDate, limit);

		default:
			return new ArrayList<UiCandle>();

		}

	}

	public void detectZoneNew(Instrument instrument, TimePeriod tp, boolean isfirstTime) {
		List<UiCandle> uiCandles = getCandles(instrument, tp, isfirstTime);
		System.out.println(
				"startted " + instrument.getInstrumentToken() + " " + instrument.getName() + " " + tp.toString());

		// uiCandles = new ArrayList<UiCandle>();

		Boolean isUpLastCandle = null;
		Boolean isPreviousExcited = null;

		List<UiCandle> excitedCandlesBefore = new ArrayList<UiCandle>();
		List<UiCandle> excitedCandlesAfter = new ArrayList<UiCandle>();
		List<UiCandle> baseCandles = new ArrayList<UiCandle>();

		for (UiCandle candle : uiCandles) {
			boolean isExcited = CandleUtils.detectIsExcited(candle);
			boolean isUpCandle = CandleUtils.detectIsUpCandle(candle);
			if (isPreviousExcited != null && isUpLastCandle != null) {
				if (isExcited) {

					if (isPreviousExcited && isUpCandle != isUpLastCandle) {
						if (excitedCandlesAfter.size() > 0 && excitedCandlesBefore.size() > 0) {
							// zone end
							Zone zone = saveZone(excitedCandlesBefore, baseCandles, excitedCandlesAfter,
									instrument.getInstrumentToken(), tp, isfirstTime);
						}
						excitedCandlesBefore.clear();
						excitedCandlesBefore.addAll(excitedCandlesAfter);
						baseCandles.clear();
						// baseCandles.add(candle);
						excitedCandlesAfter.clear();

					} else {

					}
					excitedCandlesAfter.add(candle);
				} else {

					if (isPreviousExcited) {
						if (excitedCandlesAfter.size() > 0 && excitedCandlesBefore.size() > 0) {
							// zone end
							Zone zone = saveZone(excitedCandlesBefore, baseCandles, excitedCandlesAfter,
									instrument.getInstrumentToken(), tp, isfirstTime);
						}
						excitedCandlesBefore.clear();
						excitedCandlesBefore.addAll(excitedCandlesAfter);
						baseCandles.clear();
						baseCandles.add(candle);
						excitedCandlesAfter.clear();
					} else {
						baseCandles.add(candle);

					}
				}
			}
			isPreviousExcited = isExcited;
			isUpLastCandle = isUpCandle;
			if (isfirstTime) {
				h2Jdbc.execute("delete from ZONE where time_period = " + tp.ordinal() + "" + " and instrument_token= "
						+ instrument.getInstrumentToken() + " " + "and distal_line < " + candle.getHigh() + " "
						+ "and zone_type= " + ZoneType.SELL.ordinal());
				h2Jdbc.execute("delete from ZONE where time_period = " + tp.ordinal() + "" + " and instrument_token= "
						+ instrument.getInstrumentToken() + " " + "and distal_line > " + candle.getLow() + " "
						+ "and zone_type= " + ZoneType.BUY.ordinal());
				h2Jdbc.execute("update ZONE set touched = true where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and proxy_line > "
						+ candle.getLow() + " " + "and zone_type= " + ZoneType.BUY.ordinal());
				h2Jdbc.execute("update ZONE set touched = true where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and proxy_line < "
						+ candle.getHigh() + " " + "and zone_type= " + ZoneType.SELL.ordinal());
			} else {
				jdbcTemplate.execute("delete from zone where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and distal_line < "
						+ candle.getHigh() + " " + "and zone_type= " + ZoneType.SELL.ordinal());
				jdbcTemplate.execute("delete from zone where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and distal_line > "
						+ candle.getLow() + " " + "and zone_type= " + ZoneType.BUY.ordinal());
				jdbcTemplate.execute("update zone set touched = true where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and proxy_line > "
						+ candle.getLow() + " " + "and zone_type= " + ZoneType.BUY.ordinal());
				jdbcTemplate.execute("update zone set touched = true where time_period = " + tp.ordinal() + ""
						+ " and instrument_token= " + instrument.getInstrumentToken() + " " + "and proxy_line < "
						+ candle.getHigh() + " " + "and zone_type= " + ZoneType.SELL.ordinal());
			}
		}
		if (excitedCandlesAfter.size() > 0 && excitedCandlesBefore.size() > 0) {
			// zone end
			Zone zone = saveZone(excitedCandlesBefore, baseCandles, excitedCandlesAfter,
					instrument.getInstrumentToken(), tp, isfirstTime);
		}

		System.out.println(
				"startted " + instrument.getInstrumentToken() + " " + instrument.getName() + " " + tp.toString());

	}

	private Zone saveZone(List<UiCandle> excitedCandlesBefore, List<UiCandle> baseCandles,
			List<UiCandle> excitedCandlesAfter, Long instrumentToken, TimePeriod tp, Boolean isfirstTime) {
		Zone zone = CandleUtils.createZone(excitedCandlesBefore, baseCandles, excitedCandlesAfter, instrumentToken, tp);
		if (zone.getZoneType() == ZoneType.BUY
				&& (zone.getMaxTarget() - zone.getProxyLine()) > (zone.getProxyLine() - zone.getDistalLine()) * 2) {
			if (isfirstTime) {
				h2Jdbc.insert(zone);
			} else {
				zoneRepository.save(zone);
			}

		} else if (zone.getZoneType() == ZoneType.SELL
				&& (zone.getProxyLine() - zone.getMaxTarget()) > (zone.getDistalLine() - zone.getProxyLine()) * 2) {
			if (isfirstTime) {
				h2Jdbc.insert(zone);

			} else {
				zoneRepository.save(zone);
			}
		}

		return zone;
	}

	public void detectZoneRecursive(Instrument instrument, TimePeriod tp) {
		Date endDate = new Date();
		int limit = 10000;
		Double mHigh = Double.MIN_VALUE;
		Double mLow = Double.MAX_VALUE;

		while (true) {
			List<UiCandle> candlesList = candleService.fetchMinuteDataByCompleteDateEqaulAndLessThanLimit(75,
					instrument.getInstrumentToken(), endDate, limit);
			for (UiCandle candle : candlesList) {
				boolean isExcited = CandleUtils.detectIsExcited(candle);
				boolean isUpCandle = CandleUtils.detectIsUpCandle(candle);
			}
		}

	}

	public class DetectZoneRunnable implements Runnable {
		Instrument instrument;
		Boolean isFirstTime;

		public DetectZoneRunnable(Instrument instrument, boolean isFirstTime) {
			this.instrument = instrument;
			this.isFirstTime = isFirstTime;
		}

		@Override
		public void run() {
			for (TimePeriod tp : TimePeriod.values()) {
				detectZoneNew(instrument, tp, isFirstTime);

			}
		}

	}

}
