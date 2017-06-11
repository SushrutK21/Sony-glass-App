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

//import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;

import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.SmartEyeglassControl.Intents;
import com.sony.smarteyeglass.extension.sampleARLouvre.R;
import com.sony.smarteyeglass.extension.util.ar.CylindricalRenderObject;
import com.sony.smarteyeglass.extension.util.ar.GlassesRenderObject;
import com.sony.smarteyeglass.extension.util.ar.RenderObject;
import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sony.smarteyeglass.extension.util.SmartEyeglassEventListener;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration.SensorTypeValue;
import com.sonyericsson.extras.liveware.aef.sensor.Sensor;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensor;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEvent;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorEventListener;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorException;
import com.sonyericsson.extras.liveware.extension.util.sensor.AccessorySensorManager;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;


/**
 * Demonstrates how to register bitmaps to be rendered by the AR engine using
 * the cylindrical coordinate system, and display them together with
 * fixed-position objects registered using the glasses coordinate system.
 * The rendering process is executed at run time in the controller box,
 * when a registered position is in view, according to the rotation vector
 * sensor data.
 */
public final class ARexp01Control extends ControlExtension {

    private int mAngle = 0;

    /** The chosen renderer object, for either layout or bitmap method. */
    private Renderer renderer;

    private final Renderer layoutRenderer = new Renderer(){
        @Override
        public void init(){
            updateLayout();
        }
        @Override
        public void update(){
            updateLayout();
        }
    };

    /**
     * Renders a layout to the SmartEyeglass display.
     */
    private void updateLayout() {

        clearDisplay();
        // Display the view elements from layout.xml
        float X = context.getResources().getDimension(R.dimen.smarteyeglass_control_width);
        float Y = context.getResources().getDimension(R.dimen.smarteyeglass_control_height);
        Bitmap bitmap = getBitmapResource(R.drawable.img_dot);
        float W = bitmap.getWidth();
        float H = bitmap.getHeight(); // s
        int offset = (int)(W / 2.);
        int offset2 = (int)(H / 2.); //s
        Y /= 2.;              //s
        int x = (int)X - offset * 2;
        //int y = (int)Y;
        int y  = (int)Y - offset2 *2;
        switch(mAngle) {
            case 0:
                x = (int)(X / 2.) - offset;
                y = (int)(Y / 2.) - offset2; // s
                break;
            case 1:
                //o,5
                x = (int)(X / 2.) - offset;
                y = (int)(Y * 4./ 4.) - offset2;
                break;
            case 2:
                //5,0
                x = (int)(X * 3./ 4.) - offset;
                y = (int)(Y/2.)- offset2;
                break;
            case 3:
                //5,5
                x = (int)(X * 3./ 4.) - offset;
                y = (int)(Y * 4./ 4.) - offset2;
                break;
            case 4:
                //10,0
                x = (int)(X * 6./ 8.) - offset;
                y = (int)(Y/2.)- offset2;

                break;
            case 5:
                //10,5
                x = (int)(X * 6./ 8.) - offset;
                y = (int)(Y * 4./ 4.) - offset2;
                break;
            /*
            case 5:
                y = (int)(Y * 3./ 4.) - offset2;
                x = (int)(X * 3./ 4.) - offset;
                break;
            case 10:
                break; */
            default:
                x = (int)(X / 2.) - offset;
                y = (int)(Y/2.)- offset2;
                break;
        }
        showBitmap(getBitmapResource(R.drawable.img_dot), x, y);
        Log.d(Constants.LOG_TAG, "x: " + x + ", y: " + y);
    }
    /**
     * Retrieves the URI string corresponding to a resource ID.
     *
     * @param id The resource ID.
     * @return   The URI string.
     */
    private String getUriString(final int id) {
        return ExtensionUtils.getUriString(context, id);
    }

    /**
     *  Set constants
     *   Constant for 3Pi.
     */
    private static final float PI_3 = (float) (Math.PI * 3.0);
    /** Degrees of a full rotation. */
    private static final int FULL_ROTATION_DEGREE = 360;
    /** Degrees of a half rotation. */
    private static final int HALF_ROTATION_DEGREE = FULL_ROTATION_DEGREE / 2;
    /** The entire angle of the vertical range to consider */
    private static final float VER_RANGE = 5.0f;
    /** The amount by which the vertical range can be changed with one action */
    private static final float RANGE_STEP = 2.5f;

