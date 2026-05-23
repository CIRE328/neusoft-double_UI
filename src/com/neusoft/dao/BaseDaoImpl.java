package com.neusoft.dao;

import com.neusoft.pojo.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseDaoImpl<T, ID> implements BaseDao<T, ID> {

    protected abstract String getTableName();
    protected abstract String getIdColumnName();
    protected abstract T mapRowToEntity(ResultSet rs) throws SQLException;

    // 获取所有非逻辑删除的记录条件
    protected String getNotDeletedCondition() {
        return "is_deleted = 0";
    }

    // 获取所有记录（包括已删除）的 SQL 后缀
    protected String getBaseSelectSQL() {
        return "SELECT * FROM " + getTableName();
    }

    @Override
    public List<T> findAll() {
        String sql = getBaseSelectSQL() + " WHERE " + getNotDeletedCondition();
        return executeQuery(sql, null);
    }

    @Override
    public Optional<T> findById(ID id) {
        String sql = getBaseSelectSQL() + " WHERE " + getIdColumnName() + " = ? AND " + getNotDeletedCondition();
        List<T> list = executeQuery(sql, ps -> ps.setObject(1, id));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public T insert(T entity) {
        /*动态生成 INSERT 语句，使用反射获取所有非空字段（或所有字段）
        为了简化，子类必须重写此方法或提供 insertMap，但这里展示通用方法
        由于反射较复杂，且不同表字段不同，建议子类实现具体 insert
        或者使用回调接口。这里我们让子类实现 protected void setInsertParameters(PreparedStatement ps, T entity) 等方法
        更简单：在子类中直接写具体的 insert SQL。为了减少工作量，可以为每个实体手动写 insert/update 方法。
        因此 BaseDaoImpl 不实现 insert/update，留给子类实现。但为了满足接口，可以抛出 UnsupportedOperationException */
        throw new UnsupportedOperationException("子类必须实现 insert 方法");
    }

    @Override
    public T update(T entity) {
        throw new UnsupportedOperationException("子类必须实现 update 方法");
    }

    @Override
    public boolean deleteById(ID id) {
        String sql = "UPDATE " + getTableName() + " SET is_deleted = 1 WHERE " + getIdColumnName() + " = ?";
        return executeUpdate(sql, ps -> ps.setObject(1, id)) > 0;
    }

    @Override
    public boolean forceDeleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return executeUpdate(sql, ps -> ps.setObject(1, id)) > 0;
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        String sql = getBaseSelectSQL();
        return executeQuery(sql, null);
    }

    //JDBC 辅助方法
    protected List<T> executeQuery(String sql, ParameterSetter setter) {
        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEntity(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    protected int executeUpdate(String sql, ParameterSetter setter) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @FunctionalInterface
    protected interface ParameterSetter {
        void setParameters(PreparedStatement ps) throws SQLException;
    }
}