package org.berendeev.weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * @throws LocationProviderException
     */
    suspend fun getCurrentLocation(): Result {
        if (isPermissionGranted()) {
            return getCurrentLocationOrThrow()
        } else {
            throw LocationProviderException("Location permission denied")
        }
    }

    private fun isPermissionGranted() = ActivityCompat
        .checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocationOrThrow(): Result {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationProviderClient.flushLocations()
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(Result(location.asCoordinates()))
                }.addOnFailureListener { e ->
                    continuation.resumeWithException(
                        LocationProviderException(e.message ?: "Unknown error", e)
                    )
                }
        }
    }

    private fun Location.asCoordinates(): Coordinates {
        return Coordinates(this.latitude, this.longitude)
    }

    class Result(val coordinates: Coordinates)
}

class LocationProviderException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
