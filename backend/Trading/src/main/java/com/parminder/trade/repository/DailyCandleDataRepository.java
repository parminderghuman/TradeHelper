
package com.parminder.trade.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.DailyCandleData;
import com.parminder.trade.bo.Instrument;

@Repository
public interface DailyCandleDataRepository extends JpaRepository<DailyCandleData	, Long> {

	MinuteCandleData findByCandleDataDailyKeyInstrumentTokenAndCandleDataDailyKeyCompleteDate(Long instrumentToken, Date completeDate);
	
	


	Page<DailyCandleData> findByCandleDataDailyKeyInstrumentToken(long instrumentToekn,Pageable  of);
	Page<DailyCandleData> findByCandleDataDailyKeyInstrumentTokenAndVerified(long instrumentToekn,Boolean verified,Pageable  of);

	 
	
}
