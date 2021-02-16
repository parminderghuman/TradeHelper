package com.parminder.trade;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties.Jdbc;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest.Headers;

import com.parminder.trade.dto.LoginResponse;

import okhttp3.internal.http2.Header;

public class JavaPost {

	public static void main(String[] args) {
		String url = "https://kite.zerodha.com/api/login";

		RestTemplate restTemplate = new RestTemplate();
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// headers.add(null, null);
		// headers.add(":authority", "kite.zerodha.com");
		// headers.add(":method","POST");

		// headers.add(":path", "/api/login");
		// headers.add(":scheme","https");

		headers.add("accept", "application/json, text/plain, */*");
		// headers.add("accept-encoding","gzip, deflate, br");

		headers.add("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
		headers.add("user_id", "OX8934");
		headers.add("password", "Angad@111019");

		headers.add("x-kite-userid", "OX8934");
		headers.add("x-kite-version", "2.6.3");
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("user_id", "OX8934");
		map.add("password", "Angad@111019");
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, entity, LoginResponse.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
			System.out.println(response.getBody());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			org.springframework.http.HttpHeaders responseHeaders = response.getHeaders();

			// for( String s : responseHeaders.keySet()) {
			List<String> values = responseHeaders.get(org.springframework.http.HttpHeaders.SET_COOKIE);

			String cokkie = "";
			for (String val : values) {
				cokkie = cokkie + val.split(";")[0] + "; ";
				System.out.println(val);
			}
			;

			cokkie = cokkie.subSequence(0, cokkie.length() - 2).toString();
			System.out.println("final cookie" + cokkie);

			twoFactorAuth(response.getBody(), cokkie);

		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
	}

	private static void twoFactorAuth(LoginResponse loginResponse, String cokkie) {
		String url = "https://kite.zerodha.com/api/twofa";

		RestTemplate restTemplate = new RestTemplate();
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// headers.add(null, null);
		// headers.add(":authority", "kite.zerodha.com");
		// headers.add(":method","POST");

		// headers.add(":path", "/api/login");
		// headers.add(":scheme","https");

		headers.add("accept", "application/json, text/plain, */*");
		// headers.add("accept-encoding","gzip, deflate, br");

		headers.add("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
		headers.add("user_id", "OX8934");
		headers.add("password", "Angad@111019");

		headers.add("x-kite-userid", "OX8934");
		headers.add("x-kite-version", "2.6.3");
		headers.add("cookie", cokkie);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("user_id", "OX8934");
		map.add("request_id", loginResponse.getData().getRequest_id());
		map.add("twofa_value", "080910");
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

		// check response
		if (response.getStatusCode() == HttpStatus.OK) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
			System.out.println(response.getBody());
			org.springframework.http.HttpHeaders responseHeaders = response.getHeaders();
			List<String> set_cookie = responseHeaders.get(org.springframework.http.HttpHeaders.SET_COOKIE);
			System.out.println(set_cookie);
			for(String setC:set_cookie) {
				if(setC.startsWith("public_token=")) {
					System.out.println("public token " +setC.split("public_token=")[1].split(";")[0]);
				}else if(setC.startsWith("enctoken=")) {
					System.out.println("enctoken token " +setC.split("enctoken=")[1].split(";")[0]);

				}
			}

		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
	}
}
