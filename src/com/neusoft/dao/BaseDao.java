package com.neusoft.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T insert(T entity);
    T update(T entity);
    boolean deleteById(ID id);
    boolean forceDeleteById(ID id);
    List<T> findAllIncludingDeleted();
}