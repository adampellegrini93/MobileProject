package android.project;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Shake implements SensorEventListener {


    private static final float GRAVITY = 2.7F;
    private static final int TIME = 500;
    private static final int RESET = 3000;

    private OnShakeListener onShakeListener;
    private long marker;
    private int count;


    public void setListener(OnShakeListener listener){
        this.onShakeListener = listener;
    }

    public interface OnShakeListener{
        public void Motion(int tally);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if( onShakeListener != null){
            float a = sensorEvent.values[0];
            float b = sensorEvent.values[1];
            float c = sensorEvent.values[2];

            float gravityA = a / SensorManager.GRAVITY_EARTH;
            float gravityB = b / SensorManager.GRAVITY_EARTH;
            float gravityC = c / SensorManager.GRAVITY_EARTH;

            float gravitationalForce = (float)Math.sqrt(gravityA * gravityA
            + gravityB * gravityB + gravityC * gravityC);

            if(gravitationalForce > GRAVITY){
                final long current = System.currentTimeMillis();

                if(marker + TIME > current){
                    return;
                }

                if(marker + RESET < current){
                    count = 0;
                }

                marker = current;
                count ++;
                onShakeListener.Motion(count);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int dependency){

    }
}
