package com.ole.expression;

import com.ole.expression.exceptions.ExpressionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpressionSolverTest {

    @Test
    @DisplayName("Тест правильности вычисления значений выражений")
    void evaluate() throws ExpressionException {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        Assertions.assertEquals(expressionSolver.evaluate("5^2.1 + 4.65/3 *(7/sin(PI/3))"),Math.pow(5, 2.1)+4.65/3*(7/Math.sin(Math.PI/3)));
        Assertions.assertEquals(expressionSolver.evaluate(" 3 "),3.0);
        Assertions.assertEquals(expressionSolver.evaluate("13    /  2  *5"),13.0/2*5);
        Assertions.assertEquals(expressionSolver.evaluate("var1=13+5"),13.0+5.0);
        Assertions.assertEquals(expressionSolver.evaluate("var1"),18.0);
        Assertions.assertEquals(expressionSolver.evaluate("5+var1/3*cos(var1)"),5+18.0/3*Math.cos(18.0));
    }

    @Test
    @DisplayName("Тест добавления функций")
    void setFunction() throws ExpressionException {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        expressionSolver.setFunction("triple", (a) -> a*3);
        Assertions.assertEquals(expressionSolver.evaluate("triple(13.3)"),13.3*3);
    }

    @Test
    @DisplayName("Тест добавления переменных")
    void setVariable() throws ExpressionException {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        expressionSolver.setVariable("var1", Math.tan(1.2));
        Assertions.assertEquals(expressionSolver.evaluate("var1"),Math.tan(1.2));
    }
}