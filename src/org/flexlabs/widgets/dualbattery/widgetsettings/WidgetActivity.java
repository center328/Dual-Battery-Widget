/*
 * Copyright 2011 Artiom Chilaru (http://flexlabs.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flexlabs.widgets.dualbattery.widgetsettings;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.view.MenuInflater;
import android.widget.Button;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

public class WidgetActivity extends FragmentActivity {
    private int appWidgetId;
    private IntentFilter intentFilter;
    private BatteryInfoViewManager batteryInfoViewManager = new BatteryInfoViewManager();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        int screenLayout = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                screenLayout > Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Intent newIntent = new Intent(this, WidgetTabbedActivity.class);
            newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivity(newIntent);
            finish();
            return;
        }

        setContentView(R.layout.battery_info_table);
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryInfoViewManager.loadData(this, findViewById(R.id.body), appWidgetId);
        findViewById(R.id.batterySummary).setOnClickListener(batteryInfoViewManager.batterySummaryListener);
        Button widgetSettings = (Button)findViewById(R.id.widgetSettings);
        widgetSettings.setVisibility(View.VISIBLE);
        widgetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(WidgetActivity.this, WidgetSettingsActivity.class);
                newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                startActivity(newIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        batteryInfoViewManager.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(batteryInfoViewManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batteryInfoViewManager, intentFilter);
        batteryInfoViewManager.buildChart();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return batteryInfoViewManager.onCreateDialog(this, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.widget_batteryinfo, menu);
        inflater.inflate(R.menu.widget, menu);
        if (!Constants.HAS_MARKET_BILLING)
            menu.findItem(R.id.donate_market).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.temperature).setTitle(batteryInfoViewManager.getMenuTitle());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return batteryInfoViewManager.onMenuItemSelected(item) ||
               super.onOptionsItemSelected(item);
    }
}