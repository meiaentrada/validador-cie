package br.org.meiaentrada.validadorcie.model;

import java.io.Serializable;
import java.util.Optional;


public interface GenericDao<T, I extends Serializable> {

    <S extends T> S save(T entity);
    Optional<T> findById(I id);
    Iterable<T> findAll();
    int count();
    T update(T entity);
    void delete(I id);
    boolean existsById(I id);

}
