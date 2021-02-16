package com.parminder.trade.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.parminder.trade.bo.Instrument;

@Repository
public interface InstrumentRepository extends CrudRepository<Instrument	, Long> {
	
	List<Instrument> findByIsFecthData(boolean isFecthData);
	
	Instrument  findByInstrumentToken(Long instrumentToken);

}
