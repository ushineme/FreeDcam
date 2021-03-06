/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.dimen;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityAbstract;
import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.sonyremote.SonyCameraFragment;
import freed.cam.apis.sonyremote.parameters.JoyPad;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.cam.ui.I_swipe;
import freed.cam.ui.SwipeMenuListner;
import freed.cam.ui.guide.GuideHandler;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildCameraSwitch;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildExit;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChildModuleSwitch;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsFocusPeak;
import freed.cam.ui.themesample.handler.FocusImageHandler;
import freed.cam.ui.themesample.handler.SampleInfoOverlayHandler;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.utils.AppSettingsManager;
import freed.viewer.screenslide.ScreenSlideFragment.I_ThumbClick;

/**
 * Created by troop on 14.06.2015.
 */
public class CameraUiFragment extends AbstractFragment implements SettingsChildAbstract.SettingsChildClick, SettingsChildAbstract.CloseChildClick, I_swipe, OnClickListener, ModuleHandlerAbstract.CaptureStateChanged
{
    final String TAG = CameraUiFragment.class.getSimpleName();
    private UiSettingsChild flash;
    private UiSettingsChild iso;
    private UiSettingsChild autoexposure;
    private UiSettingsChild whitebalance;
    private UiSettingsChild focus;
    private UiSettingsChild night;
    private UiSettingsChild format;
    private UiSettingsChildCameraSwitch cameraSwitch;
    private UiSettingsChildModuleSwitch modeSwitch;
    private UiSettingsChild contShot;
    private UiSettingsChild currentOpendChild;
    private UiSettingsChild aepriority;
    private UiSettingsFocusPeak focuspeak;
    private UiSettingsChild hdr_switch;
    private UiSettingsChildExit exit;

    private HorizontalValuesFragment horizontalValuesFragment;
    private SwipeMenuListner touchHandler;
    private ShutterButton shutterButton;
    private ManualFragment manualModesFragment;
    private FrameLayout manualModes_holder;
    private boolean manualsettingsIsOpen;
    private FocusImageHandler focusImageHandler;
    private ActivityInterface fragment_activityInterface;
    private SampleInfoOverlayHandler infoOverlayHandler;
    private GuideHandler guideHandler;
    private final String KEY_MANUALMENUOPEN = "key_manualmenuopen";
    private SharedPreferences sharedPref;
    public I_ThumbClick thumbClick;

    private UiSettingsChild aelock;

    private UserMessageHandler messageHandler;

    private HorizontLineFragment horizontLineFragment;
    private View camerauiValuesFragmentHolder;

    private JoyPad joyPad;

    public CameraUiFragment()
    {

    }

    @Override
    protected void setCameraUiWrapperToUi() {
        if (cameraUiWrapper == null || cameraUiWrapper.GetParameterHandler() == null || !isAdded())
        {
            Log.d(TAG, "failed to set cameraUiWrapper");
            if (isAdded())
                hide_ManualSettings();
            return;
        }
        cameraUiWrapper.GetModuleHandler().SetWorkListner(this);
        flash.SetParameter(cameraUiWrapper.GetParameterHandler().FlashMode);
        iso.SetParameter(cameraUiWrapper.GetParameterHandler().IsoMode);
        autoexposure.SetParameter(cameraUiWrapper.GetParameterHandler().ExposureMode);
        whitebalance.SetParameter(cameraUiWrapper.GetParameterHandler().WhiteBalanceMode);
        focus.SetParameter(cameraUiWrapper.GetParameterHandler().FocusMode);
        night.SetParameter(cameraUiWrapper.GetParameterHandler().NightMode);
        aepriority.SetParameter(cameraUiWrapper.GetParameterHandler().AE_PriorityMode);

        cameraSwitch.SetCameraUiWrapper(cameraUiWrapper);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper);

