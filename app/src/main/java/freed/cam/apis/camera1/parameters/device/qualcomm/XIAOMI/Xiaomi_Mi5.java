package freed.cam.apis.camera1.parameters.device.qualcomm.XIAOMI;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by troop on 21.10.2016.
 */

public class Xiaomi_Mi5 extends BaseQcomNew {
    public Xiaomi_Mi5(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 20500480:
                return new DngProfile(64, 4652, 3520, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX298));
        }
        return null;
    }

    @Override
    public ModeParameterInterface getHDRMode() {
        return null;
    }


    @Override
    public ModeParameterInterface getVideoStabilisation() {
        return null;
    }

    @Override
    public ModeParameterInterface getDigitalImageStabilisation() {
        return null;
    }
}
