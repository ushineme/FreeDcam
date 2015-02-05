package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    I_CameraHolder cameraHolder;
    public PreviewFormatParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFormatParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder cameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);


    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }

}
