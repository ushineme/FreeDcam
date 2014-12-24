package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 20.10.2014.
 */
public class PictureFormatExpandableChild extends ExpandableChild {
    public PictureFormatExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }


    @Override
    public void setValue(String value)
    {
        parameterHolder.SetValue(value, true);
        valueTextView.setText(value);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    @Override
    public String Value() {
        return super.Value();
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }


    public void setParameterHolder(I_ModeParameter parameterHolder, ArrayList<String> modulesToShow) {
        this.parameterHolder = parameterHolder;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        //TODO code design fail
        //process raw supported devices
        settingValue = setDeviceSettings(parameterHolder, appSettingsManager, settingsname, settingValue);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        appSettingsManager.setString(settingsname, settingValue);
    }

    private String setDeviceSettings(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, String settingValue) {
        if (settingValue.equals(""))
        {
            appSettingsManager.setString(settingsname, "jpeg");
            settingValue = "jpeg";
        }
        return settingValue;
    }
}
