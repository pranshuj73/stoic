package com.pj.raco.dailyzen

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.widget.TextView
import org.json.JSONObject

/**
 * Implementation of App Widget functionality.
 */
class QuoteWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.quote_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)

    // get json array of quotes from assets/data.json and select random quote from array
    fun genQuote(): JSONObject {
//        get json array of quotes from assets/data.json and select random quote from array
        val assets = context.assets
        val quotes = JSONObject(assets.open("data.json").bufferedReader().use { it.readText() })
            .getJSONArray("quotes")

        return quotes.getJSONObject((0 until quotes.length()).random())
    }

    fun summonQuote() {
        val quote = genQuote()
        views.setTextViewText(R.id.quote, quote.getString("quote"))
        views.setTextViewText(R.id.author, buildString {
            append("— ")
            append(quote.getString("author"))
        })
    }

    summonQuote()


    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}