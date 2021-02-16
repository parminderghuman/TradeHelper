package com.parminder.trade.repository.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.parminder.trade.bo.CandleDataMinuteKey;
import com.parminder.trade.bo.MinuteCandleData;

@Service
public class MinuteInstrumentDataCandleRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void createTableIfNotExist(long instrumentId) {

		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `" + instrumentId + "inminute_candle_data` (\n"
				+ "  `complete_date` datetime(6) NOT NULL,\n" + "  `instrument_token` bigint NOT NULL,\n"
				+ "  `close` float DEFAULT NULL,\n" + "  `date` int DEFAULT NULL,\n"
				+ "  `day_minute_value` int DEFAULT NULL,\n" + "  `exchange` varchar(255) DEFAULT NULL,\n"
				+ "  `hight` float DEFAULT NULL,\n" + "  `hour` int DEFAULT NULL,\n" + "  `low` float DEFAULT NULL,\n"
				+ "  `minute` int DEFAULT NULL,\n" + "  `month` int DEFAULT NULL,\n" + "  `open` float DEFAULT NULL,\n"
				+ "  `open_interest` bigint DEFAULT NULL,\n" + "  `symbol` varchar(255) DEFAULT NULL,\n"
				+ "  `volume` bigint DEFAULT NULL,\n" + "  `year` int DEFAULT NULL,\n"
				+ "  PRIMARY KEY (`complete_date`,`instrument_token`)\n" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
	}

	public void saveAll(List<MinuteCandleData> candles, long instrumentId,boolean update) throws SQLException {
		int batchSize = 100;
		createTableIfNotExist(instrumentId);
		Connection connection = jdbcTemplate.getDataSource().getConnection();
		connection.setAutoCommit(false);
		String sql = "insert IGNORE into `" + instrumentId
				+ "inminute_candle_data` (complete_date,exchange,symbol,instrument_token,hight,open,close,low,year,month,date,hour,minute,day_minute_value,open_interest,volume) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		if(update) {
			 sql = "insert  into `" + instrumentId
					+ "inminute_candle_data` (complete_date,exchange,symbol,instrument_token,hight,open,close,low,year,month,date,hour,minute,day_minute_value,open_interest,volume) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
					+ " ON DUPLICATE KEY UPDATE hight = VALUES(hight),open = VALUES(open),close = VALUES(close),low = VALUES(low),open_interest = VALUES(open_interest),volume = VALUES(volume)";
		
		}
		PreparedStatement ps = connection.prepareStatement(sql);
		int count = 0;
		for (MinuteCandleData mcd : candles) {
			int j = 0;
			ps.setTimestamp(++j, new Timestamp(mcd.getCompleteDate().getTime()));
			ps.setString(++j, mcd.getExchange());
			ps.setString(++j, mcd.getSymbol());
			ps.setLong(++j, mcd.getInstrumentToken());

			ps.setFloat(++j, mcd.getHight());
			ps.setFloat(++j, mcd.getOpen());
			ps.setFloat(++j, mcd.getClose());
			ps.setFloat(++j, mcd.getLow());

			ps.setInt(++j, mcd.getYear());
			ps.setInt(++j, mcd.getMonth());
			ps.setInt(++j, mcd.getDate());
			ps.setInt(++j, mcd.getHour());

			ps.setInt(++j, mcd.getMinute());
			ps.setInt(++j, mcd.getDayMinute());
			ps.setLong(++j, mcd.getOpenInterest());
			ps.setLong(++j, mcd.getVolume());
			ps.addBatch();
			if (count % batchSize == 0) {
				ps.executeBatch();
			}

		}
		ps.executeBatch();
		connection.commit();
		connection.setAutoCommit(true);
		connection.close();
		System.out.println();

	}

	public RowMapper<MinuteCandleData> minutCandleRowMapper = new RowMapper<MinuteCandleData>() {

		@Override
		public MinuteCandleData mapRow(ResultSet rs, int rowNum) throws SQLException {
			MinuteCandleData mcd = new MinuteCandleData();
			mcd.setCandleDataMinuteKey(
					new CandleDataMinuteKey(rs.getLong("instrument_token"), rs.getTimestamp("complete_date")));
			mcd.setClose(rs.getFloat("close"));
			mcd.setDate(rs.getInt("date"));
			mcd.setMinute(rs.getInt("minute"));
			mcd.setHour(rs.getInt("hour"));
			mcd.setMonth(rs.getInt("month"));
			mcd.setYear(rs.getInt("year"));
			mcd.setDayMinute(rs.getInt("day_minute_value"));
			mcd.setOpenInterest(rs.getLong("open_interest"));
			mcd.setExchange(rs.getString("exchange"));
			mcd.setSymbol(rs.getString("symbol"));
			mcd.setHight(rs.getFloat("hight"));
			mcd.setOpen(rs.getFloat("open"));
			mcd.setLow(rs.getFloat("low"));
			mcd.setVolume(rs.getLong("volume"));
			return mcd;
		}

	};

	public Date findLargestDate(long instrumentId) {
		// TODO Auto-generated method stub
		try {
			MinuteCandleData candleData = jdbcTemplate.queryForObject(
					"select * from `" + instrumentId + "inminute_candle_data` order by complete_date desc limit 1",
					minutCandleRowMapper);
			if (candleData != null) {
				return candleData.getCompleteDate();
			}

		} catch (EmptyResultDataAccessException | IncorrectResultSetColumnCountException e) {
		}
		return null;
	}

	public int countByCandleDataMinuteKeyInstrumentTokenAndCandleDataMinuteKeyCompleteDateLessThanEqualAndCandleDataMinuteKeyCompleteDateGreaterThanEqual(
			long instrumentId, Date endDate, Date startDate) {
		return jdbcTemplate.queryForObject(
				"select count(*) from `" + instrumentId
						+ "inminute_candle_data` where complete_date<= ? and complete_date >= ?",
				new Object[] { endDate, startDate }, Integer.class);
	}
	
	
}
