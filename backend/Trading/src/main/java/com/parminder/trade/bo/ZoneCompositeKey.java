package com.parminder.trade.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.parminder.trade.bo.Zone.TimePeriod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCompositeKey implements Serializable {

	Long instrumentToken;
	Date baseMinDate;

	TimePeriod timePeriod;

}
