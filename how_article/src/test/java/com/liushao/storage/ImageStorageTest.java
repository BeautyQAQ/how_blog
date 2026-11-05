package com.liushao.storage;

import com.liushao.service.ImageStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ImageStorageTest {
    @TempDir Path directory;
    static byte[] png(int width, int height) throws Exception {
        var output = new ByteArrayOutputStream(); var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "png", output); image.flush(); return output.toByteArray();
    }
    @Test void storesDecodedPixelsWithGeneratedNamesAndSurvivesANewStorageInstance() throws Exception {
        var original = new ByteArrayOutputStream(); original.write(png(20, 10)); original.write("<script>payload</script>".getBytes(StandardCharsets.UTF_8));
        var storage = new ImageStorage(directory.toString());
        var result = storage.store(new MockMultipartFile("file", "../../evil.html", "text/html", original.toByteArray()));
        assertTrue(result.filename().matches("[a-f0-9]{32}\\.png"));
        var stored = new ImageStorage(directory.toString()).read(result.filename());
        assertEquals(20, ImageIO.read(stored.path().toFile()).getWidth());
        assertFalse(new String(Files.readAllBytes(stored.path()), StandardCharsets.ISO_8859_1).contains("<script>payload"));
        assertEquals("image/png", stored.contentType()); storage.delete(result.filename());
        assertThrows(ResponseStatusException.class, () -> storage.read(result.filename()));
    }
    @Test void rejectsSvgFakeImagesLargeFilesAndExcessiveDimensions() throws Exception {
        var storage = new ImageStorage(directory.toString());
        assertThrows(IllegalArgumentException.class, () -> storage.store(new MockMultipartFile("file", "x.png", "image/png", "<svg onload='bad'/>".getBytes())));
        var tooLarge = new MockMultipartFile("file", new byte[]{1}) { @Override public long getSize() { return ImageStorage.MAX_BYTES + 1; } };
        assertEquals(413, assertThrows(ResponseStatusException.class, () -> storage.store(tooLarge)).getStatusCode().value());
        assertThrows(IllegalArgumentException.class, () -> storage.store(new MockMultipartFile("file", png(8193, 1))));
        assertThrows(ResponseStatusException.class, () -> storage.read("../outside.png"));
        try (var files = Files.list(directory)) { assertEquals(0, files.count()); }
    }
}
