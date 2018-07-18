import java.util.regex.Pattern

fun Double.formatCNY(): String = String.format("￥ %.2f", this)

fun Double.reserveFraction(figures: Int = 2): String = String.format("%." + figures + "f", this)

fun String.isCellPhone(): Boolean = Pattern.compile("1[34578]\\d{9}").matcher(this).matches()

fun String.durationTo(end: String) = String.format("%1s - %2s", this, end)