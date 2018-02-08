package br.org.meiaentrada.validadorcie.model;


public interface CapturaDao extends GenericDao<Captura, String> {

    Captura next();

}
