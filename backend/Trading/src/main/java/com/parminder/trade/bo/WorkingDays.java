package com.parminder.trade.bo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
public class WorkingDays {

	
	
	@Id
	@Temporal(TemporalType.DATE)
	Date date;

	
	public WorkingDays() {
		// TODO Auto-generated constructor stub
	}
	public WorkingDays(Date completeDate) {
		this.date = completeDate;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
