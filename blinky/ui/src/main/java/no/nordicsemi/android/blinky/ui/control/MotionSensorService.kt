package no.nordicsemi.android.blinky.ui.control

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2

data class MotionData(val pitch: Float, val roll: Float)

@Singleton
class MotionSensorService @Inject constructor(@ApplicationContext private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    private val _motionData = MutableStateFlow(MotionData(0f, 0f))
    val motionData: StateFlow<MotionData> = _motionData

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            // Process gravity sensor data to calculate pitch and roll
            val gravity = event.values
            val roll = atan2(gravity[1].toDouble(), gravity[2].toDouble()).toFloat()
            val pitch = atan2(gravity[0].toDouble(), gravity[2].toDouble()).toFloat()

            _motionData.value = MotionData(pitch, roll)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    fun startMonitoring() {
        gravitySensor?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}