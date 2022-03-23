package com.ole;

import com.ole.expression.exceptions.ExpressionException;
import com.ole.expression.ExpressionSolver;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        ExpressionSolver expressionSolver = new ExpressionSolver();
        Scanner in = new Scanner(System.in);

        System.out.println("""
                Эта программа умеет вычислять математические выражения.
                В строке выражений также разрешено одно присваивание переменной.
                Например a=5*(sin(PI/3)+1)""");

        //Добавление пользовательской функции округления
        expressionSolver.setFunction("round", Program::round);

        boolean isNotExiting = true;
        while (isNotExiting) {
            int choice = menu(in);

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Введите выражение: ");
                        String expression = in.nextLine();
                        System.out.println(expressionSolver.evaluate(expression));
                    } catch (ExpressionException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                case 2:
                    System.out.println("Введите название переменной");
                    System.out.print("> ");
                    String name = in.nextLine();
                    System.out.println("Введите значение переменной");
                    boolean isNotCorrectInput = true;
                    while (isNotCorrectInput) {
                        System.out.print("> ");
                        try {
                            String line = in.nextLine();
                            double value = Double.parseDouble(line);
                            isNotCorrectInput = false;
                            if (!expressionSolver.setVariable(name, value))
                                System.out.println("Некорректное имя переменной");
                        } catch (Exception ex) {
                            System.out.println("Некорректное значение");
                        }
                    }
                    break;
                case 3:
                    var variables = expressionSolver.getVariables();
                    for (var variable :
                            variables) {
                        System.out.println("  " + variable);
                    }
                    break;
                case 4:
                    var functions = expressionSolver.getFunctions();
                    for (var function :
                            functions) {
                        System.out.println("  " + function);
                    }
                    break;
                case 5:
                    var constants = expressionSolver.getConstants();
                    for (var constant :
                            constants) {
                        System.out.println("  " + constant);
                    }
                    break;
                case 6:
                    isNotExiting = false;
                    break;
            }
        }
    }

    public static int menu(Scanner scanner) {
        System.out.println("1. Вычислить выражение.");
        System.out.println("2. Присвоить переменной значение");
        System.out.println("3. Вывести список переменных");
        System.out.println("4. Вывести список функций");
        System.out.println("5. Вывести список констант");
        System.out.println("6. Выход");
        return correctIntInputInRange(1,6, scanner);
    }

    public static int correctIntInputInRange(int leftBound, int rightBound, Scanner scanner){
        while(true) {
            try {
                System.out.print("> ");
                String line = scanner.nextLine();
                int result = Integer.parseInt(line);
                if (result >= leftBound && result <= rightBound)
                    return result;
                System.out.println("Число выходит за диапазон ("+leftBound+", "+rightBound+")");
            } catch (Exception ex) {
                System.out.println("Введите число");
            }
        }
    }

    public static double round(double value){
        return (double) Math.round(value);
    }
}
