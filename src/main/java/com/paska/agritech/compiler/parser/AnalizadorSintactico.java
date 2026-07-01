package com.paska.agritech.compiler.parser;

import com.paska.agritech.compiler.lexer.Token;
import com.paska.agritech.compiler.lexer.TipoToken;
import java.util.List;

/**
 * ANALIZADOR SINTACTICO (Parser)  -  Tema del curso.
 *
 * Implementa un analizador descendente recursivo que reconoce el lenguaje
 * definido por la siguiente GRAMATICA LIBRE DE CONTEXTO (GLC):
 *
 *   <consulta>  ::= <comando> <metrica> <fuente> <filtro_opt>
 *   <comando>   ::= CONSULTAR | MONITOREAR | ALERTAR
 *   <metrica>   ::= HUMEDAD | TEMPERATURA | BATERIA
 *   <fuente>    ::= DE PARCELA NUMERO | DE SENSOR NUMERO
 *   <filtro_opt>::= DONDE VALOR OPERADOR NUMERO | epsilon
 *   <operador>  ::= < | > | <= | >= | =
 *   NUMERO      ::= (definido por el Automata Finito en el lexer)
 *
 * Cada no terminal de la GLC se mapea a un metodo del parser. Si la secuencia
 * de tokens no se ajusta a la gramatica se lanza un error sintactico.
 */
public class AnalizadorSintactico {

    private final List<Token> tokens;
    private int actual = 0;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
    }

    /** Punto de entrada: <consulta> */
    public NodoConsulta parsear() throws ErrorSintactico {
        NodoConsulta nodo = new NodoConsulta();

        // <comando>
        Token t = consumir(TipoToken.COMANDO, "Se esperaba un comando (CONSULTAR, MONITOREAR o ALERTAR)");
        nodo.setComando(t.getLexema());

        // <metrica>
        t = consumir(TipoToken.METRICA, "Se esperaba una metrica (HUMEDAD, TEMPERATURA o BATERIA)");
        nodo.setMetrica(t.getLexema());

        // <fuente> ::= DE (PARCELA | SENSOR) NUMERO
        consumir(TipoToken.DE, "Se esperaba la palabra 'DE'");
        if (verificar(TipoToken.PARCELA)) {
            avanzar();
            nodo.setTipoFuente("PARCELA");
        } else if (verificar(TipoToken.SENSOR)) {
            avanzar();
            nodo.setTipoFuente("SENSOR");
        } else {
            throw new ErrorSintactico("Se esperaba 'PARCELA' o 'SENSOR'", actualToken());
        }
        Token num = consumir(TipoToken.NUMERO, "Se esperaba el identificador numerico de la fuente");
        nodo.setIdFuente((int) Double.parseDouble(num.getLexema()));

        // <filtro_opt> ::= DONDE VALOR OPERADOR NUMERO | epsilon
        if (verificar(TipoToken.DONDE)) {
            avanzar();
            consumir(TipoToken.VALOR, "Se esperaba la palabra 'VALOR' luego de 'DONDE'");
            Token op = consumir(TipoToken.OPERADOR, "Se esperaba un operador (<, >, <=, >=, =)");
            Token val = consumir(TipoToken.NUMERO, "Se esperaba un numero luego del operador");
            nodo.setTieneFiltro(true);
            nodo.setOperador(op.getLexema());
            nodo.setValorFiltro(Double.parseDouble(val.getLexema()));
        }

        // Debe terminar la entrada (EOF). Si hay tokens extra => error.
        consumir(TipoToken.EOF, "Tokens inesperados al final de la consulta");
        return nodo;
    }

    // ---- utilidades del parser ----

    private Token consumir(TipoToken tipo, String mensaje) throws ErrorSintactico {
        if (verificar(tipo)) return avanzar();
        throw new ErrorSintactico(mensaje, actualToken());
    }

    private boolean verificar(TipoToken tipo) {
        return actualToken().getTipo() == tipo;
    }

    private Token avanzar() {
        return tokens.get(actual++);
    }

    private Token actualToken() {
        return tokens.get(actual);
    }

    /** Excepcion de error sintactico con contexto. */
    public static class ErrorSintactico extends Exception {
        public ErrorSintactico(String mensaje, Token token) {
            super(mensaje + ". Se encontro: " + token);
        }
    }
}
