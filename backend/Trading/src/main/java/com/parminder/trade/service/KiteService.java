package com.parminder.trade.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.parminder.trade.TradingApplication;
import com.parminder.trade.dto.CandleResponse;
import com.parminder.trade.dto.LoginResponse;
import com.parminder.trade.utils.DateUtils;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

@Service
public class KiteService {

	public static String histoticalDataUrl = "https://kite.zerodha.com/oms/instruments/historical/";
	String userId = "OX8934";
	String password = "Angad@111019";
	String twoFactorToken = "080910";
	String enctoken = "";
	String public_token = "";
	String loginurl = "https://kite.zerodha.com/api/login";
	String twoFactorUrl = "https://kite.zerodha.com/api/twofa";

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	MemoryService memoryService ;

	@Autowired
	WebScoketService webScoketService ;
	
	@PostConstruct
	public void postConstruct() throws KiteException, Exception {
	//	login();
	//	memoryService.init();
	//	webScoketService.connect();
	}

	public CandleResponse getHistoryData(long token, String from, String to,String time) throws Exception {
		return getHistoryData(token, from, to,time, true);
	}

	private CandleResponse getHistoryData(long token, String from, String to,String time, boolean retry) throws Exception {
		try {
			
		
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.set("authorization", "enctoken " + enctoken);
		MultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
		HttpEntity entity = new HttpEntity(headers);

		ResponseEntity<CandleResponse> response = restTemplate.exchange(histoticalDataUrl + token + "/"+time+"?user_id="
				+ userId + "&oi=1&from=" + from + "&to=" + to + "&ciqrandom=1610870487142  ", HttpMethod.GET, entity,
				CandleResponse.class, param);
		if (response.getStatusCode() == HttpStatus.OK) {
			CandleResponse cr = response.getBody();
			return cr;
		} else {
			if (retry) {
				login();
				return getHistoryData(token, from, to,time, false);

			} else {
				throw new Exception("Cant login");
			}
		}
		}catch (HttpClientErrorException.BadRequest e) {
			login();
			return getHistoryData(token, from, to,time, false);
		}
	}

	public void login() {

		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("accept", "application/json, text/plain, */*");
		headers.add("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
		headers.add("user_id", userId);
		headers.add("password", password);

		headers.add("x-kite-userid", userId);
		headers.add("x-kite-version", "2.6.3");
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("user_id", userId);
		map.add("password", password);
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<LoginResponse> response = restTemplate.postForEntity(loginurl, entity, LoginResponse.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Request Successful");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			org.springframework.http.HttpHeaders responseHeaders = response.getHeaders();
			List<String> values = responseHeaders.get(org.springframework.http.HttpHeaders.SET_COOKIE);
			String cokkie = "";
			for (String val : values) {
				cokkie = cokkie + val.split(";")[0] + "; ";
			}
			cokkie = cokkie.subSequence(0, cokkie.length() - 2).toString();
			System.out.println("final cookie" + cokkie);

			twoFactorAuth(response.getBody(), cokkie);

		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
	}

	private void twoFactorAuth(LoginResponse loginResponse, String cokkie) {

		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		headers.add("accept", "application/json, text/plain, */*");

		headers.add("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
		headers.add("user_id", userId);
		headers.add("password", password);

		headers.add("x-kite-userid", userId);
		headers.add("x-kite-version", "2.6.3");
		headers.add("cookie", cokkie);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("user_id", userId);
		map.add("request_id", loginResponse.getData().getRequest_id());
		map.add("twofa_value", twoFactorToken);
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(twoFactorUrl, entity, String.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
			System.out.println(response.getBody());
			org.springframework.http.HttpHeaders responseHeaders = response.getHeaders();
			List<String> set_cookie = responseHeaders.get(org.springframework.http.HttpHeaders.SET_COOKIE);
			for(String setC:set_cookie) {
				if(setC.startsWith("public_token=")) {
					this.public_token = setC.split("public_token=")[1].split(";")[0];
					System.out.println("public token " +this.public_token);
				}else if(setC.startsWith("enctoken=")) {
					this.enctoken = setC.split("enctoken=")[1].split(";")[0];
					System.out.println("enctoken token " +this.enctoken);

				}
			}

		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
	}

}
