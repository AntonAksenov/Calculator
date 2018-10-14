package aksenov.anton.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import kotlinx.android.synthetic.main.activity_main.*
import mathparser.Parser
import mathparser.ParsingException
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import kotlin.math.max


class MainActivity : AppCompatActivity() {

    private val LOG_TAG = "MainActivity"
    private val CUR_EXPR = MainActivity::class.java.name + ".cur_expr"
    private val CUR_POS = MainActivity::class.java.name + ".cur_pos"
    private val BALANCE = MainActivity::class.java.name + ".balance"

    private var parser: Parser = Parser()

    private var curInv: Boolean = false
    private var curPos: Int = 0

    private var curExpr: StringBuilder = StringBuilder("")
    private var curAns: String = ""
    private var isCorrect: Boolean = false
    private var balance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        expr.movementMethod = ScrollingMovementMethod();

        clear.setOnClickListener {
            curPos = 0
            balance = 0
            curExpr = StringBuilder("")
            updateExpr()
            eval()
        }
        back.setOnClickListener {
            if (curPos > 0) {
                if (curExpr[curPos - 1] == '(') {
                    balance--
                } else if (curExpr[curPos - 1] == ')') {
                    balance++
                }
                curExpr.delete(curPos - 1, curPos).toString()
                curPos--
                updateExpr()
                eval()
            }
        }
        equals.setOnClickListener {
            if (isCorrect) {
                curExpr = StringBuilder("")
                curPos = 0
                insert(curAns.replace("E", "*10^"))
                // parser doesn't work with exp form, as i understood (it's not mine)
                eval()
            }
        }

        goLeft?.setOnClickListener {
            if (curPos > 0) curPos--
            updateExpr()
        }
        goRight?.setOnClickListener {
            if (curPos < curExpr.length) curPos++
            updateExpr()
        }

        plus.setOnClickListener { insert("+") }
        minus.setOnClickListener { insert("-") }
        mul.setOnClickListener { insert("*") }
        div.setOnClickListener { insert("/") }
        mod.setOnClickListener { insert("%") }
        dot.setOnClickListener { insert(".") }

        zero.setOnClickListener { insert("0") }
        one.setOnClickListener { insert("1") }
        two.setOnClickListener { insert("2") }
        three.setOnClickListener { insert("3") }
        four.setOnClickListener { insert("4") }
        five.setOnClickListener { insert("5") }
        six.setOnClickListener { insert("6") }
        seven.setOnClickListener { insert("7") }
        eight.setOnClickListener { insert("8") }
        nine.setOnClickListener { insert("9") }

        lBracket?.setOnClickListener { insert("(") }
        rBracket?.setOnClickListener { insert(")") }
        expMinusOne?.setOnClickListener { insert("^(-1)") }
        sin?.setOnClickListener { insert("sin(") }
        cos?.setOnClickListener { insert("cos(") }
        tan?.setOnClickListener { insert("tan(") }
        ln?.setOnClickListener { insert("ln(") }
        log?.setOnClickListener { insert("log(") }
        e?.setOnClickListener { insert("e") }
        pi?.setOnClickListener { insert("pi") }
        nRt?.setOnClickListener { insert("^(1/") }
        nExp?.setOnClickListener { insert("^(") }
        fact?.setOnClickListener { insert("fact(") }
        abs?.setOnClickListener { insert("abs(") }

        inv?.setOnClickListener {
            if (curInv) {
                sin?.text = "sin"
                cos?.text = "cos"
                tan?.text = "tan"
                ln?.text = "ln"
                log?.text = "log"

                sin?.setOnClickListener { insert("sin(") }
                cos?.setOnClickListener { insert("cos(") }
                tan?.setOnClickListener { insert("tan(") }
                ln?.setOnClickListener { insert("ln(") }
                log?.setOnClickListener { insert("log(") }

                curInv = false
            } else {
                sin?.text = "asin"
                cos?.text = "acos"
                tan?.text = "atan"
                ln?.text = "eⁿ"
                log?.text = "10ⁿ"

                sin?.setOnClickListener { insert("asin(") }
                cos?.setOnClickListener { insert("acos(") }
                tan?.setOnClickListener { insert("atan(") }
                ln?.setOnClickListener { insert("e^(") }
                log?.setOnClickListener { insert("10^(") }

                curInv = true
            }
        }

        if (savedInstanceState != null) {
            curExpr = StringBuilder(savedInstanceState.getString(CUR_EXPR))
            curPos = savedInstanceState.getInt(CUR_POS)
            balance = savedInstanceState.getInt(BALANCE)
            updateExpr()
        }
        eval()
    }

    private fun insert(string: String) {
        curExpr.insert(curPos, string).toString()
        curPos += string.length
        if (string.contains('(')) balance++
        if (string.contains(')')) balance--
        updateExpr()
        eval()
    }

    private fun eval() {
        try {
            parser!!.parse(curExpr.toString()
                    .replace("pi", "3.14159265358979323")
                    .replace("e", "2.71828182845904523")
                    + CharArray(max(0, balance)) { ')' }.joinToString(separator = ""))
            isCorrect = true
            curAns = "" + parser!!.getNumericAnswer()
            ans.text = "= " + curAns
        } catch (ex: ParsingException) {
            isCorrect = false
            ans.text = "= Incorrect expression"
        }
    }

    private fun updateExpr() {
        var spannable = SpannableString(
                curExpr.substring(0, curPos)
                        + "|" + curExpr.substring(curPos)
                        + CharArray(max(0, balance)) { ')' }.joinToString(separator = ""))
        spannable.setSpan(ForegroundColorSpan(-0x444445), curPos, curPos + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(-0x444445), curExpr.length, curExpr.length + max(0, balance) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        expr.text = spannable
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CUR_EXPR, curExpr.toString())
        outState.putInt(CUR_POS, curPos)
        outState.putInt(BALANCE, balance)
        super.onSaveInstanceState(outState)
    }

}
