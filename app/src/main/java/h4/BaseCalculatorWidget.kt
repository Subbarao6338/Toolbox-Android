package h4

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.toolbox.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

abstract class BaseCalculatorWidget : AppWidgetProvider() {

    var showClear: Boolean = false

    abstract fun getLayoutId(): Int

    open fun setPendingIntents(context: Context, appWidgetId: Int, remoteViews: RemoteViews) {
        val intent = Intent(context, javaClass).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra("all.in.one.calculator.widget.show_clear", showClear)
        }
        val baseRequestCode = appWidgetId shl 5

        fun setClick(id: Int, action: String, offset: Int) {
            val clickIntent = Intent(intent).apply { setAction(action) }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(context, baseRequestCode + offset, clickIntent, flags)
            remoteViews.setOnClickPendingIntent(id, pendingIntent)
        }

        setClick(R.id.digit0, "all.in.one.calculator.widget.0", 0)
        setClick(R.id.digit1, "all.in.one.calculator.widget.1", 1)
        setClick(R.id.digit2, "all.in.one.calculator.widget.2", 2)
        setClick(R.id.digit3, "all.in.one.calculator.widget.3", 3)
        setClick(R.id.digit4, "all.in.one.calculator.widget.4", 4)
        setClick(R.id.digit5, "all.in.one.calculator.widget.5", 5)
        setClick(R.id.digit6, "all.in.one.calculator.widget.6", 6)
        setClick(R.id.digit7, "all.in.one.calculator.widget.7", 7)
        setClick(R.id.digit8, "all.in.one.calculator.widget.8", 8)
        setClick(R.id.digit9, "all.in.one.calculator.widget.9", 9)
        setClick(R.id.dot, "all.in.one.calculator.widget.dot", 10)
        setClick(R.id.div, "all.in.one.calculator.widget.div", 11)
        setClick(R.id.mul, "all.in.one.calculator.widget.mul", 12)
        setClick(R.id.minus, "all.in.one.calculator.widget.minus", 13)
        setClick(R.id.plus, "all.in.one.calculator.widget.plus", 14)
        setClick(R.id.equal, "all.in.one.calculator.widget.equals", 15)
        setClick(R.id.delete, "all.in.one.calculator.widget.delete", 16)
        setClick(R.id.clear, "all.in.one.calculator.widget.clear", 17)
    }

    fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val remoteViews = RemoteViews(context.packageName, getLayoutId())
        val symbols = DecimalFormatSymbols(Locale.getDefault())

        val displayText = getStoredExpression(context, appWidgetId)
        remoteViews.setTextViewText(R.id.display, displayText)
        remoteViews.setViewVisibility(R.id.display, android.view.View.VISIBLE)
        remoteViews.setTextViewText(R.id.dot, symbols.decimalSeparator.toString())
        remoteViews.setViewVisibility(R.id.delete, if (showClear) android.view.View.GONE else android.view.View.VISIBLE)
        remoteViews.setViewVisibility(R.id.clear, if (showClear) android.view.View.VISIBLE else android.view.View.GONE)

        setPendingIntents(context, appWidgetId, remoteViews)
        try {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        } catch (_: Exception) {
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
        val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
            exponentSeparator = "e"
            minusSign = '−'
            zeroDigit = '0'
        }

        var currentExp = getStoredExpression(context, appWidgetId)
        if (currentExp == "--" || currentExp == "∞") {
            currentExp = ""
        }

        showClear = intent.getBooleanExtra("all.in.one.calculator.widget.show_clear", false)
        val action = intent.action ?: ""

        var newExp = "--"
        var prefix = ""

        when {
            action.startsWith("all.in.one.calculator.widget.") && action.substringAfterLast(".").all { it.isDigit() } -> {
                val digit = action.substringAfterLast(".")
                if (showClear) showClear = false else prefix = currentExp
                newExp = prefix + digit
            }
            action == "all.in.one.calculator.widget.dot" -> {
                if (showClear) showClear = false else prefix = currentExp
                newExp = prefix + symbols.decimalSeparator
            }
            action == "all.in.one.calculator.widget.div" -> {
                newExp = appendOperator(currentExp, '÷')
            }
            action == "all.in.one.calculator.widget.mul" -> {
                newExp = appendOperator(currentExp, '×')
            }
            action == "all.in.one.calculator.widget.minus" -> {
                newExp = appendOperator(currentExp, '−')
            }
            action == "all.in.one.calculator.widget.plus" -> {
                newExp = appendOperator(currentExp, '+')
            }
            action == "all.in.one.calculator.widget.equals" -> {
                if (showClear) {
                    showClear = false
                } else {
                    showClear = true
                    prefix = currentExp
                }
                if (prefix.isNotEmpty()) {
                    newExp = evaluateExpression(symbols, prefix)
                }
            }
            action == "all.in.one.calculator.widget.clear" -> {
                newExp = ""
            }
            action == "all.in.one.calculator.widget.delete" -> {
                newExp = if (currentExp.isNotEmpty()) currentExp.dropLast(1) else currentExp
            }
            else -> {
                newExp = currentExp
            }
        }

        val formattedExp = formatExpression(symbols, unformatExpression(symbols, newExp))
        storeExpression(context, appWidgetId, formattedExp)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, javaClass))
        for (id in ids) {
            updateWidget(context, appWidgetManager, id)
        }

        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    private fun appendOperator(expr: String, op: Char): String {
        if (expr.isEmpty()) {
            return if (op == '−') op.toString() else expr
        }
        if (op != '−') {
            var len = expr.length
            while (len > 0 && isOperator(expr[len - 1])) {
                len--
            }
            if (len == 0 && expr.isNotEmpty() && isOperator(expr[0])) {
                return op.toString()
            }
            return expr.substring(0, len) + op
        }
        if (expr.lastOrNull() != '−') {
            return expr + op
        }
        return expr
    }

    private fun isOperator(c: Char): Boolean = c in "+−×÷"

    private fun getStoredExpression(context: Context, id: Int): String {
        val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
        return prefs.getString("all.in.one.calculator.widget$id", "") ?: ""
    }

    private fun storeExpression(context: Context, id: Int, expr: String) {
        val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString("all.in.one.calculator.widget$id", expr).apply()
    }

    private fun evaluateExpression(symbols: DecimalFormatSymbols, expr: String): String {
        val raw = unformatExpression(symbols, expr).replace('×', '*').replace('÷', '/')
        val value = evalSimple(raw)

        if (value.isNaN()) return expr
        if (value.isInfinite()) return "∞"

        val fmt1 = DecimalFormat("#.##", symbols).apply { roundingMode = RoundingMode.HALF_UP }
        val fmt2 = DecimalFormat("#.######", symbols).apply { roundingMode = RoundingMode.HALF_UP }
        val fmt3 = DecimalFormat("0.00E0", symbols)

        val absVal = Math.abs(value)
        return when {
            absVal >= 1.0 -> {
                if (absVal > 1.0e9) fmt3.format(Math.floor(value)) else fmt1.format(value)
            }
            absVal == 0.0 -> "0"
            absVal >= 1.0e-4 -> {
                val res = fmt1.format(value)
                if (res == "0") fmt2.format(value) else res
            }
            else -> fmt3.format(value)
        }
    }

    private fun evalSimple(expr: String): Double {
        return try {
            object : Any() {
                var pos = -1
                var ch = 0

                fun nextChar() {
                    ch = if (++pos < expr.length) expr[pos].code else -1
                }

                fun eat(charToEat: Int): Boolean {
                    while (ch == ' '.code) nextChar()
                    if (ch == charToEat) {
                        nextChar()
                        return true
                    }
                    return false
                }

                fun parse(): Double {
                    nextChar()
                    val x = parseExpression()
                    if (pos < expr.length) return Double.NaN
                    return x
                }

                fun parseExpression(): Double {
                    var x = parseTerm()
                    while (true) {
                        if (eat('+'.code)) x += parseTerm()
                        else if (eat('-'.code)) x -= parseTerm()
                        else return x
                    }
                }

                fun parseTerm(): Double {
                    var x = parseFactor()
                    while (true) {
                        if (eat('*'.code)) x *= parseFactor()
                        else if (eat('/'.code)) x /= parseFactor()
                        else return x
                    }
                }

                fun parseFactor(): Double {
                    if (eat('+'.code)) return parseFactor()
                    if (eat('-'.code)) return -parseFactor()

                    var x: Double
                    val startPos = pos
                    if (eat('('.code)) {
                        x = parseExpression()
                        eat(')'.code)
                    } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                        while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                        x = expr.substring(startPos, pos).toDouble()
                    } else {
                        return Double.NaN
                    }
                    return x
                }
            }.parse()
        } catch (_: Exception) {
            Double.NaN
        }
    }

    private fun unformatExpression(symbols: DecimalFormatSymbols, expr: String): String {
        return expr.replace(symbols.groupingSeparator.toString(), "")
            .replace(symbols.decimalSeparator, '.')
    }

    private fun formatExpression(symbols: DecimalFormatSymbols, expr: String): String {
        return expr.replace('.', symbols.decimalSeparator)
    }
}
