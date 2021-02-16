package com.parminder.trade.bo;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.parminder.trade.utils.DateUtils;

import lombok.Data;

@Entity

@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "instrument_token", "complete_date" }) })

public class DailyCandleData {

	public DailyCandleData() {
		// TODO Auto-generated constructor stub
	}

	public DailyCandleData(Instrument instrument, List<String> candle) throws Exception {

		String timestamp = candle.get(0);
		Date completeDate = DateUtils.parseCompletFormate(timestamp);
		open = Float.parseFloat(candle.get(1));
		hight = Float.parseFloat(candle.get(2));
		low = Float.parseFloat(candle.get(3));
		close = Float.parseFloat(candle.get(4));
		volume = Long.parseLong(candle.get(5));
		openInterest = Long.parseLong(candle.get(6));

		this.exchange = instrument.exchange;
		this.symbol = instrument.tradingSymbol;
		this.candleDataDailyKey = new CandleDataDailyKey(instrument.instrumentToken, completeDate);
		this.year = completeDate.getYear() + 1900;
		this.month = completeDate.getMonth() + 1;
		this.date = completeDate.getDate();
		this.hour = completeDate.getHours();
		this.minute = completeDate.getMinutes();
		Date tempDate = new Date(completeDate.getTime());
		tempDate.setHours(9);
		tempDate.setMinutes(15);
		tempDate.setSeconds(0);
		verified = false;

		this.dayMinute = (int) ((completeDate.getTime() - tempDate.getTime()) / 60000);
		// System.out.println(dayMinute);
	}

	@EmbeddedId
	CandleDataDailyKey candleDataDailyKey;

	String exchange;

	String symbol;

	// @Column(name = "c_h")
	Float hight;
	// @Column(name = "c_o")

	Float open;
	// @Column(name = "c_c")
	Float close;

	// @Column(name = "c_l")
	Float low;

	// @Column(name = "c_year")
	Integer year;

	// @Column(name = "c_month")
	Integer month;

	// @Column(name = "c_date")
	Integer date;

	// @Column(name = "c_hour")
	Integer hour;
	// @Column(name = "c_minute")

	Integer minute;

	@Column(name = "day_minute_value")
	Integer dayMinute;

	@Column(name = "open_interest")
	Long openInterest;

	Long volume;

	Boolean verified;

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Float getHight() {
		return hight;
	}

	public void setHight(Float hight) {
		this.hight = hight;
	}

	public Float getOpen() {
		return open;
	}

	public void setOpen(Float open) {
		this.open = open;
	}

	public Float getClose() {
		return close;
	}

	public void setClose(Float close) {
		this.close = close;
	}

	public Float getLow() {
		return low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public Integer getDayMinute() {
		return dayMinute;
	}

	public void setDayMinute(Integer dayMinute) {
		this.dayMinute = dayMinute;
	}

	public Long getOpenInterest() {
		return openInterest;
	}

	public void setOpenInterest(Long openInterest) {
		this.openInterest = openInterest;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public Date getCompleteDate() {
		return this.candleDataDailyKey.getCompleteDate();
	}
	public long getInstrumentToken() {
		return this.candleDataDailyKey.getInstrumentToken();

	}


}
