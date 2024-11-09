package com.example.mid_term;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private StringBuilder currentInput = new StringBuilder();
    private boolean hasDot = false;
    private int openBracketCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);


        int[] numButtonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int id : numButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(view -> {
                Button btn = (Button) view;
                currentInput.append(btn.getText().toString());
                updateDisplay();
            });
        }


        int[] operatorButtonIds = {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide};
        for (int id : operatorButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(view -> {
                Button btn = (Button) view;
                currentInput.append(" " + btn.getText().toString() + " ");
                hasDot = false;
                updateDisplay();
            });
        }


        Button btnDot = findViewById(R.id.btnDot);
        btnDot.setOnClickListener(view -> {
            if (!hasDot) {
                if (currentInput.length() == 0 || isLastCharacterOperator() || isLastCharacterOpenBracket()) {
                    currentInput.append("0.");
                } else {
                    currentInput.append(".");
                }
                hasDot = true;
                updateDisplay();
            }
        });


        Button btnDel = findViewById(R.id.btnDel);
        btnDel.setOnClickListener(view -> {
            if (currentInput.length() > 0) {
                if (currentInput.charAt(currentInput.length() - 1) == '.') {
                    hasDot = false;
                } else if (currentInput.charAt(currentInput.length() - 1) == '(') {
                    openBracketCount--;
                } else if (currentInput.charAt(currentInput.length() - 1) == ')') {
                    openBracketCount++;
                }
                currentInput.deleteCharAt(currentInput.length() - 1);
                updateDisplay();
            }
        });


        Button btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(view -> {
            String result = calculate(currentInput.toString());
            display.setText(result);
            currentInput.setLength(0); // Reset input after calculation
            hasDot = false;
            openBracketCount = 0;
        });


        Button btnOpenBracket = findViewById(R.id.btnOpenBracket);
        btnOpenBracket.setOnClickListener(view -> {
            if (currentInput.length() == 0 || isLastCharacterOperator() || isLastCharacterOpenBracket()) {
                currentInput.append("(");
                openBracketCount++;
                updateDisplay();
            }
        });


        Button btnCloseBracket = findViewById(R.id.btnCloseBracket);
        btnCloseBracket.setOnClickListener(view -> {
            if (openBracketCount > 0 && !isLastCharacterOperator()) {
                currentInput.append(")");
                openBracketCount--;
                updateDisplay();
            }
        });


        Button btnAC = findViewById(R.id.btnAC);
        btnAC.setOnClickListener(view -> resetCalculator());
    }


    private void resetCalculator() {
        currentInput.setLength(0);
        display.setText("");
        hasDot = false;
        openBracketCount = 0;
    }


    private void updateDisplay() {
        display.setText(currentInput.toString());
    }


    private boolean isLastCharacterOperator() {
        if (currentInput.length() == 0) return false;
        char lastChar = currentInput.charAt(currentInput.length() - 1);
        return lastChar == ' ' || lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/';
    }


    private boolean isLastCharacterOpenBracket() {
        return currentInput.length() > 0 && currentInput.charAt(currentInput.length() - 1) == '(';
    }


    private String calculate(String input) {
        try {
            return String.valueOf(evaluateExpression(input));
        } catch (Exception e) {
            return "Error";
        }
    }


    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '(') {
                operators.push(c);
            } else if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
        }
        return -1;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': if (b != 0) return a / b;
        }
        return 0;
    }
}

