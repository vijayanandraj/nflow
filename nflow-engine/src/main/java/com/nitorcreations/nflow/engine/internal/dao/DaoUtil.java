package com.nitorcreations.nflow.engine.internal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DaoUtil {

  static final FirstColumnLengthExtractor firstColumnLengthExtractor = new FirstColumnLengthExtractor();

  private DaoUtil() {
    // prevent instantiation
  }

  public static Timestamp toTimestamp(DateTime time) {
    return time == null ? null : new Timestamp(time.getMillis());
  }

  public static DateTime toDateTime(Timestamp time) {
    return time == null ? null : new DateTime(time.getTime());
  }

  static final class FirstColumnLengthExtractor implements ResultSetExtractor<Integer> {
    @Override
    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
      return rs.getMetaData().getColumnDisplaySize(1);
    }
  }
}
