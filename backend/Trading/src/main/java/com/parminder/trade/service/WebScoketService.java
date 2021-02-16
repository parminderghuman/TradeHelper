package com.parminder.trade.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parminder.trade.TradingApplication;
import com.parminder.trade.repository.InstrumentRepository;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;

@Service
public class WebScoketService {
	
	KiteTicker tickerProvider;

	@Autowired
	KiteService kiteService;
	
	@Autowired
	InstrumentRepository instrumentRepository;
	
	public void connect() throws KiteException {
		tickerProvider = new KiteTicker(kiteService.enctoken ,null, kiteService.userId);
		tickerProvider.setOnConnectedListener(connectLister);
		tickerProvider.setOnDisconnectedListener(onDisconnect);
		tickerProvider.setOnOrderUpdateListener(onOrderUpdate);
		tickerProvider.setOnTickerArrivalListener(onTicks);
		tickerProvider.setOnErrorListener(onError);
		tickerProvider.setTryReconnection(true);
		// maximum retries and should be greater than 0
		tickerProvider.setMaximumRetries(10);
		// set maximum retry interval in seconds
		tickerProvider.setMaximumRetryInterval(30);

		/**
		 * connects to com.zerodhatech.com.zerodhatech.ticker server for getting live
		 * quotes
		 */
		tickerProvider.connect();

	}

	OnError onError = new OnError() {

		@Override
		public void onError(String error) {
			System.out.println(error);

		}

		@Override
		public void onError(KiteException kiteException) {
			// TODO Auto-generated method stub
			kiteException.printStackTrace();

		}

		@Override
		public void onError(Exception exception) {
			exception.printStackTrace();
		}
	};
	OnConnect connectLister = new OnConnect() {

		@Override
		public void onConnected() {
			final ArrayList<Long> list = new ArrayList<Long>();
			
			/**
			 * Subscribe ticks for token. By default, all tokens are subscribed for
			 * modeQuote.
			 */
			instrumentRepository.findByIsFecthData(true).forEach(inst ->{
				list.add(inst.getInstrumentToken());
			});
			tickerProvider.subscribe(list);
			tickerProvider.setMode(list, KiteTicker.modeFull);
		}
	};

	OnDisconnect onDisconnect = new OnDisconnect() {

		@Override
		public void onDisconnected() {
			System.out.println("dis");

		}

	};

	OnOrderUpdate onOrderUpdate = new OnOrderUpdate() {

		@Override
		public void onOrderUpdate(Order order) {
			System.out.println(order);

		}
	};

	OnTicks onTicks = new OnTicks() {
		;

		@Override
		public void onTicks(ArrayList<Tick> ticks) {
			NumberFormat formatter = new DecimalFormat();
			System.out.println("ticks size " + ticks.size());
			System.out.println(ticks);
			if (ticks.size() > 0) {

				System.out.println("last price " + ticks.get(0).getLastTradedPrice());
				System.out.println("open interest " + formatter.format(ticks.get(0).getOi()));
				System.out.println("day high OI " + formatter.format(ticks.get(0).getOpenInterestDayHigh()));
				System.out.println("day low OI " + formatter.format(ticks.get(0).getOpenInterestDayLow()));
				System.out.println("change " + formatter.format(ticks.get(0).getChange()));
				System.out.println("tick timestamp " + ticks.get(0).getTickTimestamp());
				System.out.println("tick timestamp date " + ticks.get(0).getTickTimestamp());
				System.out.println("last traded time " + ticks.get(0).getLastTradedTime());
				System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
			}
		}

	};
}