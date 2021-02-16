package com.parminder.trade.bo;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Zone {

	public enum TimePeriod {
		QUARTER, MONTH, WEEK, DAY, MIN_125, MIN_75, MIN_25, MIN_15, MIN_5, MIN_3, MIN_1
	}

	public enum ZoneType {
		SELL, BUY
	}

	public enum ZoneSide {
		Continue, Recursive
	}
 
	@EmbeddedId
	ZoneCompositeKey zoneCompositeKey;
	
	Integer baseCandleCount;
	
	ZoneType zoneType;

	
	Double proxyLine;
	
	Double distalLine;
	
	Double maxTarget;
	
	ZoneSide zoneSide;
	
	Boolean touched ;

	
	Date baseMaxDate;
	Date startDate;

	Date endDate;

	Double hightValue;

	Double lowValue;

	
	

	

	Double entry;
	Double stopLoss;
	Double target;

	Date createdDate;

}
