package dao;

import java.util.List;
import java.util.Optional;

/**
 * 基础数据访问接口
 * 提供通用的数据库操作方法，所有DAO接口都应继承此接口
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */

public interface BaseDao<T, ID> {

    /**
     * 查询所有未删除的记录
     * @return 实体列表
     */

    List<T> findAll();

    /**
     * 根据ID查询记录
     * @param id 主键ID
     * @return 包含实体的Optional对象
     */

    Optional<T> findById(ID id);

    /**
     * 插入新记录
     * @param entity 要插入的实体对象
     * @return 插入后的实体对象
     */

    T insert(T entity);

    /**
     * 更新记录
     * @param entity 要更新的实体对象
     * @return 更新后的实体对象
     */

    T update(T entity);

    /**
     * 根据ID软删除记录（逻辑删除）
     * @param id 主键ID
     * @return 删除成功返回true，否则返回false
     */

    boolean deleteById(ID id);

    /**
     * 根据ID强制删除记录（物理删除）
     * @param id 主键ID
     * @return 删除成功返回true，否则返回false
     */

    boolean forceDeleteById(ID id);

    /**
     * 查询所有记录，包括已删除的记录
     * @return 实体列表
     */

    List<T> findAllIncludingDeleted();
}