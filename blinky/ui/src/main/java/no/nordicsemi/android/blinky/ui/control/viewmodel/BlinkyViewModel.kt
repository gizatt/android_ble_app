package no.nordicsemi.android.blinky.ui.control.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.nordicsemi.android.blinky.ui.control.MotionData
import no.nordicsemi.android.blinky.ui.control.MotionSensorService
import no.nordicsemi.android.blinky.spec.GamepadInput
import no.nordicsemi.android.blinky.ui.control.repository.GamepadRepository
import no.nordicsemi.android.common.logger.LoggerLauncher
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class GamepadUIOutput(
    var enable: Boolean,
    var speed: Float,
    var leftJoystickX: Float,
    var leftJoystickY: Float,
    var rightJoystickX: Float,
    var rightJoystickY: Float,
    var height: Float,
    var motionEnable: Boolean,
) {
}

fun Float.toSignedByte(): Byte {
    val intValue = (this.coerceIn(-1.0F, 1.0F) * 127).toInt()
    return intValue.toByte()
}
fun Float.toUnsignedByte(): UByte {
    val intValue = (this.coerceIn(0.0F, 1.0F) * 255).toInt()
    return intValue.toUByte()
}

/**
 * The view model for the Blinky screen.
 *
 * @param context The application context.
 * @property repository The repository that will be used to interact with the device.
 * @property deviceName The name of the Blinky device, as advertised.
 */
@HiltViewModel
class BlinkyViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: GamepadRepository,
    val motionSensorService: MotionSensorService,
    @Named("deviceName") val deviceName: String,
) : AndroidViewModel(context as Application) {
    /** The connection state of the device. */
    val state = repository.state
    /** The button state. */
    val t = repository.loggedGamepadOutputState
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Zero point for motion control
    private var motionZero : MotionData? = null
    private var lastUIInput: GamepadUIOutput? = null

    init {
        // In this sample we want to connect to the device as soon as the view model is created.
        connect()

        // Start listening to the motion sensor too in case the UI wants to use this data.
        // This will run as long as the view model is live.
        viewModelScope.launch {
            motionSensorService.motionData.collect { motionData ->
                // Process motion data and update UI state
                println("Pitch: ${motionData.pitch}, Roll: ${motionData.roll}")
                if (lastUIInput != null) {
                    updateRepository()
                }
            }
        }
        motionSensorService.startMonitoring()
    }

    /**
     * Connects to the device.
     */
    fun connect() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            // This method may throw an exception if the connection fails,
            // Bluetooth is disabled, etc.
            // The exception will be caught by the exception handler and will be ignored.
            repository.connect()
        }
    }


    /**
     * Sends a command to the device.
     */
    fun setGamepadState(input: GamepadUIOutput) {
        lastUIInput = input
        updateRepository()
    }

    private fun updateRepository(){
        val input = lastUIInput!!
        val exceptionHandler = CoroutineExceptionHandler { _, _ -> }
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            var pitch = 0.0f
            var roll = 0.0f
            if (input.motionEnable){
                if (motionZero == null) {
                    motionZero = motionSensorService.motionData.value
                }
                pitch = (motionSensorService.motionData.value.pitch - motionZero!!.pitch) * -2.0F
                roll = (motionSensorService.motionData.value.roll - motionZero!!.roll) * -2.0F
            } else {
                motionZero = null
            }

            // Log all values
            Timber.d("Pitch and roll outputs: $pitch, $roll")

            // Just like above, when this method throws an exception, it will be caught by the
            // exception handler and ignored.
            repository.setGamepadState(GamepadInput(
                input.enable,
                input.speed.toUnsignedByte(),
                input.leftJoystickX.toSignedByte(),
                input.leftJoystickY.toSignedByte(),
                input.rightJoystickX.toSignedByte(),
                input.rightJoystickY.toSignedByte(),
                pitch=pitch.toSignedByte(),
                roll=roll.toSignedByte(),
                height=input.height.toSignedByte(),
            ))
        }
    }

    /**
     * Opens nRF Logger app with the log or Google Play if the app is not installed.
     */
    fun openLogger() {
        LoggerLauncher.launch(getApplication(), repository.logSession)
    }

    override fun onCleared() {
        super.onCleared()
        motionSensorService.stopMonitoring()
        repository.release()
    }
}