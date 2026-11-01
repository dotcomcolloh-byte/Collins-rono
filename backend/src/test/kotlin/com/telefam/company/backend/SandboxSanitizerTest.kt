package com.telefam.company.backend

import com.telefam.company.backend.security.SandboxImageProcessor
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SandboxSanitizerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun testValidJpegSanitizationAndFingerprinting() {
        val sandboxDir = tempFolder.newFolder("quarantine")
        val storageDir = tempFolder.newFolder("storage")
        val processor = SandboxImageProcessor(sandboxDir, storageDir, 5242880)

        // Generate synthetic test image
        val img = BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()
        g.color = Color.RED
        g.fillRect(0, 0, 256, 256)
        g.color = Color.BLUE
        g.drawString("Telefam Test", 50, 50)
        g.dispose()

        val baos = ByteArrayOutputStream()
        ImageIO.write(img, "jpg", baos)
        val rawBytes = baos.toByteArray()

        val artifact = processor.processInSandbox(rawBytes, "image/jpeg")

        assertNotNull(artifact)
        assertEquals("image/jpeg", artifact.verifiedMimeType)
        assertEquals(512, artifact.width)
        assertEquals(512, artifact.height)
        assertTrue(artifact.sha256Checksum.length == 64, "SHA-256 should be 64 hex characters")
        assertTrue(artifact.perceptualDHash.isNotEmpty(), "Perceptual dHash should be non-empty")
        assertTrue(artifact.sanitizedBytes.isNotEmpty(), "Sanitized bytes must be populated")
    }

    @Test
    fun testRejectionOfSpoofedExecutableFile() {
        val sandboxDir = tempFolder.newFolder("quarantine_spoof")
        val storageDir = tempFolder.newFolder("storage_spoof")
        val processor = SandboxImageProcessor(sandboxDir, storageDir, 5242880)

        // Renamed shell script or executable masquerading as JPEG
        val dangerousScript = "#!/bin/bash\necho 'malicious payload'\n".toByteArray()

        assertFailsWith<IllegalArgumentException> {
            processor.processInSandbox(dangerousScript, "image/jpeg")
        }
    }
}
