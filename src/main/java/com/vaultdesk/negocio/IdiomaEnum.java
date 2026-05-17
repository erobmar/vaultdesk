package com.vaultdesk.negocio;

public enum IdiomaEnum {

    ES(1, "es"),
    EN(2, "en"),
    CAT(3, "cat"),
    AST(4, "ast");

    private final int idIdioma;
    private final String codigo;

    IdiomaEnum(int idIdioma, String codigo){
        this.idIdioma =  idIdioma;
        this.codigo = codigo;
    }

    // Métodos getter
    public int getIdIdioma(){
        return idIdioma;
    }

    public String getCodigo(){
        return codigo;
    }

    public static IdiomaEnum getCodigoDesdeId(int idIdioma){

        for(IdiomaEnum idioma : values()){

            if(idioma.getIdIdioma() == idIdioma){
                return idioma;
            }
        }
        throw new IllegalArgumentException();
    }

}
