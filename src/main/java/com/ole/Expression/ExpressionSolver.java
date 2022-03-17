package com.ole.Expression;

import java.util.*;

public class ExpressionSolver {

    private Map<String, Double> variables;



    public final List<String> functions;

    public final List<String> standartOperators;

    public ExpressionSolver(){

        variables = new HashMap<String, Double>();

        functions = List.of(new String[] {"sin", "cos", "tan", "cotan", "factor"});

        standartOperators = List.of(new String[]{"(",")","+","-","*","/","^","_"});
    }

    private boolean isDigit(char c){
        if(c>='0' && c<='9')
            return true;
        return false;
    }



    public double evaluate(String expression) throws Exception {

        List<String> postfixNotation = convertToReversePolishNotation(expression);
        Stack<Double> stack = new Stack<Double>();

        try {
        for (String token:
             postfixNotation) {
            if(standartOperators.contains(token)) {
                switch (token){
                    case "+":
                        stack.push(stack.pop()+stack.pop());
                        break;
                    case "-":
                        stack.push(-stack.pop()+stack.pop());
                        break;
                    case "*":
                        stack.push(stack.pop()*stack.pop());
                        break;
                    case "_": //унарный минус
                        stack.push(-stack.pop());
                        break;
                    case "/":
                        double right = stack.pop();
                        double left = stack.pop();
                        if(right==0)
                            throw new ArithmeticException("деление на ноль");
                        stack.push(left/right);
                        break;
                    case "^":
                        double power=stack.pop();
                        double base=stack.pop();
                        if(base<0)
                            throw  new ArithmeticException("возведение отрицательного числа в степень");
                        stack.push(Math.pow(base, power));
                }
            }
            else if(functions.contains(token)){
                if(token.equals("sin")) {
                    stack.push(Math.sin(stack.pop()));
                }
            }
            else if(variables.containsKey(token)){
                stack.push(variables.get(token));
            }else{
                try {
                    stack.push(Double.parseDouble(token));
                }
                catch (Exception ex)
                {
                    throw new Exception("неизвестный токен " + token);
                }
            }
        }

        }
        catch(EmptyStackException ex)
        {
            System.out.println("упс");
        }
        catch (Exception ex)
        {
            throw ex;
        }
        if(stack.size()>1)
            throw new ArithmeticException("Неправильное расположение операторов");
        return stack.pop();
    }

    private boolean checkParenthesis(String input) {
        int count=0;

        for (int i = 0; i < input.length(); i++) {
            if(input.charAt(i)=='(')
                count++;
            else if(input.charAt(i)==')')
                count--;
            if(count<0)
                return false;
        }
        if(count!=0) return false;
        return true;
    }

    public List<String> convertToReversePolishNotation(String expression) {
        if(!checkParenthesis(expression))
            throw new ArithmeticException("Несовпадение открывающих и закрывающих скобок");

        List<String> output = new ArrayList<>();

        //List<Token> tokens = new ArrayList<>();

        Stack<String> stack = new Stack<>();
        int n=expression.length();


        List<String> tokens = separate(expression);
        //for (String token : tokens) {
        for (int index = 0; index < tokens.size(); index++) {

            String token=tokens.get(index);
            if(standartOperators.contains(token)){
                if(stack.size()>0 && !token.equals("(")){
                    //если скобка, то переносим все операторы в стеке до закрывающие скобки
                    if(token.equals(")"))
                    {
                        String s=stack.pop();
                        while(!s.equals("("))
                        {
                            output.add(s);
                            s=stack.pop();
                        }
                    }
                    //по разному обрабатываем в зависимости от приоритета текущей операции
                    else if(getPriority(token)>getPriority(stack.peek())){
                        if(token.equals("-")&&(index==0||tokens.get(index-1).equals("(")))
                            stack.push("_");
                        else
                            stack.push(token);
                    }
                    else{
                        while(stack.size()>0&&(getPriority(token)<=getPriority(stack.peek()))) {
                            output.add(stack.pop());
                        }
                        stack.push(token);
                    }
                }
                else{
                    if(token.equals("-")&&(index==0||tokens.get(index-1).equals("(")))
                        stack.push("_");
                    else
                        stack.push(token);
                }
            }
            else {
                //так отделяем функцию от переменной
                if(index<tokens.size()-1&&tokens.get(index+1).equals("("))
                    stack.push(token);
                else //Добавление значения
                    output.add(token);
            }
        }
        //переносим весь остаток операторов из стека
        while(stack.size()>0)
            output.add(stack.pop());
        return output;
    }

    private int getPriority(String token) {
        return switch (token) {
            case "(", ")" -> 0;
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 4;
        };
    }

    private List<String> separate(String input){
        int pos = 0;
        int n=input.length();

        List<String> tokens = new ArrayList<>();

        while(pos<n){
            String token = "" + input.charAt(pos);

            if(!standartOperators.contains(input.charAt(pos))) {
                if(Character.isDigit(input.charAt(pos))) {
                    for (int i = pos+1; i < n && (Character.isDigit(input.charAt(i)) || input.charAt(i)=='.'); i++) {
                        token+=input.charAt(i);
                    }
                }
                else if(Character.isLetter(input.charAt(pos))){
                    for (int i = pos+1; i < n && (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i))); i++) {
                        token+=input.charAt(i);
                    }
                }
            }
            if(!token.contains(" "))
                tokens.add(token);
            pos+=token.length();
        }
        return tokens;
    }

}
