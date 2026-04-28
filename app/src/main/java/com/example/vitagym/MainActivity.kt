package com.example.vitagym

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.vitagym.navigation.NavigationWrapper
import com.example.vitagym.ui.theme.VitaGymTheme
import timber.log.Timber
import timber.log.Timber.i


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)

        // Add a smooth exit animation to the splash screen
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Create a zoom out animation for the icon
            val zoomOut = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.SCALE_X,
                1f,
                0f
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 400L
            }

            val zoomOutY = ObjectAnimator.ofFloat(
                splashScreenView.iconView,
                View.SCALE_Y,
                1f,
                0f
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 400L
            }

            // Create a fade out animation for the background
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ALPHA,
                1f,
                0f
            ).apply {
                duration = 300L
            }

            // When animations end, remove the splash screen
            fadeOut.doOnEnd { splashScreenView.remove() }

            // Run animations
            zoomOut.start()
            zoomOutY.start()
            fadeOut.start()
        }

        enableEdgeToEdge()
        Timber.plant(Timber.DebugTree())
        i("VitaGym MainActivity started...")
        
        setContent {
            VitaGymTheme {
                NavigationWrapper()
            }        
        }    
    }
}
