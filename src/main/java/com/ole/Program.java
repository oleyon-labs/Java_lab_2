package com.ole;

import com.ole.Expression.ExpressionSolver;

import java.util.List;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) throws Exception {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        Scanner in = new Scanner(System.in);
        System.out.print("Input a string: ");
        String expression = in.nextLine();


        List<String> res=expressionSolver.convertToReversePolishNotation(expression);
        for (String token:
             res) {
            System.out.println(token);
        }
        System.out.println();
        System.out.println(expressionSolver.convertToReversePolishNotation(expression));

        System.out.println(expression);
        System.out.println(expressionSolver.evaluate(expression));
    }
}
