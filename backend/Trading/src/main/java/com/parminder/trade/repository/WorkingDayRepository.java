package com.parminder.trade.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.parminder.trade.bo.MinuteCandleData;
import com.parminder.trade.bo.Instrument;
import com.parminder.trade.bo.WorkingDays;

@Repository
public interface WorkingDayRepository extends JpaRepository<WorkingDays, Long> {

}
