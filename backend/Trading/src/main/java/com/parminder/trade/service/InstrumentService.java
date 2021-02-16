package com.parminder.trade.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parminder.trade.bo.Instrument;
import com.parminder.trade.repository.InstrumentRepository;

@Service
public class InstrumentService {

@Autowired
InstrumentRepository instrumentRepository;
	
//public static void main(String[] args) {
//	new InstrumentService().loadInstruments();
//}

public List<Instrument> get() {
	return instrumentRepository.findByIsFecthData(true);
}
	public void loadInstruments() {
		try {
			if (true) {
			return;	
			}
		URL url = new URL("https://api.kite.trade/instruments");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		int status = con.getResponseCode();
		System.out.println("status"+status);
		List<String> s = new ArrayList<String>();
		List<Instrument> l = new ArrayList<Instrument>();
		BufferedReader in = new BufferedReader(
				  new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				boolean isHeader = false;
				while ((inputLine = in.readLine()) != null) {
					if(!isHeader) {
						isHeader = true;
						continue;
					}
					//System.out.println(inputLine);
					s.add(inputLine);
				    //content.append(inputLine);
//					String[] s = inputLine.split(",");
//					Instrument instrument = new  Instrument( s);
//				l.add(instrument);
				}
				in.close();
				System.out.println("start saving");
				int i =0;
				for(String is : s) {
					Instrument instrument = new  Instrument(is.split(","));
					l.add(instrument);
					//instrumentRepository.save(instrument);
//					i++;
//					if(l.size()>1000) {
//						System.out.println(i);
//						instrumentRepository.saveAll(l);
//						l = new ArrayList();
//						System.out.println(i+" = done");
//						
//					}

				}
				System.out.println("start save start");
				instrumentRepository.saveAll(l);
				System.out.println("done");
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		System.out.println("done outer");

		
	}
}
