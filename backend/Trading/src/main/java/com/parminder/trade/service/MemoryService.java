package com.parminder.trade.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parminder.trade.bo.Instrument;
import com.parminder.trade.dto.CandleResponse;
import com.parminder.trade.dto.UiCandle;
import com.parminder.trade.repository.InstrumentRepository;
import com.parminder.trade.utils.DateUtils;

import lombok.val;

@Service
public class MemoryService {

	@Autowired
	CandleService candleService;

	@Autowired
	KiteService kiteService;

	@Autowired
	InstrumentRepository instrumentRepository;

	Map<Long, Map<Integer, UiCandle>> memoryMap = new HashMap<Long, Map<Integer, UiCandle>>(200);

	public synchronized void putInstrumentData(Long instrumentToken, int minuteOfTheDay, UiCandle uiCandle) {

		if (!memoryMap.containsKey(instrumentToken)) {
			memoryMap.put(instrumentToken, new HashMap<Integer, UiCandle>(500));
		}
		memoryMap.get(instrumentToken).put(minuteOfTheDay, uiCandle);
		// System.out.println(memoryMap);
	}

	public void init() throws Exception {
		loadTodayData();
		// List<UiCandle> uiCandles = getData(5633l, 3);
		//System.out.println(uiCandles);

	}

//
//	public static void main(String[] args) {
//		MemoryService m = new MemoryService();
//		m.putInstrumentData(33l, 0, new UiCandle());
//		m.putInstrumentData(33l, 0, new UiCandle());
//		m.putInstrumentData(33l, 2, new UiCandle());
//	}
	public static void main(String[] args) {
		for (int i = 0; i < 375; i++) {
			System.out.println((int) i / 3);
		}
	}

	public List<UiCandle> getData(Long instrumentToken, int factor) {
		List<UiCandle> candles = new ArrayList<UiCandle>();
		Map<Integer, UiCandle> map = memoryMap.get(instrumentToken);
		if (map != null) {

			int integer = 0;
			UiCandle candle = null;

			for (Entry<Integer, UiCandle> entry : map.entrySet()) {
				int key = entry.getKey();
				UiCandle uicandle = entry.getValue();
				if (candle == null) {
					candle = uicandle.cloneCandle();
				}

				if (integer != (int) key / factor) {

					integer++;
					candles.add(candle);
					candle = uicandle.cloneCandle();
				} else {

					if (candle.getHigh() < uicandle.getHigh()) {
						candle.setHigh(uicandle.getHigh());
					}

					if (candle.getLow() > uicandle.getLow()) {
						candle.setLow(uicandle.getLow());
					}
					candle.setClose(uicandle.getClose());
					candle.setDate(uicandle.getDate());
					candle.addInterest(uicandle.getInterest());
					candle.addVolume(uicandle.getVolume());
				}
			}
			if (candle != null) {
				candles.add(candle);
			}

		}
		return candles;
	}

	public void loadTodayData() throws Exception {

		Date d = new Date();
		List<Instrument> instruments = instrumentRepository.findByIsFecthData(true);
		Date date = new Date();

		date.setHours(9);
		date.setMinutes(15);
		date.setSeconds(0);

		date.setTime(((long) date.getTime() / 1000) * 1000);

		String sDate = DateUtils.parseDayFormate(date);
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (Instrument i : instruments) {
			executorService.execute(new BackLoad(i, kiteService, sDate, date));

		}
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.MINUTES);
		System.out.println("loading sone" + (new Date().getTime() - d.getTime()));
	}

	private class BackLoad implements Runnable {
		Instrument i;
		String sDate;
		Date date;

		public BackLoad(Instrument i, KiteService kiteService, String sDate, Date date) {
			this.i = i;
			this.sDate = sDate;
			this.date = date;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			CandleResponse cr;
			try {
				cr = kiteService.getHistoryData(i.getInstrumentToken(), sDate, sDate, "minute");

				for (List<String> candle : cr.getData().get("candles")) {

					try {

						UiCandle uiCandle = new UiCandle();
						uiCandle.setClose(Float.parseFloat(candle.get(4)));
						String timestamp = candle.get(0);
						uiCandle.setDate(DateUtils.parseCompletFormate(timestamp));
						uiCandle.setHigh(Float.parseFloat(candle.get(2)));
						uiCandle.setInterest(Long.parseLong(candle.get(6)));
						uiCandle.setLow(Float.parseFloat(candle.get(3)));
						uiCandle.setOpen(Float.parseFloat(candle.get(1)));
						uiCandle.setVolume(Long.parseLong(candle.get(5)));
						uiCandle.setMinDate(DateUtils.parseCompletFormate(timestamp));
						Date completeDate = DateUtils.parseCompletFormate(timestamp);
						// System.out.println((completeDate.getTime() - date.getTime()));
						int dayMinute = (int) ((completeDate.getTime() - date.getTime()) / 60000);
						putInstrumentData(i.getInstrumentToken(), dayMinute, uiCandle);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
}
