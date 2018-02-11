package br.org.meiaentrada.validadorcie.model;

import java.io.Serializable;


public interface GenericDao<T, I extends Serializable> {

    T save(T entity);
    T findById(I id);
    Iterable<T> findAll();
    int count();
    T update(T entity);
    void delete(I id);
    boolean existsById(I id);

}
