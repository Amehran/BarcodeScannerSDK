package com.amehran.barcodescanner

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.hilt.navigation.compose.hiltViewModel
import com.amehran.barcodescanner.di.BarcodeDomainModule
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
import com.amehran.barcodescanner.presentation.scanner.BarcodeViewModel
import com.amehran.barcodescanner.ui.screens.BarcodeScannerScreen
import com.amehran.scanner.domain.model.BarcodeFormat
import com.amehran.scanner.domain.model.BarcodeResult
import com.amehran.scanner.domain.model.BarcodeType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

// 1. Mock the dependency we want to control in our tests
val mockScanBarcodeUseCase: ScanBarcodeUseCase = mockk(relaxed = true)

// 2. Create a Test Hilt Module to provide the mock dependency
@Module
@InstallIn(SingletonComponent::class)
object TestBarcodeDomainModule {
    @Provides
    @Singleton
    fun provideScanBarcodeUseCase(): ScanBarcodeUseCase {
        return mockScanBarcodeUseCase
    }
}

// 3. Uninstall the production module and create the test class
@UninstallModules(BarcodeDomainModule::class)
@HiltAndroidTest
class BarcodeScannerScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private var viewModel: BarcodeViewModel? = null

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun whenScanIsSuccessful_successUIisDisplayed() {
        // Arrange
        val barcodeValue = "Success-12345"
        val successResult = listOf(
            BarcodeResult(barcodeValue, BarcodeFormat.QR_CODE, BarcodeType.TEXT, barcodeValue)
        )
        every { mockScanBarcodeUseCase(any()) } returns flowOf(Result.success(successResult))

        // Act
        composeTestRule.setContent {
            // Capture the ViewModel instance to interact with it directly
            viewModel = hiltViewModel()
            BarcodeScannerScreen(viewModel = viewModel!!)
        }

        // Trigger the scan logic by calling the ViewModel method
        composeTestRule.activity.runOnUiThread {
            viewModel?.processBarcodeScan(mockk<Bitmap>(relaxed = true))
        }

        // Assert
        composeTestRule.onNodeWithText("Barcode Found: $barcodeValue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scan Another").assertIsDisplayed()
    }
}