    /** Map of registered render objects to their associated bitmaps */
    private SparseArray<RenderObject> objectMap =
            new SparseArray<RenderObject>();

    /**
     *  Initialize program variables
     * The heading direction of the user when app starts
     */
    private int baseDeg;
    /** Fixed-position icon can be shown or not */
    private boolean iconEnabled = false;
    /** Iteration vars for direction update */
    private boolean isFirst = true;
    /** */
    private int count = 0;
    /** Application context */
    private final Context context;
    /** Control utility instance */
    private final SmartEyeglassControlUtils utils;

    /** Uses SmartEyeglass API version*/
    private static final int SMARTEYEGLASS_API_VERSION = 2;

    /** */
    private GlassesRenderObject iconObj;
    /** Sensor management objects */
    private AccessorySensor sensor = null;
    /** */
    private AccessorySensorManager sensorManager;
    /** Vertical range */
    private float verRange = VER_RANGE;

    /**
     * Creates an instance of this control class.
     *      @param context              The context.
     *      @param hostAppPackageName   Package name of host application.
     */
    public ARexp01Control(final Context context,
            final String hostAppPackageName) {
        super(context, hostAppPackageName);
        this.context = context;
        // Manager object for accessing sensor data
        sensorManager = new AccessorySensorManager(context, hostAppPackageName);

        // Handlers for AR events
        SmartEyeglassEventListener listener = new SmartEyeglassEventListener() {
            // Log successful registrations of an AR object
            @Override
            public void onARRegistrationResult(
                    final int result, final int objectId) {
                Log.d(Constants.LOG_TAG,
                        "onARRegistrationResult() result=" + result
                        + " objectId=" + objectId);
            }
            // Find bitmap to render when a registered position is in view
            @Override
            public void onARObjectRequest(
                    final int objectId) {
                Log.d(Constants.LOG_TAG,
                        "onLocalRenderingObjectRequest() "
                        + " objectId=" + objectId);
                // Search for the render object with requested object ID
                RenderObject obj = objectMap.get(objectId);
                if (obj == null) {
                    if (iconObj.getObjectId() != objectId) {
                        return;
                    }
                    obj = iconObj;
                }
                // Send the requested render object
                utils.sendARObjectResponse(obj, 0);
            }
        };
        utils = new SmartEyeglassControlUtils(hostAppPackageName, listener);
        utils.setRequiredApiVersion(SMARTEYEGLASS_API_VERSION);
        utils.activate(context);
    }

    // Clean up data structures on termination.
    @Override
    public void onDestroy() {
        utils.deactivate();
    }

    /** Start the rendering operation. */
    private void renderStart() {
        utils.setRenderMode(Intents.MODE_AR);
        loadResource();
    }

    /**
     * Provides a public method for ExtensionService and Activity to call in
     * order to request start.
     */
    public void requestExtensionStart() {
        startRequest();
    }

    /**
     * Toggle the state of the fixed-position icon
     */
    private void changeIconEnable() {
        iconEnabled = !iconEnabled;
        // When enabled, register the AR rendering object
        if (iconEnabled) {
            registerIcon();
            Log.d(Constants.LOG_TAG, "changeIconEnable(true)");
        } else {
            // When disabled, remove the AR object from the map
            deleteIcon();
            Log.d(Constants.LOG_TAG, "changeIconEnable(false)");
        }
    }

    /**
     * Add the AR render object for the fixed-position icon to the rendering map
     */
    private void registerIcon() {
        registerObject(iconObj);
    }

    /**
     * Remove the fixed-position icon from the rendering map
     */
    private void deleteIcon() {
        if (iconObj.getObjectId() == 0) {
            return;
        }
        utils.deleteARObject(iconObj);
    }

