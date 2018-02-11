package br.org.meiaentrada.validadorcie.configuration;


public class GlobalConstants {

    public static final String URL_FOTOS = "https://s3-sa-east-1.amazonaws.com/meiaentrada-publico-prod/";
    public static final String URL_CAPTURAS = "https://klkst1hbza.execute-api.sa-east-1.amazonaws.com/validador_api_4";
    public static final String URL_CHAVES = "https://prod.meiaentrada.org.br/public/downloads/crl";
    public static final String URL_CPF = "https://r4yl4or3t4.execute-api.sa-east-1.amazonaws.com/validador_api_8/";

    public static final String ERRO_INVALIDO = "\n     Documento inválido     \n";
    public static final String ERRO_EXPIRADO = "\n     Documento expirado     \n";
    public static final String ERRO_REVOGADO = "\n     Documento revogado     \n";
    public static final String DOC_VALIDO = "\n     Documento válido     \n";

    public static final String URL_VALIDATE_OPERADOR =     "https://prod.meiaentrada.org.br/public/operador-codigo-acesso";

    public static final String URL_VALIDATE_CODIGO_USO_AND_DT_NASCIMENTO =
            "https://qczjzea7ah.execute-api.sa-east-1.amazonaws.com/validador_api_3?codigoAcesso=%s&dataNascimento=%s&codigoUso=%s&evento=%s";

    public static final String PREFERENCE_FILE_KEY = "val_shared_pref";

}
