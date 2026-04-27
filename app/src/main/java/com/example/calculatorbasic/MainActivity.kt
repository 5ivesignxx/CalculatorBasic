package com.example.calculatorbasic

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// ── Enum class: four mathematical operations ──
enum class Operation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

// ── Calculator class with primary constructor ──
class Calculator(private val operand1: Double, private val operand2: Double) {

    fun calculate(operation: Operation): Double {
        return when (operation) {
            Operation.ADD      -> operand1 + operand2
            Operation.SUBTRACT -> operand1 - operand2
            Operation.MULTIPLY -> operand1 * operand2
            Operation.DIVIDE   -> {
                if (operand2 == 0.0) throw ArithmeticException("Cannot divide by zero")
                operand1 / operand2
            }
        }
    }
}

// ── Main Activity ──
class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression : TextView
    private lateinit var tvDisplay    : TextView

    private var currentInput = StringBuilder()
    private var firstOperand = 0.0
    private var pendingOp    : Operation? = null
    private var resultShown  = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay    = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)

        // Digit buttons
        findViewById<Button>(R.id.btn0).setOnClickListener { appendDigit("0") }
        findViewById<Button>(R.id.btn1).setOnClickListener { appendDigit("1") }
        findViewById<Button>(R.id.btn2).setOnClickListener { appendDigit("2") }
        findViewById<Button>(R.id.btn3).setOnClickListener { appendDigit("3") }
        findViewById<Button>(R.id.btn4).setOnClickListener { appendDigit("4") }
        findViewById<Button>(R.id.btn5).setOnClickListener { appendDigit("5") }
        findViewById<Button>(R.id.btn6).setOnClickListener { appendDigit("6") }
        findViewById<Button>(R.id.btn7).setOnClickListener { appendDigit("7") }
        findViewById<Button>(R.id.btn8).setOnClickListener { appendDigit("8") }
        findViewById<Button>(R.id.btn9).setOnClickListener { appendDigit("9") }

        // Function buttons
        findViewById<Button>(R.id.btnDot).setOnClickListener       { appendDot() }
        findViewById<Button>(R.id.btnNegate).setOnClickListener    { negateInput() }
        findViewById<Button>(R.id.btnClear).setOnClickListener     { clearAll() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }

        // Operator buttons
        findViewById<Button>(R.id.btnAdd).setOnClickListener      { onOperatorPressed(Operation.ADD) }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { onOperatorPressed(Operation.SUBTRACT) }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorPressed(Operation.MULTIPLY) }
        findViewById<Button>(R.id.btnDivide).setOnClickListener   { onOperatorPressed(Operation.DIVIDE) }

        // Equals
        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsPressed() }

        updateDisplay()
    }

    private fun appendDigit(digit: String) {
        if (resultShown) { currentInput.clear(); resultShown = false }
        if (currentInput.toString() == "0") currentInput.clear()
        currentInput.append(digit)
        updateDisplay()
    }

    private fun appendDot() {
        if (resultShown) { currentInput.clear(); resultShown = false }
        if (!currentInput.contains('.')) {
            if (currentInput.isEmpty()) currentInput.append('0')
            currentInput.append('.')
        }
        updateDisplay()
    }

    private fun backspace() {
        if (resultShown) { clearAll(); return }
        if (currentInput.isNotEmpty()) currentInput.deleteCharAt(currentInput.lastIndex)
        updateDisplay()
    }

    private fun negateInput() {
        if (currentInput.isEmpty() || currentInput.toString() == "0") return
        if (currentInput.startsWith('-')) currentInput.deleteCharAt(0)
        else currentInput.insert(0, '-')
        updateDisplay()
    }

    private fun onOperatorPressed(op: Operation) {
        if (pendingOp != null && currentInput.isNotEmpty() && !resultShown) {
            evaluatePending()
        } else if (currentInput.isNotEmpty()) {
            firstOperand = currentInput.toString().toDoubleOrNull() ?: 0.0
        }
        pendingOp   = op
        resultShown = false
        tvExpression.text = "${fmt(firstOperand)} ${symbol(op)}"
        currentInput.clear()
        updateDisplay()
    }

    private fun onEqualsPressed() {
        if (pendingOp == null || currentInput.isEmpty()) return
        evaluatePending(showFull = true)
        pendingOp   = null
        resultShown = true
    }

    private fun evaluatePending(showFull: Boolean = false) {
        val op  = pendingOp ?: return
        val op2 = currentInput.toString().toDoubleOrNull() ?: return
        try {
            val result = Calculator(firstOperand, op2).calculate(op)
            if (showFull) {
                tvExpression.text = "${fmt(firstOperand)} ${symbol(op)} ${fmt(op2)} ="
            }
            firstOperand = result
            currentInput = StringBuilder(fmt(result))
            updateDisplay()
        } catch (e: ArithmeticException) {
            tvDisplay.text    = "Error"
            tvExpression.text = "Cannot divide by zero"
            currentInput.clear()
            firstOperand = 0.0
        }
    }

    private fun clearAll() {
        currentInput.clear()
        firstOperand = 0.0
        pendingOp    = null
        resultShown  = false
        tvExpression.text = ""
        updateDisplay()
    }

    private fun updateDisplay() {
        tvDisplay.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
    }

    private fun fmt(n: Double): String =
        if (n == n.toLong().toDouble()) n.toLong().toString() else n.toString()

    private fun symbol(op: Operation) = when (op) {
        Operation.ADD      -> "+"
        Operation.SUBTRACT -> "-"
        Operation.MULTIPLY -> "x"
        Operation.DIVIDE   -> "/"
    }
}
