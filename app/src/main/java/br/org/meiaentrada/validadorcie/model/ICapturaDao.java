package br.org.meiaentrada.validadorcie.model;


public interface ICapturaDao extends IDatabaseHandler {

    @Override
    void add(Class<?> obj);

}
