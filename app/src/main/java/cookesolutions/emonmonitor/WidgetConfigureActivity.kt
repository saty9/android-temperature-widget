package cookesolutions.emonmonitor

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText

/**
 * The configuration screen for the [MainWidget] AppWidget.
 */
class WidgetConfigureActivity : Activity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    //internal var mAppWidgetText: EditText

    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@WidgetConfigureActivity

        // When the button is clicked, store the string locally
        val label = findViewById<EditText>(R.id.labelText).text.toString()
        val apiKey = findViewById<EditText>(R.id.ApiKeyText).text.toString()
        val feedText = findViewById<EditText>(R.id.feedIdText).text.toString()

        saveTitlePref(context, mAppWidgetId, label,apiKey,feedText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        MainWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.widget_configure)
        //mAppWidgetText = findViewById<View>(R.id.appwidget_text) as EditText
        findViewById<View>(R.id.add_button).setOnClickListener(mOnClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    companion object {

        private val PREFS_NAME = "cookesolutions.emonmonitor.MainWidget"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int, label: String, api: String, feedID: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + "label_"+ appWidgetId, label)
            prefs.putString(PREF_PREFIX_KEY + "apiKey_"+ appWidgetId, api)
            prefs.putString(PREF_PREFIX_KEY + "feedID_"+ appWidgetId, feedID)
            prefs.apply()
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadPref(context: Context, appWidgetId: Int): Triple<String,String,String> {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val label = prefs.getString(PREF_PREFIX_KEY + "label_"+ appWidgetId, "")
            val api = prefs.getString(PREF_PREFIX_KEY + "apiKey_"+ appWidgetId, "")
            val feedID = prefs.getString(PREF_PREFIX_KEY + "feedID_"+ appWidgetId, "")
            return Triple(label,api,feedID)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + "label_" + appWidgetId)
            prefs.remove(PREF_PREFIX_KEY + "apiKey_" + appWidgetId)
            prefs.remove(PREF_PREFIX_KEY + "feedID_" + appWidgetId)
            prefs.apply()
        }
    }
}

