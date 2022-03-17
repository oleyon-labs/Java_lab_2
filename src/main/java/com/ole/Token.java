package com.ole;

public class Token {

    final Type dataType;
    final Object data;

    public Token(Type dataType, Object data){
        this.dataType=dataType;
        this.data=data;
    }


    public enum Type{
        NUMBER,
        VARIABLE,
        FUNCTION
    }

}
