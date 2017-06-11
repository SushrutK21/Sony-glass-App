/*
Copyright (c) 2011, Sony Mobile Communications Inc.
Copyright (c) 2014, Sony Corporation

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications Inc.
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sony.smarteyeglass.extension.ARexp01;

import android.content.Intent;
import android.util.Log;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.DisplayInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationAdapter;

/**
 * The Sample Extension Service handles registration and keeps track of all
 * extension class on all accessories.
 */
public final class SampleExtensionService extends ExtensionService {

    /** */
    public static ARexp01Control SmartEyeglassControl;
    /** */
    public static SampleExtensionService Object;

    private float xp = 0.f;
    private float yp = 0.f;
    private static String Message = null;

    /** Creates a new instance. */
    public SampleExtensionService() {
        super(Constants.EXTENSION_KEY);
        Object = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.LOG_TAG, "SampleExtensionService : onCreate()");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new SampleRegistrationInformation(this);
    }

    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(
            final String hostAppPackageName) {
        boolean isApiSupported = DeviceInfoHelper
            .isSmartEyeglassScreenSupported(this, hostAppPackageName);
        if (isApiSupported) {
            SmartEyeglassControl = new ARexp01Control(this, hostAppPackageName);
            return SmartEyeglassControl;
//            return new SampleARCylindricalControl(this, hostAppPackageName);
        } else {
            Log.d(Constants.LOG_TAG, "Service: not supported, exiting");
            throw new IllegalArgumentException(
                "No control for: " + hostAppPackageName);
        }
    }

    /**
     * Sets message to be shown when the SmartEyeglass app is ready.
     * Starts the app if it has not already started.
     */
    public void sendMessageToExtention(final float x, final float y, final String message) {
        xp = x;
        yp = y;
        Message = message;
        if (SmartEyeglassControl == null) {
            startSmartEyeglassExtension();
        } else {
            SmartEyeglassControl.requestExtensionStart();
        }

        if (SmartEyeglassControl != null) {
            SmartEyeglassControl.updateCoordinates(xp, yp);
            startSmartEyeglassExtension();
        }else{
            Log.d(Constants.LOG_TAG,"SmartEyeglassControl is null.");
        }
    }
    /**
     * You can use this method to kickstart your extension on SmartEyeglass
     * Host App.
     * This is useful if the user has not started your extension
     * since the SmartEyeglass was turned on.
     */
    public void startSmartEyeglassExtension() {
        Intent intent = new Intent(Control.Intents
                .CONTROL_START_REQUEST_INTENT);
        ExtensionUtils.sendToHostApp(getApplicationContext(),
                "com.sony.smarteyeglass", intent);
    }
    /**
     * Sends a  message to be shown in Android activity
     */
    public void sendMessageToActivity(final String message) {
//        Intent intent = new Intent();
//        intent.setClass(getBaseContext(), ARexp01Activity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("Message", message);
//        startActivity(intent);
        ARexp01Activity.udpTest(message);
    }

    
}