        messageHandler.SetCameraUiWrapper(cameraUiWrapper);
        shutterButton.SetCameraUIWrapper(cameraUiWrapper, messageHandler);
        format.SetParameter(cameraUiWrapper.GetParameterHandler().PictureFormat);
        contShot.SetParameter(cameraUiWrapper.GetParameterHandler().ContShootMode);
        if (manualModesFragment != null)
            manualModesFragment.SetCameraUIWrapper(cameraUiWrapper);
        if (cameraUiWrapper.GetParameterHandler().Focuspeak != null) {
            focuspeak.SetParameter(cameraUiWrapper.GetParameterHandler().Focuspeak);
        }
        guideHandler.setCameraUiWrapper(cameraUiWrapper);
        focuspeak.SetCameraUiWrapper(cameraUiWrapper);
        modeSwitch.SetCameraUiWrapper(cameraUiWrapper);
        hdr_switch.SetParameter(cameraUiWrapper.GetParameterHandler().HDRMode);
        horizontLineFragment.setCameraUiWrapper(cameraUiWrapper);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        shutterButton.setVisibility(View.VISIBLE);
        aelock.SetParameter(cameraUiWrapper.GetParameterHandler().ExposureLock);
        //restore view state for the manuals
        if(manualsettingsIsOpen)
            showManualSettings();
        //remove the values fragment from ui when a new api gets loaded and it was open.
        if (horizontalValuesFragment != null && horizontalValuesFragment.isAdded())
            removeHorizontalFragment();

        if (cameraUiWrapper instanceof SonyCameraFragment)
        {
            joyPad.setVisibility(View.GONE);
            if (cameraUiWrapper.GetParameterHandler().PreviewZoom != null)
                cameraUiWrapper.GetParameterHandler().PreviewZoom.addEventListner(joyPad);
            joyPad.setNavigationClickListner((SimpleStreamSurfaceView)cameraUiWrapper.getSurfaceView());
        }
        else
            joyPad.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.d(TAG, "####################ONCREATEDVIEW####################");

        fragment_activityInterface = (ActivityInterface)getActivity();
        touchHandler = new SwipeMenuListner(this);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        manualsettingsIsOpen = sharedPref.getBoolean(KEY_MANUALMENUOPEN, false);

        return inflater.inflate(layout.cameraui_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manualModes_holder = (FrameLayout) view.findViewById(id.manualModesHolder);
        messageHandler = new UserMessageHandler(view);
        flash = (UiSettingsChild) view.findViewById(id.Flash);
        flash.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_FLASHMODE);
        flash.SetMenuItemClickListner(this, true);

