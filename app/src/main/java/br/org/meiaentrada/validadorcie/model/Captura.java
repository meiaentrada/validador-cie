package br.org.meiaentrada.validadorcie.model;

import br.org.meiaentrada.validadorcie.util.HashUtil;


public class Captura {

    private int id;
    private String certificado;
    private String status;
    private String horario;
    private String evento;
    private String latitude;
    private String longitude;
    private String idDispositivo;
    private String codigoAcesso;

    public Captura() {
    }

    public Captura(String certificado, String status, String horario, String evento,
                   String latitude, String longitude, String idDispositivo) {

        this.certificado = certificado;
        this.status = status;
        this.horario = horario;
        this.evento = evento;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idDispositivo = idDispositivo;

    }

    public Captura(int id, String certificado, String status, String horario, String evento,
                   String latitude, String longitude, String idDispositivo) {

        this.id = id;
        this.certificado = certificado;
        this.status = status;
        this.horario = horario;
        this.evento = evento;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idDispositivo = idDispositivo;

    }

    public String getHashCertificado() {
        return certificado != null ? HashUtil.getMD5(certificado) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(String idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

}