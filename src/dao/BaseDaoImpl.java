package dao;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseDaoImpl<T, ID> implements BaseDao<T, ID> {

    private final String tableName;
    private final String idColumn;
    private final Class<T> entityClass;
    private final Map<String, Field> columnToFieldMap;
    private final boolean useLogicDelete;

    public BaseDaoImpl(String tableName, String idColumn, Class<T> entityClass) {
        this(tableName, idColumn, entityClass, true);
    }

    public BaseDaoImpl(String tableName, String idColumn, Class<T> entityClass, boolean useLogicDelete) {
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.entityClass = entityClass;
        this.useLogicDelete = useLogicDelete;
        this.columnToFieldMap = buildColumnFieldMap();
    }

    private Map<String, Field> buildColumnFieldMap() {
        Map<String, Field> map = new HashMap<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = camelToSnake(field.getName());
            map.put(columnName, field);
        }
        return map;
    }

    private String camelToSnake(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String getSelectByIdSql() {
        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";
        if (useLogicDelete) sql += " AND is_deleted = 0";
        return sql;
    }

    private String getSelectAllSql(boolean includeDeleted) {
        if (!useLogicDelete) return "SELECT * FROM " + tableName;
        if (includeDeleted) return "SELECT * FROM " + tableName;
        else return "SELECT * FROM " + tableName + " WHERE is_deleted = 0";
    }

    private String getInsertSql() {
        List<String> columns = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
            String column = entry.getKey();
            if (column.equals(idColumn)) continue;
            columns.add(column);
            placeholders.add("?");
        }
        if (useLogicDelete && !columns.contains("is_deleted") && columnToFieldMap.containsKey("is_deleted")) {
            columns.add("is_deleted");
            placeholders.add("?");
        }
        return "INSERT INTO " + tableName + " (" + String.join(", ", columns) +
                ") VALUES (" + String.join(", ", placeholders) + ")";
    }

    private String getUpdateSql() {
        List<String> assignments = new ArrayList<>();
        for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
            String column = entry.getKey();
            if (column.equals(idColumn) || "is_deleted".equals(column)) continue;
            assignments.add(column + " = ?");
        }
        return "UPDATE " + tableName + " SET " + String.join(", ", assignments) +
                " WHERE " + idColumn + " = ?";
    }

    private String getLogicDeleteSql() {
        if (!useLogicDelete) {
            throw new UnsupportedOperationException("Table " + tableName + " does not support logical deletion.");
        }
        return "UPDATE " + tableName + " SET is_deleted = 1 WHERE " + idColumn + " = ?";
    }

    private String getForceDeleteSql() {
        return "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
    }

    protected List<T> executeQuery(String sql, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    T entity = entityClass.getDeclaredConstructor().newInstance();
                    for (Map.Entry<String, Field> entry : columnToFieldMap.entrySet()) {
                        String column = entry.getKey();
                        Field field = entry.getValue();
                        Object value = rs.getObject(column);
                        if (value != null) {
                            // 处理日期类型转换
                            if (field.getType() == java.util.Date.class) {
                                if (value instanceof Timestamp) {
                                    field.set(entity, new java.util.Date(((Timestamp) value).getTime()));
                                } else if (value instanceof java.time.LocalDateTime) {
                                    java.time.LocalDateTime ldt = (java.time.LocalDateTime) value;
                                    java.util.Date date = java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
                                    field.set(entity, date);
                                } else if (value instanceof java.sql.Date) {
                                    field.set(entity, new java.util.Date(((java.sql.Date) value).getTime()));
                                } else {
                                    field.set(entity, value);
                                }
                            } else if (field.getType() == java.time.LocalDateTime.class && value instanceof java.util.Date) {
                                java.util.Date date = (java.util.Date) value;
                                java.time.LocalDateTime ldt = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                                field.set(entity, ldt);
                            } else {
                                field.set(entity, value);
                            }
                        }
                    }
                    list.add(entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    protected int executeUpdate(String sql, Object... params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                Object value = params[i];
                if (value instanceof java.util.Date) {
                    value = new Timestamp(((java.util.Date) value).getTime());
                }
                ps.setObject(i + 1, value);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void setDefaultValues(T entity) {
        try {
            try {
                Field f = entityClass.getDeclaredField("createTime");
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    f.set(entity, new java.util.Date());
                }
            } catch (NoSuchFieldException ignored) {}
            try {
                Field f = entityClass.getDeclaredField("updateTime");
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    f.set(entity, new java.util.Date());
                }
            } catch (NoSuchFieldException ignored) {}
            try {
                Field f = entityClass.getDeclaredField("createBy");
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    f.set(entity, 0);
                }
            } catch (NoSuchFieldException ignored) {}
            try {
                Field f = entityClass.getDeclaredField("updateBy");
                f.setAccessible(true);
                if (f.get(entity) == null) {
                    f.set(entity, 0);
                }
            } catch (NoSuchFieldException ignored) {}
            if (useLogicDelete) {
                try {
                    Field f = entityClass.getDeclaredField("isDeleted");
                    f.setAccessible(true);
                    if (f.get(entity) == null) {
                        f.set(entity, 0);
                    }
                } catch (NoSuchFieldException ignored) {}
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<T> findAll() {
        return executeQuery(getSelectAllSql(false));
    }

    @Override
    public Optional<T> findById(ID id) {
        List<T> list = executeQuery(getSelectByIdSql(), id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public T insert(T entity) {
        setDefaultValues(entity);
        String sql = getInsertSql();
        List<String> insertColumns = columnToFieldMap.keySet().stream()
                .filter(col -> !col.equals(idColumn))
                .collect(Collectors.toList());
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (String col : insertColumns) {
                Field field = columnToFieldMap.get(col);
                Object value = field.get(entity);
                if (value instanceof java.util.Date) {
                    value = new Timestamp(((java.util.Date) value).getTime());
                }
                ps.setObject(index++, value);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Field idField = columnToFieldMap.get(idColumn);
                    if (idField != null) {
                        Object key = rs.getObject(1);
                        if (key instanceof Number) {
                            idField.set(entity, ((Number) key).intValue());
                        } else {
                            idField.set(entity, key);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public T update(T entity) {
        try {
            try {
                Field f = entityClass.getDeclaredField("updateTime");
                f.setAccessible(true);
                f.set(entity, new java.util.Date());
            } catch (NoSuchFieldException ignored) {}
            try {
                Field f = entityClass.getDeclaredField("updateBy");
                f.setAccessible(true);
                if (f.get(entity) == null) f.set(entity, 0);
            } catch (NoSuchFieldException ignored) {}
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        String sql = getUpdateSql();
        List<String> updateColumns = columnToFieldMap.keySet().stream()
                .filter(col -> !col.equals(idColumn) && !"is_deleted".equals(col))
                .collect(Collectors.toList());
        List<Object> params = new ArrayList<>();
        try {
            for (String col : updateColumns) {
                Field field = columnToFieldMap.get(col);
                Object value = field.get(entity);
                if (value instanceof java.util.Date) {
                    value = new Timestamp(((java.util.Date) value).getTime());
                }
                params.add(value);
            }
            Field idField = columnToFieldMap.get(idColumn);
            params.add(idField.get(entity));
            executeUpdate(sql, params.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public boolean deleteById(ID id) {
        if (!useLogicDelete) {
            return forceDeleteById(id);
        }
        return executeUpdate(getLogicDeleteSql(), id) > 0;
    }

    @Override
    public boolean forceDeleteById(ID id) {
        return executeUpdate(getForceDeleteSql(), id) > 0;
    }

    @Override
    public List<T> findAllIncludingDeleted() {
        return executeQuery(getSelectAllSql(true));
    }
}