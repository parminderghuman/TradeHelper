package com.parminder.trade.dto;

import java.util.Date;

import com.parminder.trade.utils.CandleUtils;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UiCandle {


	public UiCandle(double open, double hight, double close, double low, double volume, double interest, Date date, Date minDate) {
		this.open = open;
		this.high = hight;
		this.close = close;
		this.low = low;
		this.volume = volume;
		this.interest = interest;
		this.date = date;
		this.minDate = minDate;
	}

	double open;
	double high;
	double close;
	double low;
	double volume;
	double interest;
	Date date;
	
	Date minDate;
	
	boolean isUpCandle;
	
	boolean isExcited;


	public boolean isUpCandle() {
	return	CandleUtils.detectIsUpCandle(this);
	}

	public void setUpCandle(boolean isUpCandle) {
		this.isUpCandle = isUpCandle;
	}

	public boolean isExcited() {
		return CandleUtils.detectIsExcited(this);
	}

	public void setExcited(boolean isExcited) {
		this.isExcited = isExcited;
	}

	public UiCandle cloneCandle() {
		// TODO Auto-generated method stub
		return new UiCandle(open, high, close, low, volume, interest, date,minDate);
	}

	public void addInterest(double interest) {
		this.interest = this.interest + interest;

	}

	public void addVolume(double volume) {
		this.volume = this.volume + volume;
	}

}
