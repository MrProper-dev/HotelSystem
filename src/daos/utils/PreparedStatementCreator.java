package daos.utils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PreparedStatementCreator{
    
    private List<Object> params;
    private StringBuilder root;
    private StringBuilder values;
    private StringBuilder where;
    private StringBuilder end;

    public PreparedStatementCreator(String select){
        root = new StringBuilder(select);
        values = new StringBuilder();
        where = new StringBuilder();
        end = new StringBuilder();
        params = new ArrayList<>();
    }

    public void addWhereAndCondition(String condition){
        where.append(" AND ");
        where.append(condition);
    }

    public void addValue(String value){
        values.append(" ");
        values.append(value);
        values.append(",");
    }

    public void addReturning(String value){
        end.append(" RETURNING ");
        end.append(value);
    }
    
    public void addLimit(){
        end.append(" LIMIT ?");
    }

    public void addOffset(){
        end.append(" OFFSET ?");
    }

    public void addParam(Object param){
        params.add(param);
    }

    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
        if(!where.isEmpty()){
            where.delete(0, 5);
            where.insert(0, " WHERE ");
        }

        if(!values.isEmpty()){
            values.delete(values.length()-1, values.length());
        }

        root.append(values.toString());
        where.append(end.toString());
        root.append(where.toString());
        
        System.out.println(root.toString());
        PreparedStatement statement = connection.prepareStatement(root.toString());
        addParams(statement);
        return statement;
    }

    private void addParams(PreparedStatement statement) throws SQLException{
        for(int i = 0; i<params.size(); i++) {
            if (params.get(i) instanceof String) {
                statement.setString(i+1, (String) params.get(i));
            } else if (params.get(i) instanceof Integer) {
                statement.setInt(i+1, (Integer) params.get(i));
            } else if (params.get(i) instanceof Long) {
                statement.setLong(i+1, (Long) params.get(i));
            } else if (params.get(i) instanceof Double) {
                statement.setDouble(i+1, (Double) params.get(i));
            } else if (params.get(i) instanceof Float) {
                statement.setFloat(i+1, (Float) params.get(i));
            } else if (params.get(i) instanceof Boolean) {
                statement.setBoolean(i+1, (Boolean) params.get(i));
            } else if (params.get(i) instanceof java.util.Date || params.get(i) instanceof java.sql.Date || params.get(i) instanceof java.sql.Timestamp) {
                statement.setTimestamp(i+1, params.get(i) instanceof java.sql.Timestamp ? (java.sql.Timestamp) params.get(i) : new java.sql.Timestamp(((java.util.Date) params.get(i)).getTime()));
            } else if (params.get(i) instanceof BigDecimal) {
                statement.setBigDecimal(i+1, (BigDecimal) params.get(i));
            } else {
                statement.setObject(i+1, params.get(i));
            }
        }
    }

}