    /**
     * Create render objects and place them next to each other
     * to create a panoramic view made up of multiple images
     */
    private void loadResource() {
        int objectId = 0;
        final List<RenderObject> list = new ArrayList<RenderObject>();
        final int order = context.getResources().getInteger(
                R.integer.CYLINDRINCAL_OBJ_ORDER);
        final float v = 0.0f;
        final float delta = Float.valueOf(
                context.getResources().getString(R.string.POSH_OFFSET));

        // Set h to baseDeg, which is the heading direction of the user when
        // app starts
        float h = baseDeg;
        // Place first image to the center of initial heading direction
        list.add(new CylindricalRenderObject(objectId++,
                getBitmapResource(R.drawable.img_dot), order,
//                getBitmapResource(R.drawable.img_2403_50), order,
                SmartEyeglassControl.Intents.AR_OBJECT_TYPE_STATIC_IMAGE,
                h, v));

        // Use first 5 images to fill right side of the initial heading
        // direction
//        final int[] idArray = {};
//                R.drawable.img_2403_51,
//                R.drawable.img_2403_52,
//                R.drawable.img_2403_53,
//                R.drawable.img_2403_54,
//                R.drawable.img_2403_55};
//        for (int id : idArray) {
//            // Increase h to shift horizontal position for next image
//            h += delta;
//            if (h >= FULL_ROTATION_DEGREE) {
//                h -= FULL_ROTATION_DEGREE;
//            }
//            list.add(new CylindricalRenderObject(objectId++,
//                    getBitmapResource(id), order,
//                    SmartEyeglassControl.Intents.AR_OBJECT_TYPE_STATIC_IMAGE,
//                    h, v));
//        }

        // Use second 5 images to fill left side of the initial heading
        // direction
//        h = baseDeg;
//        final int[] idArray2 = {};
//                R.drawable.img_2403_56,
//                R.drawable.img_2403_55,
//                R.drawable.img_2403_54,
//                R.drawable.img_2403_53,
//                R.drawable.img_2403_52};
//        for (int id : idArray2) {
//            // Decrease h to shift horizontal position for next image
//            h -= delta;
//            if (h < 0.0f) {
//                h += FULL_ROTATION_DEGREE;
//            }
//            list.add(new CylindricalRenderObject(objectId++,
//                    getBitmapResource(id), order,
//                    SmartEyeglassControl.Intents.AR_OBJECT_TYPE_STATIC_IMAGE,
//                    h, v));
//        }

        // Keep render objects in the object map and register them as AR objects
        for (RenderObject obj : list) {
            objectMap.put(obj.getObjectId(), obj);
            registerObject(obj);
        }

        // Create a render object that uses Glasses Coordinate System
        // for the fixed-position icon
        iconObj = new GlassesRenderObject(
                objectId, getBitmapResource(R.drawable.icon3), 0, 0, 0,
                SmartEyeglassControl.Intents.AR_OBJECT_TYPE_STATIC_IMAGE);
        Log.d(Constants.LOG_TAG,"Resource was loaded.");
    }

    /**
     * Send a registered object to be rendered.
     *      @param obj The registered object.
     */
    private void registerObject(final RenderObject obj) {
        Log.d(Constants.LOG_TAG, "registerObject " + obj);
        this.utils.registerARObject(obj);
    }

    /**
     * Convert sensor data to an orientation.
     *      @param x The horizontal coordinate
     *      @param y The vertical coordinate
     *      @return The heading value.
     */
    private static int getHeading(final float x, final float y) {
        double heading = 0;
        if (x == 0 && y < 0) {
            heading = Math.PI / 2.0;
        }
        if (x == 0 && y > 0) {
            heading = PI_3 / 2.0;
        }
        if (x < 0) {
            heading = Math.PI - Math.atan(y / x);
        }
        if (x > 0 && y < 0) {
            heading = -Math.atan(y / x);
        }
        if (x > 0 && y > 0) {
            heading = 2.0 * Math.PI - Math.atan(y / x);
        }
        int d = (int) (heading * HALF_ROTATION_DEGREE / Math.PI);
        if (d < 0) {
            d += FULL_ROTATION_DEGREE;
        }
        return d;
    }

