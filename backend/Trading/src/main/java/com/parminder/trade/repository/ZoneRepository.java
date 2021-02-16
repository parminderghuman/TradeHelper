package com.parminder.trade.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parminder.trade.bo.DailyCandleData;
import com.parminder.trade.bo.WorkingDays;
import com.parminder.trade.bo.Zone;
import com.parminder.trade.bo.Zone.TimePeriod;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

	List<Zone> findAllByZoneCompositeKeyTimePeriodAndZoneCompositeKeyInstrumentToken(TimePeriod tp, Long instrumentToken);

	Page<Zone>  findAllByZoneCompositeKeyTimePeriodAndZoneCompositeKeyInstrumentToken(TimePeriod tp, Long instrumentToken, Pageable of);

	
}

