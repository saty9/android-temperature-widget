package cookesolutions.emonmonitor

import android.appwidget.AppWidgetProvider
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
//import sun.invoke.util.VerifyAccess.getPackageName
import android.widget.RemoteViews
import com.github.kittinunf.fuel.*
import com.github.kittinunf.result.success
import org.jetbrains.anko.*


class MainWidget: AppWidgetProvider() {

    val requestStrings = emptyMap<Int,String>()
    val baseurl = "https://emoncms.org/feed/"

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        if (context != null) {
            val remoteViews = RemoteViews(context.packageName,
                    R.layout.widget)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            WidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val count = appWidgetIds.size
        Log.i("widget count",(count +1).toString())
        for (i in 0 until count) {
            Log.i("widget index",i.toString())
            val widgetId = appWidgetIds[i]

            val remoteViews = RemoteViews(context.packageName,
                    R.layout.widget)
            remoteViews.setTextViewText(R.id.textView, "---")

            /*val intent = Intent(context, MainWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            val pendingIntent = PendingIntent.getBroadcast(context,
                    widgetId, intent, 0)*/

            updateAppWidget(context,appWidgetManager,widgetId)

        }
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val (label,apiKey,feedID) = WidgetConfigureActivity.loadPref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget)

            views.setTextViewText(R.id.labelText, label)
            views.setTextViewText(R.id.textView, "---")

            val ids = intArrayOf(appWidgetId)
            val intent = Intent(context, MainWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            val pendingIntent = PendingIntent.getBroadcast(context,
                    appWidgetId, intent, 0)
            views.setOnClickPendingIntent(R.id.refreshButton, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

            doAsync {
                var requestURL = "https://emoncms.org/feed/get.json?id=%s&field=value".format(feedID)
                if (apiKey.length > 0){
                    requestURL += "&apikey=" + apiKey
                }

                Log.d("requesting",requestURL)
                val (request, response, result) = requestURL.httpGet().responseString()
                var temp: Float? = null
                result.success {
                    temp = it.drop(1).dropLast(1).toFloat()
                    Log.i("temperature",temp.toString())
                }

                uiThread {
                    if (temp != null) {
                        val out = String.format("%.2f C".format(temp))
                        Log.i("posting",out)
                        views.setTextViewText(R.id.textView, out)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }


}