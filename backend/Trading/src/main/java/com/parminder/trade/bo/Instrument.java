package com.parminder.trade.bo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parminder.trade.utils.DateUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Instrument {
	

	

	
	public Instrument(){
		
	}
	public Instrument(String[] s) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		instrumentToken = Long.parseLong(s[0]);
		exchangeToken = Long.parseLong(s[1]);
		tradingSymbol = s[2];
		name = s[3];
		lastPrice = Float.parseFloat(s[4]);
		try {
			expiry =DateUtils.parseDayFormate(s[5]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(s+ " "+s[5]);
		}
		strike =Float.parseFloat(s[6]);
		tickSize =Float.parseFloat(s[7]);
		
				lotSize=Integer.parseInt(s[8]);
				instrumentType = s[9];
				segment =s[10];
				exchange=s[11];
		
	}
	
	@Id
	@Column(name = "instrument_token")
	Long instrumentToken;
	
	@Column(name = "exchange_token")
	Long exchangeToken;
	@Column(name = "tradingsymbol")

	String tradingSymbol;
	String name;
	@Column(name = "last_price")

	Float lastPrice;
	Date expiry;
	Float strike;
	@Column(name = "tick_size")

	Float tickSize;
	@Column(name = "lot_size")

	Integer lotSize;
	@Column(name = "instrument_type")

	String instrumentType;
	String segment;
	String exchange;
	
	@Column(name="is_fetch_data")	
	Boolean isFecthData;
	
	@Column(name="last_fetch_time")
	Date lastFetchTime;
	
	@Column(name="last_verified_date")
	Date lastVerifiedDate;
	
	@Transient
	Long totalDailyCount;

	public Long getTotalDailyCount() {
		return totalDailyCount;
	}
	public void setTotalDailyCount(Long instrumentToken) {
		this.totalDailyCount = instrumentToken;
	}
	public Long getInstrumentToken() {
		return instrumentToken;
	}
	public void setInstrumentToken(Long instrumentToken) {
		this.instrumentToken = instrumentToken;
	}
	public Long getExchangeToken() {
		return exchangeToken;
	}
	public void setExchangeToken(Long exchangeToken) {
		this.exchangeToken = exchangeToken;
	}
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Float getLastPrice() {
		return lastPrice;
	}
	public void setLastPrice(Float lastPrice) {
		this.lastPrice = lastPrice;
	}
	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	public Float getStrike() {
		return strike;
	}
	public void setStrike(Float strike) {
		this.strike = strike;
	}
	public Float getTickSize() {
		return tickSize;
	}
	public void setTickSize(Float tickSize) {
		this.tickSize = tickSize;
	}
	public Integer getLotSize() {
		return lotSize;
	}
	public void setLotSize(Integer lotSize) {
		this.lotSize = lotSize;
	}
	public String getInstrumentType() {
		return instrumentType;
	}
	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public Boolean getIsFecthData() {
		return isFecthData;
	}
	public void setIsFecthData(Boolean isFecthData) {
		this.isFecthData = isFecthData;
	}
	public Date getLastFetchTime() {
		return lastFetchTime;
	}
	public void setLastFetchTime(Date lastFetchTime) {
		this.lastFetchTime = lastFetchTime;
	}
	public Date getLastVerifiedDate() {
		return lastVerifiedDate;
	}
	public void setLastVerifiedDate(Date lastVerifiedDate) {
		this.lastVerifiedDate = lastVerifiedDate;
	}
	
	
	
}
	
	/*
	 * 
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("zeel") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("wipro") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("voltas") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("vedl") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("upl") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ultracemco") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ujjivan") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ubl") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("tvsmotor") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("torntpower") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TORNTPHARM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TITAN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TECHM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TCS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TATASTEEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TATAPOWER") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TATAMOTORS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TATACONSUM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("TATACHEM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SUNTV") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SUNPHARMA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SRTRANSFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SRF") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SIEMENS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SHREECEM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SBIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("SAIL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("RELAINCE") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("RECLTD") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("RBLBANK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("RAMCOCEM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("POWERGRID") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PNB") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PIDILITIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PFC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PETRONET") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("PAGEIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ONGC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("OIL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NTPC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NMDC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NESTLEIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NCC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NAUKRI") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("NATIONALUM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MUTHOOTFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MRF") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MOTHERSUMI") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MINDTREE") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MGL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MFSL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MCDOWELL-N") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MARUTI") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MARICO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("MANAPPURAM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("M&MFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("M&M") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("LUPIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("LT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("LICHSGFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("L&TFH") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("KOTAKBANK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("JUSTDIAL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("JUBLFOOD") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("JSWSTEEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("JINDALSTEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ITC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("IOC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("INFY") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("INDUSINDBK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("INDIGO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("IGL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("IDFCFIRSTB") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ICICIPRULLI") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ICICIBANK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("IBULHSGFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HINDUNILVR") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HINDPETRO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HINDALCO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HEROMOTOCO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HDFCLIFE") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HDFCBANK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HDFC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HCLTECH") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("HAVELLS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("GRASIM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("GODREJCP") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("GMRINFRA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("GLENMARK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("GAIL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("FEDERALBNK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("EXIDEIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ESCORTS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("EQUITAS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("EICHERMOT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("DRREDDY") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("DLF") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("DIVISLAB") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("DABUR") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CUMMINSIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CONCOR") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("COLPAL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("COALINDIA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CIPLA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CHOLAFIN") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CESC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CENTURYTEX") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CANBK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("CADILAHC") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BRITANNIA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BPCL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BOSCHLTD") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BIOCON") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BHEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BHARTIARTL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BHARTFORG") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BERGEPAINT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BEL") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BATAINDIA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BANKBARODA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BANDHANBNK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BALKRISIND") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BAJFINANCE") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BAJAJFINSV") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("BAJAJ-AUTO") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("AXISBANK") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("AUROPHARMA") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ASIANPAINT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ASHOKLEY") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("APOLLOTYRE") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("APOLLOHOSP") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("AMBUJACEM") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("AMARAJABAT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ADANIPOWER") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ADANIPORTS") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ADANIENT") and exchange="NSE";
update instrument set is_fetch_data = 1 where tradingsymbol = UPPER("ACC") and exchange="NSE";
*/
