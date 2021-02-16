package com.parminder.trade.utils;

import java.util.List;

import com.parminder.trade.bo.Zone;
import com.parminder.trade.bo.Zone.TimePeriod;
import com.parminder.trade.bo.Zone.ZoneSide;
import com.parminder.trade.bo.Zone.ZoneType;
import com.parminder.trade.bo.ZoneCompositeKey;
import com.parminder.trade.dto.UiCandle;

public class CandleUtils {

	public static boolean detectIsExcited(UiCandle uiCandle) {
//		double ocDiff = Math.abs(uiCandle.getOpen() - uiCandle.getClose());
//		double tlDiff = Math.abs(uiCandle.getHigh() - uiCandle.getLow());

		return Math.abs(uiCandle.getOpen() - uiCandle.getClose()) *2.5 > (uiCandle.getHigh() - uiCandle.getLow());

		

	}

	public static boolean detectIsUpCandle(UiCandle candle) {
		return candle.getOpen() < candle.getClose();

	}

	public static Zone createZone(List<UiCandle> excitedCandlesBefore, List<UiCandle> baseCandles,
			List<UiCandle> excitedCandlesAfter, Long instrumentToken, TimePeriod tp) {
		boolean previousFlow = detectIsUpCandle(excitedCandlesBefore.get(0));
		boolean nextFlow = detectIsUpCandle(excitedCandlesAfter.get(0));
		Zone zone = new Zone();

		zone.setZoneType(nextFlow ? ZoneType.BUY : ZoneType.SELL);
		zone.setBaseCandleCount(baseCandles.size());

		zone.setZoneSide(nextFlow == previousFlow ? ZoneSide.Continue : ZoneSide.Recursive);
		zone.setMaxTarget(excitedCandlesAfter.get(excitedCandlesAfter.size() - 1).getClose());

		zone.setProxyLine(detectProxymalLine(excitedCandlesBefore, baseCandles, excitedCandlesAfter, zone.getZoneType(),
				zone.getZoneSide()));
		zone.setDistalLine(detectDistalLine(excitedCandlesBefore, baseCandles, excitedCandlesAfter, zone.getZoneType(),
				zone.getZoneSide()));
		zone.setMaxTarget(detectMaxTarget(excitedCandlesBefore, baseCandles, excitedCandlesAfter, zone.getZoneType(),
				zone.getZoneSide()));

		zone.setBaseMaxDate(baseCandles.size() > 0 ? baseCandles.get(baseCandles.size() - 1).getDate()
				: excitedCandlesBefore.get(excitedCandlesBefore.size() - 1).getDate());
		zone.setStartDate(excitedCandlesBefore.get(0).getMinDate());
		zone.setEndDate(excitedCandlesAfter.get(excitedCandlesAfter.size() - 1).getMinDate());

		zone.setZoneCompositeKey(new ZoneCompositeKey(instrumentToken,
				baseCandles.size() > 0 ? baseCandles.get(baseCandles.size() - 1).getMinDate()
						: excitedCandlesBefore.get(excitedCandlesBefore.size() - 1).getMinDate(),
				tp));
		return zone;
	}

	public static double detectProxymalLine(List<UiCandle> excitedCandlesBefore, List<UiCandle> baseCandles,
			List<UiCandle> excitedCandlesAfterm, ZoneType zoneType, ZoneSide zoneSide) {

		if (baseCandles.size() > 0) {
			UiCandle candle = baseCandles.get(baseCandles.size() - 1);

			if (zoneType == ZoneType.BUY) {
				return candle.getOpen() < candle.getClose() ? candle.getOpen() : candle.getClose();
			}
			if (zoneType == ZoneType.SELL) {
				return candle.getOpen() > candle.getClose() ? candle.getOpen() : candle.getClose();
			}
		} else {
			UiCandle beforeCandle = excitedCandlesBefore.get(excitedCandlesBefore.size() - 1);
			UiCandle AfterCandle = excitedCandlesAfterm.get(0);

			if (zoneType == ZoneType.BUY) {
				return AfterCandle.getOpen() > beforeCandle.getClose() ? AfterCandle.getOpen()
						: beforeCandle.getClose();
			}
			if (zoneType == ZoneType.SELL) {
				return AfterCandle.getOpen() < beforeCandle.getClose() ? AfterCandle.getOpen()
						: beforeCandle.getClose();

			}
		}
		return 0;
	}

	public static double detectMaxTarget(List<UiCandle> excitedCandlesBefore, List<UiCandle> baseCandles,
			List<UiCandle> excitedCandlesAfterm, ZoneType zoneType, ZoneSide zoneSide) {
		UiCandle candle = excitedCandlesAfterm.get(excitedCandlesAfterm.size() - 1);

		if (zoneType == ZoneType.BUY) {
			return candle.getClose();
		}
		if (zoneType == ZoneType.SELL) {
			return candle.getClose();
		}

		return 0;
	}

	public static double detectDistalLine(List<UiCandle> excitedCandlesBefore, List<UiCandle> baseCandles,
			List<UiCandle> excitedCandlesAfterm, ZoneType zoneType, ZoneSide zoneSide) {
		double dV = 0;
		if (zoneType == ZoneType.BUY) {
			dV = Double.MAX_VALUE;
			for (UiCandle uiCandle : baseCandles) {
				if (dV > uiCandle.getLow()) {
					dV = uiCandle.getLow();
				}
			}
			for (UiCandle uiCandle : excitedCandlesAfterm) {
				if (dV > uiCandle.getLow()) {
					dV = uiCandle.getLow();
				}
			}
			if (zoneSide == ZoneSide.Recursive) {
				for (UiCandle uiCandle : excitedCandlesBefore) {
					if (dV > uiCandle.getLow()) {
						dV = uiCandle.getLow();
					}
				}
			}
		}
		if (zoneType == ZoneType.SELL) {
			dV = Double.MIN_VALUE;
			for (UiCandle uiCandle : baseCandles) {
				if (dV < uiCandle.getHigh()) {
					dV = uiCandle.getHigh();
				}
			}
			for (UiCandle uiCandle : excitedCandlesAfterm) {
				if (dV < uiCandle.getHigh()) {
					dV = uiCandle.getHigh();
				}
			}
			if (zoneSide == ZoneSide.Recursive) {
				for (UiCandle uiCandle : excitedCandlesBefore) {
					if (dV < uiCandle.getHigh()) {
						dV = uiCandle.getHigh();
					}
				}
			}
		}

		return dV;
	}

}
