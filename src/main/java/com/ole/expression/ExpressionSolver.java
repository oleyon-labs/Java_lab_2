package com.ole.expression;

import java.util.*;
import java.util.function.Function;

import com.ole.expression.exceptions.*;

/**
 * Класс для вычисления выражений.
 * Хранит в себе названия переменных и ассоциированные им значения.
 * Также хранит названия функций и ассоциированные с ними функции.
 * Умеет добавлять новые переменные и функции.
 */
public class ExpressionSolver {

    /**
     * Устанавливает стартовые списки функций и операторов
     */
    public ExpressionSolver(){

        variables = new HashMap<>();

        constants = new HashMap<>();
        constants.put("PI", Math.PI);
        constants.put("E", Math.E);


        functions = new HashMap<>();
        functions.put("sin", Math::sin);
        functions.put("cos", Math::cos);
        functions.put("tan", Math::tan);
        functions.put("log", Math::log);
        functions.put("log10", Math::log10);
    }

    /**
     * Основной метод вычисления математического выражения. Умеет добавлять новые переменные.
     * @param expression Строка математического выражения, которая может также состоять из 1 присваивания для сохраниения значения переменной.
     * @return Значениевыражения
     * @throws ExpressionException Выбрасывается при ошибках в вычислении результата выражения или присваивания значения переменной
     */
    public double evaluate(String expression) throws ExpressionException {
        var expressions = expression.split("=");
        if(expressions.length>2)
            throw new ExpressionException("Кол-во присваиваний больше 1");
        else if(expressions.length==1)
            return calculate(expressions[0]);
        else if(expressions.length==0)
            throw new ExpressionException("Неверное выражение");
        else{
            String variable=expressions[0];
            if(!isValidName(variable))
                throw new ExpressionException("Неподходящее имя переменной: " + variable);
            if(functions.containsKey(variable))
                throw new ExpressionException("Имя переменной совпадает со значением функции: " + variable);
            double result= calculate(expressions[1]);
            variables.put(variable, result);
            return result;
        }
    }

    /**
     * Возвращает список функций
     * @return Список функций
     */
    public String[] getFunctions(){
        return functions.keySet().toArray(new String[0]);
    }

    /**
     * Пытается установить новую функцию
     * @param name Название функции
     * @param function Функция
     * @return Результат операции
     */
    public boolean setFunction(String name, Function<Double,Double> function) {
        if(constants.containsKey(name)||variables.containsKey(name))
            return false;
        functions.put(name,function);
        return true;
    }

    /**
     * Возвращает список переменных
     * @return Список переменных
     */
    public String[] getVariables(){
        String[] result = new String[variables.size()];
        var keys=variables.keySet().toArray(new String[0]);
        for (int i = 0; i < variables.size(); i++) {
            result[i] = keys[i] + " : " + variables.get(keys[i]);
        }
        return result;
    }

    /**
     * Пытается установить новое значение переменной
     * @param name Название переменной
     * @param value Значение
     * @return Результат операции
     */
    public boolean setVariable(String name, Double value){
        if(!isValidName(name) || constants.containsKey(name) || functions.containsKey(name))
            return false;
        variables.put(name, value);
        return true;
    }

    /**
     * Возвращает список констант
     * @return Список констант
     */
    public String[] getConstants(){
        String[] result = new String[constants.size()];
        var keys=constants.keySet().toArray(new String[0]);
        for (int i = 0; i < constants.size(); i++) {
            result[i] = keys[i] + " : " + constants.get(keys[i]);
        }
        return result;
    }

    /**
     * Список стандартных операторов
     * _ является репрезентацией унарного минуса
     */
    private final List<String> standartOperators = List.of(new String[]{"(",")","+","-","*","/","^","_"});

    /**
     * Словарь переменных
     */
    private final Map<String, Double> variables;

    /**
     * Список констант
     */
    private final Map<String, Double> constants;

    /**
     * Список функций
     */
    private final Map<String, Function<Double, Double>> functions;

    /**
     * Проверяет имя переменной на правильность написания
     * @param name Имя переменной
     * @return true в случае, если переменная начинается с буквы и состоить из букв и цифр, иначе false.
     */
    private boolean isValidName(String name) {
        boolean isValid = Character.isLetter(name.charAt(0));
        for (int i = 1; i < name.length()&&isValid; i++) {
            if(!(Character.isDigit(name.charAt(i)) || Character.isLetter(name.charAt(i))))
                isValid=false;
        }
        return isValid;
    }

