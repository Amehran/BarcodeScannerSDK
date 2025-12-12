package com.amehran.barcodescanner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.amehran.barcodescanner.di.BarcodeDomainModule
import com.amehran.barcodescanner.domain.ScanBarcodeUseCase
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A fake, controllable implementation of the [ScanBarcodeUseCase] for UI testing.
 * This is provided by the [TestAppModule] to replace the real implementation.
 */
class FakeScanBarcodeUseCase : ScanBarcodeUseCase {
    private val flow = MutableSharedFlow<Result<List<BarcodeResult>>>()
    suspend fun emit(value: Result<List<BarcodeResult>>) = flow.emit(value)
    override fun invoke(bitmap: android.graphics.Bitmap): Flow<Result<List<BarcodeResult>>> = flow
    override fun releaseScanner() {}
}

// This Hilt module will be used in tests instead of the production one.
@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {
    
    private val fakeScanBarcodeUseCase = FakeScanBarcodeUseCase()
    
    @Provides
    @Singleton
    fun provideScanBarcodeUseCase(): ScanBarcodeUseCase = fakeScanBarcodeUseCase
    
    @Provides
    @Singleton
    fun provideFakeScanBarcodeUseCase(): FakeScanBarcodeUseCase = fakeScanBarcodeUseCase
}

// 1. Uninstall the production module to avoid duplicate bindings.
@UninstallModules(BarcodeDomainModule::class)
// 2. Mark this as a Hilt test.
@HiltAndroidTest
class BarcodeScannerScreenTest {

    // 3. Standard Hilt and Compose test rules.
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // 4. Hilt injects the *same* instance of the Fake that the ViewModel gets.
    @Inject
    lateinit var fakeScanBarcodeUseCase: FakeScanBarcodeUseCase

    @Before
    fun setUp() {
        hiltRule.inject()
        // We DO NOT call setContent, as MainActivity does this for us.
    }

    @Test
    fun whenScanIsSuccessful_successUIisDisplayed() {
        // Arrange
        val barcodeValue = "Success-12345"
        val successResult = listOf(
            BarcodeResult(barcodeValue, BarcodeFormat.QR_CODE, BarcodeType.TEXT, barcodeValue)
        )

        // Act: Simulate the continuous camera stream finding a barcode.
        runBlocking {
            fakeScanBarcodeUseCase.emit(Result.success(successResult))
        }

        // Assert: Wait for the UI to update and then confirm the text is displayed.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Barcode Found: $barcodeValue").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Barcode Found: $barcodeValue").assertIsDisplayed()
    }

    @Test
    fun whenScanFails_errorUIisDisplayed() {
        // Arrange
        val errorMessage = "Scanner exploded!"

        // Act: Simulate the continuous camera stream encountering an error.
        runBlocking {
            fakeScanBarcodeUseCase.emit(Result.failure(IOException(errorMessage)))
        }

        // Assert: Wait for the UI to update and confirm the error is displayed.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Error: $errorMessage").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
    }
}
