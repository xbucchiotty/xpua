package actor.message;

import actor.Message;
import org.springframework.jdbc.core.RowMapper;

public class Extract<T> implements Message {

    public final String sqlQuery;
    public final RowMapper<T> rowMapper;

    public Extract(String sqlQuery, RowMapper<T> rowMapper) {
        this.sqlQuery = sqlQuery;
        this.rowMapper = rowMapper;
    }
}
