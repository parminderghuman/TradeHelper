package com.parminder.trade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import com.parminder.trade.bo.Zone;
import com.parminder.trade.bo.Zone.TimePeriod;
import com.parminder.trade.bo.ZoneCompositeKey;

public class H2Jdbc {

	JdbcTemplate jdbcTemplate;

	public H2Jdbc(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		
		createTable();
	}

	public Object createTable() {
		return this.jdbcTemplate.update(" CREATE TABLE ZONE (\n" + "  `base_min_date` datetime(6) NOT NULL,\n"
				+ "  `instrument_token` bigint NOT NULL,\n" + "  `time_period` int NOT NULL,\n"
				+ "  `base_candle_count` int DEFAULT NULL,\n" + "  `base_max_date` datetime(6) DEFAULT NULL,\n"
				+ "  `created_date` datetime(6) DEFAULT NULL,\n" + "  `distal_line` double DEFAULT NULL,\n"
				+ "  `end_date` datetime(6) DEFAULT NULL,\n" + "  `entry` double DEFAULT NULL,\n"
				+ "  `hight_value` double DEFAULT NULL,\n" + "  `low_value` double DEFAULT NULL,\n"
				+ "  `max_target` double DEFAULT NULL,\n" + "  `proxy_line` double DEFAULT NULL,\n"
				+ "  `start_date` datetime(6) DEFAULT NULL,\n" + "  `stop_loss` double DEFAULT NULL,\n"
				+ "  `target` double DEFAULT NULL,\n" + "  `touched` bit(1) DEFAULT NULL,\n"
				+ "  `zone_side` int DEFAULT NULL,\n" + "  `zone_type` int DEFAULT NULL,\n"
				+ "  PRIMARY KEY (`base_min_date`,`instrument_token`,`time_period`)\n" + ") ");

	}

	String insertSql = "insert into ZONE (base_min_date, instrument_token , time_period , base_candle_count , base_max_date,created_date , distal_line , end_date, entry ,hight_value ,"
			+ " low_value , max_target ,proxy_line , start_date , stop_loss , target , touched , zone_side , zone_type ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	String update = "update  ZONE SET " + " base_candle_count = ?, base_max_date = ?,created_date = ?,"
			+ " distal_line = ?, `end_date` = ?,`entry` = ?,"
			+ "`hight_value` = ?, `low_value` = ?,`max_target` = ?,`proxy_line` = ?,"
			+ "`start_date` = ?, `stop_loss` =?,`target` = ?,"
			+ "`touched` = ?, `zone_side` = ?,`zone_type` = ? where base_min_date =? and  instrument_token =? and time_period=?";

	public void insert(Zone zone) {
		try {
			this.insertInternal(zone);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			
			this.update(zone);
		}

	}

	public void update(Zone zone)  {
		jdbcTemplate.update(update, new Object[] { zone.getBaseCandleCount(), zone.getBaseMaxDate(),
				zone.getCreatedDate(), zone.getDistalLine(), zone.getEndDate(), zone.getEntry(), zone.getHightValue(),
				zone.getLowValue(), zone.getMaxTarget(), zone.getProxyLine(), zone.getStartDate(), zone.getStopLoss(),
				zone.getTarget(), zone.getTouched(), zone.getZoneSide() != null ? zone.getZoneSide().ordinal() : null,
				zone.getZoneType() != null ? zone.getZoneType().ordinal() : null,
				zone.getZoneCompositeKey().getBaseMinDate(), zone.getZoneCompositeKey().getInstrumentToken(),
				zone.getZoneCompositeKey().getTimePeriod().ordinal(), });
	}

	public void insertInternal(Zone zone) throws DuplicateKeyException {
		jdbcTemplate.update(insertSql, new Object[] { zone.getZoneCompositeKey().getBaseMinDate(),
				zone.getZoneCompositeKey().getInstrumentToken(), zone.getZoneCompositeKey().getTimePeriod().ordinal(),
				zone.getBaseCandleCount(), zone.getBaseMaxDate(), zone.getCreatedDate(), zone.getDistalLine(),
				zone.getEndDate(), zone.getEntry(), zone.getHightValue(), zone.getLowValue(), zone.getMaxTarget(),
				zone.getProxyLine(), zone.getStartDate(), zone.getStopLoss(), zone.getTarget(), zone.getTouched(),
				zone.getZoneSide() != null ? zone.getZoneSide().ordinal() : null,
				zone.getZoneType() != null ? zone.getZoneType().ordinal() : null });
	}

	ResultSetExtractor<Zone> extractor = new ResultSetExtractor<Zone>() {

		@Override
		public Zone extractData(ResultSet rs) throws SQLException, DataAccessException {
			// TODO Auto-generated method stub
			Zone zone = new Zone();
			zone.setZoneCompositeKey(new ZoneCompositeKey(rs.getLong("instrument_token"), rs.getDate("base_min_date"),
					TimePeriod.values()[rs.getInt("time_period")]));
			return zone;
		}

	};

	public List<Zone> select() {
		List<Zone> zones = jdbcTemplate.query("select * from zone", new BeanPropertyRowMapper(Zone.class));

		return zones;
	}

	public Zone findLastByZoneCompositeKeyTimePeriodAndZoneCompositeKeyInstrumentToken(TimePeriod tp,
			Long instrumentToken) {
		Zone zones = (Zone) jdbcTemplate.queryForObject("select * from zone where  instrument_token =? and time_period=? order by desc start_date limit 1", new BeanPropertyRowMapper(Zone.class));

		return zones;
	}

	public void execute(String string) {
		this.jdbcTemplate.execute(string);
		
	}
}
