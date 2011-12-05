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

import android.appwidget.AppWidgetManager;
import android.os.Bundle;
import android.support.v4.app.SherlockPreferenceActivity;
import org.flexlabs.widgets.dualbattery.BatteryWidgetUpdater;
import org.flexlabs.widgets.dualbattery.Constants;
import org.flexlabs.widgets.dualbattery.R;

public class WidgetSettingsActivity extends SherlockPreferenceActivity {
    private int appWidgetId;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        getPreferenceManager().setSharedPreferencesName(Constants.SETTINGS_PREFIX + appWidgetId);
        addPreferencesFromResource(R.xml.widget_properties_general);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BatteryWidgetUpdater.updateWidget(this, appWidgetId);        
    }
}