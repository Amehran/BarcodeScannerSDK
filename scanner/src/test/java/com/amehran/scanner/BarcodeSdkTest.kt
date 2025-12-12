package com.amehran.scanner

import android.content.Context
import com.amehran.scanner.api.BarcodeSDK
import com.amehran.scanner.di.SdkEntryPoint
import com.amehran.scanner.domain.BarcodeScanner
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.EntryPointAccessors
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class BarcodeSdkTest {

    private lateinit var mockContext: Context
    private lateinit var mockAppContext: Context
    private lateinit var mockEntryPoint: SdkEntryPoint
    private lateinit var mockScanner: BarcodeScanner

    @Before
    fun setUp() {
        // Reset the singleton instance before each test
        resetSdkInstance()
        
        // Create mocks
        mockContext = mockk(relaxed = true)
        mockAppContext = mockk(relaxed = true)
        mockEntryPoint = mockk(relaxed = true)
        mockScanner = mockk(relaxed = true)
        
        // Setup mock behavior
        every { mockContext.applicationContext } returns mockAppContext
        every { mockEntryPoint.getBarcodeScannerService() } returns mockScanner
        
        // Mock the static EntryPointAccessors
        mockkStatic(EntryPointAccessors::class)
        every { 
            EntryPointAccessors.fromApplication<SdkEntryPoint>(
                mockAppContext, 
                SdkEntryPoint::class.java
            ) 
        } returns mockEntryPoint
    }

    @After
    fun tearDown() {
        unmockkAll()
        resetSdkInstance()
    }

    @Test
    fun `initialize creates and returns scanner instance`() {
        // Act
        val result = BarcodeSDK.initialize(mockContext)

        // Assert
        assertThat(result).isNotNull()
        assertThat(result).isSameInstanceAs(mockScanner)
        verify { mockContext.applicationContext }
        verify { mockEntryPoint.getBarcodeScannerService() }
    }

    @Test
    fun `initialize returns same instance on multiple calls (singleton pattern)`() {
        // Act
        val instance1 = BarcodeSDK.initialize(mockContext)
        val instance2 = BarcodeSDK.initialize(mockContext)
        val instance3 = BarcodeSDK.initialize(mockContext)

        // Assert
        assertThat(instance1).isSameInstanceAs(instance2)
        assertThat(instance2).isSameInstanceAs(instance3)
        
        // Verify initialization only happened once
        verify(exactly = 1) { mockEntryPoint.getBarcodeScannerService() }
    }

    @Test
    fun `initialize uses application context not activity context`() {
        // Act
        BarcodeSDK.initialize(mockContext)

        // Assert
        verify { mockContext.applicationContext }
        verify { 
            EntryPointAccessors.fromApplication(
                mockAppContext, 
                SdkEntryPoint::class.java
            ) 
        }
    }

    @Test
    fun `initialize throws exception when EntryPoint fails`() {
        // Arrange
        val originalException = IllegalStateException("Hilt not initialized")
        every { 
            EntryPointAccessors.fromApplication<SdkEntryPoint>(
                any(), 
                any()
            ) 
        } throws originalException

        // Act & Assert
        try {
            BarcodeSDK.initialize(mockContext)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: IllegalStateException) {
            // SDK wraps the exception with additional context
            assertThat(e.message).contains("BarcodeSDK initialization failed")
            assertThat(e.cause).isEqualTo(originalException)
        }
    }

    @Test
    fun `initialize throws exception when scanner service creation fails`() {
        // Arrange
        val expectedException = RuntimeException("Failed to create scanner")
        every { mockEntryPoint.getBarcodeScannerService() } throws expectedException

        // Act & Assert
        try {
            BarcodeSDK.initialize(mockContext)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertThat(e).isEqualTo(expectedException)
        }
    }

    @Test
    fun `initialize with different contexts returns same instance`() {
        // Arrange
        val mockContext2 = mockk<Context>(relaxed = true)
        every { mockContext2.applicationContext } returns mockAppContext

        // Act
        val instance1 = BarcodeSDK.initialize(mockContext)
        val instance2 = BarcodeSDK.initialize(mockContext2)

        // Assert
        assertThat(instance1).isSameInstanceAs(instance2)
        verify(exactly = 1) { mockEntryPoint.getBarcodeScannerService() }
    }

    /**
     * Helper function to reset the singleton instance using reflection.
     * This is necessary because the SDK uses a private static field.
     */
    private fun resetSdkInstance() {
        try {
            val instanceField = BarcodeSDK::class.java.getDeclaredField("instance")
            instanceField.isAccessible = true
            instanceField.set(null, null)
        } catch (e: Exception) {
            // If reflection fails, it's okay - the field might not exist yet
        }
    }
}
