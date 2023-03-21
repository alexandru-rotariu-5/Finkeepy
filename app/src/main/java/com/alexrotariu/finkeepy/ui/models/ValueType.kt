package com.alexrotariu.finkeepy.ui.models

import com.alexrotariu.finkeepy.R

enum class ValueType(val labelResource: Int, val colorResource: Int, val bgDrawableResource: Int) {
    NET_WORTH(
        R.string.net_worth,
        R.color.color_chart_net_worth,
        R.drawable.bg_chart_value_type_selected_net_worth
    ),
    INCOME(
        R.string.income,
        R.color.color_chart_income,
        R.drawable.bg_chart_value_type_selected_income
    ),
    EXPENSE(
        R.string.expense,
        R.color.color_chart_expense,
        R.drawable.bg_chart_value_type_selected_expense
    ),
    CASHFLOW(
        R.string.cashflow,
        R.color.color_chart_cashflow,
        R.drawable.bg_chart_value_type_selected_cashflow
    )
}