        iso = (UiSettingsChild) view.findViewById(id.ui_settings_iso);
        iso.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_ISOMODE);
        iso.SetMenuItemClickListner(this,true);

        autoexposure =(UiSettingsChild) view.findViewById(id.Ae);
        autoexposure.SetStuff(fragment_activityInterface,AppSettingsManager.SETTING_EXPOSUREMODE);
        autoexposure.SetMenuItemClickListner(this,true);

        aepriority = (UiSettingsChild) view.findViewById(id.AePriority);
        aepriority.SetStuff(fragment_activityInterface,AppSettingsManager.SETTTING_AE_PRIORITY);
        aepriority.SetMenuItemClickListner(this,true);


        whitebalance = (UiSettingsChild) view.findViewById(id.wb);
        whitebalance.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        whitebalance.SetMenuItemClickListner(this,true);

        focus = (UiSettingsChild) view.findViewById(id.focus_uisetting);
        focus.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_FOCUSMODE);
        focus.SetMenuItemClickListner(this,true);

        contShot = (UiSettingsChild) view.findViewById(id.continousShot);
        contShot.SetStuff(fragment_activityInterface, null);
        contShot.SetMenuItemClickListner(this,true);

        night = (UiSettingsChild) view.findViewById(id.night);
        night.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_NIGHTEMODE);
        night.SetMenuItemClickListner(this,true);

        format = (UiSettingsChild) view.findViewById(id.format);
        format.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_PICTUREFORMAT);
        format.SetMenuItemClickListner(this,true);

        modeSwitch = (UiSettingsChildModuleSwitch) view.findViewById(id.mode_switch);
        modeSwitch.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_CURRENTMODULE);
        modeSwitch.SetMenuItemClickListner(this,false);

        exit = (UiSettingsChildExit) view.findViewById(id.exit);
        exit.SetStuff(fragment_activityInterface, "");

        cameraSwitch = (UiSettingsChildCameraSwitch) view.findViewById(id.camera_switch);
        cameraSwitch.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_CURRENTCAMERA);

        infoOverlayHandler = new SampleInfoOverlayHandler(view, fragment_activityInterface.getAppSettings());
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);

        focusImageHandler = new FocusImageHandler(view, (ActivityAbstract) getActivity());

        shutterButton = (ShutterButton) view.findViewById(id.shutter_button);

        view.setOnTouchListener(onTouchListener);

        focuspeak = (UiSettingsFocusPeak) view.findViewById(id.ui_focuspeak);

        focuspeak.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_FOCUSPEAK);
        focuspeak.SetUiItemClickListner(this);
        focuspeak.setVisibility(View.GONE);

        hdr_switch = (UiSettingsChild) view.findViewById(id.hdr_toggle);
        hdr_switch.SetStuff(fragment_activityInterface, AppSettingsManager.SETTING_HDRMODE);
        hdr_switch.SetMenuItemClickListner(this,true);

        aelock = (UiSettingsChild)view.findViewById(id.ae_lock);
        aelock.SetUiItemClickListner(this);
        aelock.SetStuff(fragment_activityInterface, "");

        hideUiItems();

        manualModesFragment = new ManualFragment();

        horizontLineFragment = new HorizontLineFragment();

        guideHandler =GuideHandler.GetInstance(fragment_activityInterface.getAppSettings());

        manualModes_holder.setVisibility(View.GONE);
        camerauiValuesFragmentHolder =  view.findViewById(id.cameraui_values_fragment_holder);
        joyPad = (JoyPad) view.findViewById(id.joypad);
        joyPad.setVisibility(View.GONE);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(id.guideHolder, guideHandler, "Guide");
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.bottom_to_top_enter, anim.empty);
        transaction.replace(id.manualModesHolder, manualModesFragment);
        transaction.commit();

        transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.empty, anim.empty);
        transaction.replace(id.horHolder, horizontLineFragment);
        transaction.addToBackStack(null);
        transaction.commit();



        boolean showhelp = fragment_activityInterface.getAppSettings().getShowHelpOverlay();
        if (showhelp) {
            transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.helpfragment_container, HelpFragment.getFragment(helpfragmentCloser, fragment_activityInterface.getAppSettings()));
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if (cameraUiWrapper != null)
            setCameraUiWrapperToUi();
    }

    @Override
    public void onResume() {
        super.onResume();
        infoOverlayHandler.StartUpdating();
    }

    @Override
    public void onPause()
    {
        infoOverlayHandler.StopUpdating();
        sharedPref.edit().putBoolean(KEY_MANUALMENUOPEN, manualsettingsIsOpen).commit();
        boolean settingsOpen = false;
        String KEY_SETTINGSOPEN = "key_settingsopen";
        sharedPref.edit().putBoolean(KEY_SETTINGSOPEN, settingsOpen).commit();
        super.onPause();

    }

    private void hide_ManualSettings()
    {
        manualsettingsIsOpen = false;
        Log.d(TAG, "HideSettings");
        manualModes_holder.animate().translationY(manualModes_holder.getHeight()).setDuration(300);
        //manualModes_holder.setVisibility(View.GONE);
    }

    private void showManualSettings()
    {
        Log.d(TAG, "ShowSettings");
        manualsettingsIsOpen = true;
        manualModes_holder.animate().translationY(0).setDuration(300);
        manualModes_holder.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment)
    {

        if (currentOpendChild == item)
        {
            removeHorizontalFragment();
            currentOpendChild = null;
            return;
        }
        if (currentOpendChild != null)
        {
            removeHorizontalFragment();
            currentOpendChild = null;
        }
        if (horizontalValuesFragment != null)
            horizontalValuesFragment.Clear();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(dimen.manualitemwidth);
        params.rightMargin = getResources().getDimensionPixelSize(dimen.shuttericon_size);
        //params.addRule(RelativeLayout.CENTER_VERTICAL);

        if (manualsettingsIsOpen)
            params.bottomMargin = getResources().getDimensionPixelSize(dimen.manualSettingsHeight);

        if (fromLeftFragment)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        else  params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        camerauiValuesFragmentHolder.setLayoutParams(params);

        currentOpendChild = item;
        horizontalValuesFragment = new HorizontalValuesFragment();
        String[] tmo = item.GetValues();
        if (tmo != null && tmo.length >0)
            horizontalValuesFragment.SetStringValues(tmo, this);
        else
            horizontalValuesFragment.ListenToParameter(item.GetParameter());
        infalteIntoHolder(id.cameraui_values_fragment_holder, horizontalValuesFragment);

    }

    private void infalteIntoHolder(int id, HorizontalValuesFragment fragment)
    {

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, 0);
        transaction.replace(id, fragment);
        transaction.commit();
    }

    private void removeHorizontalFragment()
    {
        getChildFragmentManager().beginTransaction().remove(horizontalValuesFragment).setCustomAnimations(0, anim.right_to_left_exit).commit();
    }


    @Override
    public void onCloseClicked(String value) {
        currentOpendChild.SetValue(value);
        removeHorizontalFragment();
        currentOpendChild = null;
    }



    @Override
    public void  doRightToLeftSwipe() {

    }

    @Override
    public void doLeftToRightSwipe(){
    }

    @Override
    public void doTopToBottomSwipe(){
        hide_ManualSettings();
    }

    @Override
    public void doBottomToTopSwipe(){
        showManualSettings();

    }

    @Override
    public void onClick(int x, int y) {

    /*    showVIdItems();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideVIdItems();
            }
        }, 5000);*/

        if (focusImageHandler != null)
            focusImageHandler.OnClick(x,y);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (focusImageHandler != null)
            focusImageHandler.onTouchEvent(event);
    }

    OnTouchListener onTouchListener = new OnTouchListener()
    {
        public boolean onTouch(View v, MotionEvent event)
        {
            return touchHandler.onTouchEvent(event);
        }

    };

    //On Settings Menu Click
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCaptureStateChanged(ModuleHandlerAbstract.CaptureStates captureStates)
    {
       /* switch (captureStates)
        {
            case video_recording_start:
                hideVIdItems();
                break;
            case video_recording_stop:
                showVIdItems();
                break;
        }*/
        /*switch (captureStates)
        {
            case image_capture_stop:
                enableUiItems();
                break;
            case image_capture_start:
                disableUiItems();
                break;
            case continouse_capture_start:
                break;
            case continouse_capture_stop:
                break;
            case continouse_capture_work_start:
                break;
            case continouse_capture_work_stop:
                break;
            case cont_capture_stop_while_working:
                break;
            case cont_capture_stop_while_notworking:
                break;
        }
*/

    }

    private void enableUiItems()
    {
        if (flash.GetParameter() != null)
            flash.onParameterIsSetSupportedChanged(flash.GetParameter().IsSupported());
        if (iso.GetParameter() != null)
            iso.onParameterIsSetSupportedChanged(iso.GetParameter().IsSupported());
        if (autoexposure.GetParameter() != null)
            autoexposure.onParameterIsSetSupportedChanged(whitebalance.GetParameter().IsSupported());
        if (whitebalance.GetParameter() != null)
            whitebalance.onParameterIsSetSupportedChanged(whitebalance.GetParameter().IsSupported());
        if (focus.GetParameter() != null)
            focus.onParameterIsSetSupportedChanged(focus.GetParameter().IsSupported());
        if (night.GetParameter() != null)
            night.onParameterIsSetSupportedChanged(night.GetParameter().IsSupported());
        if (format.GetParameter() != null)
            format.onParameterIsSetSupportedChanged(format.GetParameter().IsSupported());
        cameraSwitch.onParameterIsSetSupportedChanged(true);
        if (modeSwitch.GetParameter() != null)
            modeSwitch.onParameterIsSetSupportedChanged(modeSwitch.GetParameter().IsSupported());
        if (flash.GetParameter() != null)
            flash.onParameterIsSetSupportedChanged(flash.GetParameter().IsSupported());
        if (modeSwitch.GetParameter() != null)
            modeSwitch.onParameterIsSetSupportedChanged(flash.GetParameter().IsSupported());
        if (contShot.GetParameter() != null)
            flash.onParameterIsSetSupportedChanged(contShot.GetParameter().IsSupported());
        if (aepriority.GetParameter() != null)
            aepriority.onParameterIsSetSupportedChanged(aepriority.GetParameter().IsSupported());
        if (focuspeak.GetParameter() != null)
            focuspeak.onParameterIsSetSupportedChanged(flash.GetParameter().IsSupported());
        if (hdr_switch.GetParameter() != null)
            hdr_switch.onParameterIsSetSupportedChanged(hdr_switch.GetParameter().IsSupported());
    }

    private void disableUiItems()
    {
        flash.onParameterIsSetSupportedChanged(false);
        iso.onParameterIsSetSupportedChanged(false);
        autoexposure.onParameterIsSetSupportedChanged(false);
        whitebalance.onParameterIsSetSupportedChanged(false);
        focus.onParameterIsSetSupportedChanged(false);
        night.onParameterIsSetSupportedChanged(false);
        format.onParameterIsSetSupportedChanged(false);
        cameraSwitch.onParameterIsSetSupportedChanged(false);
        modeSwitch.onParameterIsSetSupportedChanged(false);
        contShot.onParameterIsSetSupportedChanged(false);
        aepriority.onParameterIsSetSupportedChanged(false);
        focuspeak.onParameterIsSetSupportedChanged(false);
        hdr_switch.onParameterIsSetSupportedChanged(false);
    }

    private void showVIdItems()
    {
        exit.setVisibility(View.VISIBLE);
        iso.setVisibility(View.VISIBLE);
        autoexposure.setVisibility(View.VISIBLE);
        whitebalance.setVisibility(View.VISIBLE);
        focus.setVisibility(View.VISIBLE);
        contShot.setVisibility(View.VISIBLE);
        night.setVisibility(View.VISIBLE);
        format.setVisibility(View.VISIBLE);
        modeSwitch.setVisibility(View.VISIBLE);
        cameraSwitch.setVisibility(View.VISIBLE);

    }

    private void hideVIdItems()
    {
        exit.setVisibility(View.INVISIBLE);
        iso.setVisibility(View.INVISIBLE);
        autoexposure.setVisibility(View.INVISIBLE);
        whitebalance.setVisibility(View.INVISIBLE);
        focus.setVisibility(View.INVISIBLE);
        contShot.setVisibility(View.INVISIBLE);
        night.setVisibility(View.INVISIBLE);
        format.setVisibility(View.INVISIBLE);
        modeSwitch.setVisibility(View.INVISIBLE);
        cameraSwitch.setVisibility(View.INVISIBLE);
    }

    private void hideUiItems()
    {
        flash.setVisibility(View.GONE);
        iso.setVisibility(View.GONE);
        autoexposure.setVisibility(View.GONE);
        aepriority.setVisibility(View.GONE);
        whitebalance.setVisibility(View.GONE);
        focus.setVisibility(View.GONE);
        contShot.setVisibility(View.GONE);
        night.setVisibility(View.GONE);
        format.setVisibility(View.GONE);
        modeSwitch.setVisibility(View.GONE);
        cameraSwitch.setVisibility(View.GONE);
        shutterButton.setVisibility(View.GONE);
        hdr_switch.setVisibility(View.GONE);

    }

    interface i_HelpFragment
    {
        void Close(Fragment fragment);
    }

    private final i_HelpFragment helpfragmentCloser = new i_HelpFragment() {
        @Override
        public void Close(Fragment fragment) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    };
}
