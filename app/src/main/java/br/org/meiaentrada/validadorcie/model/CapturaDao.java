package br.org.meiaentrada.validadorcie.model;


public interface CapturaDao extends GenericDao<Captura, Integer> {

    Captura next();

}