    /**
     * Занимается непосредственным вычислением выражения без присваиваний.
     * @param expression Математическое выражение
     * @return Результат вычисления выражения
     * @throws ExpressionException Выбрасывается при ошибках в вычислении результата выражения
     */
    private double calculate(String expression) throws ExpressionException {

        List<String> postfixNotation = convertToReversePolishNotation(expression);
        Stack<Double> stack = new Stack<>();

        try {
        for (String token:
             postfixNotation) {
            if(standartOperators.contains(token)) {
                switch (token) {
                    case "+" -> stack.push(stack.pop() + stack.pop());
                    case "-" -> stack.push(-stack.pop() + stack.pop());
                    case "*" -> stack.push(stack.pop() * stack.pop());
                    case "_" -> //унарный минус
                            stack.push(-stack.pop());
                    case "/" -> {
                        double right = stack.pop();
                        double left = stack.pop();
                        if (right == 0)
                            throw new ExpressionException("деление на ноль");
                        stack.push(left / right);
                    }
                    case "^" -> {
                        double power = stack.pop();
                        double base = stack.pop();
                        if (base < 0)
                            throw new ExpressionException("возведение отрицательного числа в степень");
                        stack.push(Math.pow(base, power));
                    }
                }
            }
            else if(constants.containsKey(token)){
                stack.push(constants.get(token));
            }
            else if(functions.containsKey(token)){
                stack.push(functions.get(token).apply(stack.pop()));
            }
            else if(variables.containsKey(token)){
                stack.push(variables.get(token));
            }else{
                try {
                    stack.push(Double.parseDouble(token));
                }
                catch (Exception ex)
                {
                    throw new ExpressionException("неизвестный токен " + token);
                }
            }
        }

        }
        catch(EmptyStackException ex)
        {
            throw new ExpressionException("Неправильное расположение операторов");
        }
        if(stack.size() != 1)
            throw new ExpressionException("Неправильное расположение операторов");
        if(Double.isNaN(stack.peek()))
            throw new ExpressionException("Некорректные значения аргументов функций");
        return stack.pop();
    }

    /**
     * Проверяет выражение на правильность расстановки скобок
     * @param input выражение
     * @return true, если скобки расставлены верно, иначе false.
     */
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
        return count == 0;
    }

    /**
     * Конвертирует выражение в список терминов, стоящих в порядке обратной польской записи.
     * @param expression Выражение
     * @return Список терминов, стоящий в порядке обратной польской записи.
     * @throws ExpressionException Выбрасывается при ошибках в вычислении списка терминов
     */
    private List<String> convertToReversePolishNotation(String expression) throws ExpressionException {
        if(!checkParenthesis(expression))
            throw new ExpressionException("Несовпадение открывающих и закрывающих скобок");

        List<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        List<String> tokens = separate(expression);

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
                        if(token.equals("-") && tokens.get(index - 1).equals("("))
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

    /**
     * Возвращает приоритет токена
     * @param token Токен
     * @return Приоритет токена. 0 - низкий приоритет, 4 - самый высокий.
     */
    private int getPriority(String token) {
        return switch (token) {
            case "(", ")" -> 0;
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 4;
        };
    }

    /**
     * Разделяет выражение на список токенов
     * @param input Математическое выражение
     * @return Список токенов выражения
     */
    private List<String> separate(String input){
        int pos = 0;
        int n=input.length();

        List<String> tokens = new ArrayList<>();

        while(pos<n){
            StringBuilder token = new StringBuilder("" + input.charAt(pos));

            if(!standartOperators.contains(input)) {
                if(Character.isDigit(input.charAt(pos))) {
                    for (int i = pos+1; i < n && (Character.isDigit(input.charAt(i)) || input.charAt(i)=='.'); i++) {
                        token.append(input.charAt(i));
                    }
                }
                else if(Character.isLetter(input.charAt(pos))){
                    for (int i = pos+1; i < n && (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i))); i++) {
                        token.append(input.charAt(i));
                    }
                }
            }
            if(!token.toString().contains(" "))
                tokens.add(token.toString());
            pos+=token.length();
        }
        return tokens;
    }

}