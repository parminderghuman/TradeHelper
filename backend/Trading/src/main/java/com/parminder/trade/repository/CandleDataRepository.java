package com.parminder.trade.repository;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.Instrument;

@Repository
public interface CandleDataRepository extends JpaRepository<MinuteCandleData	, Long> {

	MinuteCandleData findByCandleDataMinuteKeyInstrumentTokenAndCandleDataMinuteKeyCompleteDate(Long instrumentToken, Date completeDate);
	
	@Modifying
	@Transactional
	@Query(value="insert into candle_data (complete_date,exchange,symbol,instrument_token,hight,open,close,low,year,month,date,hour,minute,day_minute_value,open_interest,volume) values (:completeDate,:exchange,:symbol,:instrumentToken,:hight,:open,:close,:low,:year,:month,:date,:hour,:minute,:dayMinute,:openInterest,:volume) ON DUPLICATE KEY UPDATE hight = VALUES(hight),open = VALUES(open),close = VALUES(close),low = VALUES(low),open_interest = VALUES(open_interest),volume = VALUES(volume)",nativeQuery = true)
	int insertUpdateIfExist(
			@Param("completeDate")Date completeDate, @Param("exchange")String exchange,@Param("symbol")String symbol,@Param("instrumentToken")Long instrumentToken,
			@Param("hight")Float hight,@Param("open")Float open,@Param("close")Float close,	@Param("low")Float low,
			@Param("year")Integer year,@Param("month")Integer month,@Param("date")Integer date,@Param("hour")Integer hour,
			@Param("minute")Integer minute,@Param("dayMinute")Integer dayMinute,@Param("openInterest")Long openInterest,@Param("volume")Long volume);
	
	long countByCandleDataMinuteKeyInstrumentTokenAndCandleDataMinuteKeyCompleteDateLessThanEqualAndCandleDataMinuteKeyCompleteDateGreaterThanEqual(Long instrumentToken, Date endDate,Date startDate);

	Page<MinuteCandleData> findByCandleDataMinuteKeyInstrumentToken(long instrumentToekn, org.springframework.data.domain.Pageable of);

	
}
