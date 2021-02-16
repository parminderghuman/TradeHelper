package com.parminder.trade.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CandleDataMinuteKey implements Serializable  {
	
	
	public CandleDataMinuteKey() {
		// TODO Auto-generated constructor stub
	}
	
	public CandleDataMinuteKey(Long instrumentToken, Date completeDate) {
		this.instrumentToken = instrumentToken;
		this.completeDate =completeDate;
	}

	@Column(name = "complete_date")
	Date completeDate;

	@Column(name = "instrument_token")
	Long instrumentToken;

	
	public Date getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public Long getInstrumentToken() {
		return instrumentToken;
	}

	public void setInstrumentToken(Long instrumentToken) {
		this.instrumentToken = instrumentToken;
	}
	
	
	
}
