package com.parminder.trade.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CandleResponse implements Serializable{

String	status;

Map<String,List<List<String>>> data;

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public Map<String, List<List<String>>> getData() {
	return data;
}

public void setData(Map<String, List<List<String>>> data) {
	this.data = data;
}
	
}
