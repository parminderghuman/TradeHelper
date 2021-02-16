package com.parminder.trade.dto;

import lombok.Data;

@Data
public class LoginResponse {

	String status;
	Data data;

	
	public class Data {
		
		public Data() {
			// TODO Auto-generated constructor stub
		}
		String request_id;// "TrvEMBxvinzTfnnsTCAr44tWcxODB0kBgEcWu0G7aQhgpoeOqvtr679stCzlL1yp"
		String twofa_status;// "active"
		String twofa_type;// "pin"
		String user_id;// : "OX8934"
		public String getRequest_id() {
			return request_id;
		}
		public void setRequest_id(String request_id) {
			this.request_id = request_id;
		}
		public String getTwofa_status() {
			return twofa_status;
		}
		public void setTwofa_status(String twofa_status) {
			this.twofa_status = twofa_status;
		}
		public String getTwofa_type() {
			return twofa_type;
		}
		public void setTwofa_type(String twofa_type) {
			this.twofa_type = twofa_type;
		}
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		
		
	}

}