    /**
     * Retrieve a registered bitmap by its resource ID.
     *      @param id   The resource ID.
     *      @return     The bitmap.
     */
    private Bitmap getBitmapResource(final int id) {
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), id);
        b.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return b;
    }

    /**
     * Start the sensors.
     */
    private void sensorStart() {
        count = 0;
        sensor = sensorManager.getSensor(SensorTypeValue.MAGNETIC_FIELD);
        isFirst = true;
        // Start listening for sensor updates.
        if (sensor == null) {
            Log.d(Constants.LOG_TAG, "No such sensor type: "
                    + SensorTypeValue.MAGNETIC_FIELD);
            return;
        }
        try {
            sensor.registerListener(new AccessorySensorEventListener() {
                @Override
                public void onSensorEvent(final AccessorySensorEvent ev) {
                    updateDirection(ev);
                }
            }, Sensor.SensorRates.SENSOR_DELAY_NORMAL, 0);
        } catch (AccessorySensorException e) {
            Log.d(Constants.LOG_TAG, "Failed to register listener");
        }
    }

    /**
     * Update the heading direction from sensor data.
     *      @param ev The sensor event.
     */
    private void updateDirection(final AccessorySensorEvent ev) {
        // Get the vector data and convert to an orientation value
        float[] v = ev.getSensorValues();
        int headDirection = getHeading(v[0], v[1]);
//        Log.d(Constants.LOG_TAG,"updateDirection: " + v[0]);

//        // Send message to Activity
//        SampleExtensionService.Object.sendMessageToActivity(String.valueOf((int)v[0]));

        ++count;
        if (count > 2 && isFirst) {
            baseDeg = headDirection;
//            renderStart();
            isFirst = false;
        }
    }

    public void updateCoordinates(final float x, final float y){
        float v0 = x;
        float v1=y;
        Log.d(Constants.LOG_TAG,"value of xx: "+ String.valueOf(v0));
        Log.d(Constants.LOG_TAG,"value of yy: "+ String.valueOf(v1));
        if(iconObj != null) {
            if (iconObj.getObjectId() != 0) {
                PointF position = new PointF();
                CylindricalRenderObject obj= (CylindricalRenderObject) objectMap.get(0);
                float fW = 30.f;//(float)obj.getBitmap().getWidth();
                position.x = baseDeg +v0;
                position.y = baseDeg +v1;
                Log.d(Constants.LOG_TAG,"value of hie: " + String.valueOf(position.x));
                Log.d(Constants.LOG_TAG,"value of wel: " + String.valueOf(position.y));
                obj.setPosition(position);
                this.utils.moveARObject(obj);
               // SampleExtensionService.Object
                  //      .sendMessageToActivity("Hello Activity");
            }
        }
        //mAngle = (int)x;
        if(v0==0.0&&v1==0.0){
            mAngle=0;
        }
        else if(v0==0.0&&v1==5.0){
            mAngle=1;
        }
        else if(v0==5.0&&v1==0.0){
            mAngle=2;
        }
        else if(v0==5.0&&v1==5.0){
            mAngle=3;
        }
        else if(v0==10.0&&v1==0.0){
            mAngle=4;
        }
        else{
            mAngle=5;
        }
        renderer.update();
        Log.d(Constants.LOG_TAG, "baseDeg : " + String.valueOf(baseDeg));
    }


    // Reset state object and assign initial renderer object
    @Override
    public void onStart() {
        renderer = layoutRenderer;
    }


    // Reset sensor reading when app becomes visible
    @Override
    public void onResume() {
        baseDeg = 0;
        sensorStart();
        renderer.init();
        super.onResume();
    }

    // Stop listening for sensor data when app is paused
    @Override
    public void onPause() {
        // Stop sensor
        if (sensor == null) {
            return;
        }
        // stop listening for sensor data
        sensor.unregisterListener();
        sensor = null;
    }

    // Respond to long touch on touch pad by switching icons
    @Override
    public void onTouch(final ControlTouchEvent ev) {
        super.onTouch(ev);
        if (ev.getAction() != Control.Intents.TOUCH_ACTION_LONGPRESS) {
            return;
        }
        changeIconEnable();
    }

    // Respond to swipe on touch pad by setting vertical range
    @Override
    public void onSwipe(final int direction) {
        switch (direction) {
        // Decrease range by one step
        case Control.Intents.SWIPE_DIRECTION_LEFT:
            verRange -= RANGE_STEP;
            break;
        // Increase range by one step
        case Control.Intents.SWIPE_DIRECTION_RIGHT:
            verRange += RANGE_STEP;
            break;
        default:
            break;
        }
        try {
            Log.d(Constants.LOG_TAG, "Range %f " + verRange);
            // set new vertical range
            utils.changeARCylindricalVerticalRange(verRange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clean up when AR Rendering is disabled
    @Override
    public void onStop() {
        super.onStop();
        objectMap.clear();
        iconEnabled = false;
    }
}
