package com.liushao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.UUID;

@Service
public class ImageStorage {
    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private final Path root;
    public ImageStorage(@Value("${blog.uploads.directory:./data/uploads}") String directory) {
        root = Path.of(directory).toAbsolutePath().normalize();
    }
    public record Uploaded(String url, String filename, int width, int height, long size) {}
    public record Stored(Path path, String contentType) {}

    public Uploaded store(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("请选择图片");
        if (file.getSize() > MAX_BYTES) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "图片不能超过5MB");
        Files.createDirectories(root);
        Path temporary = null;
        try (var input = file.getInputStream(); var stream = ImageIO.createImageInputStream(input)) {
            var readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new IllegalArgumentException("仅支持有效的PNG或JPEG图片");
            var reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!format.equals("png") && !format.equals("jpeg")) throw new IllegalArgumentException("仅支持PNG或JPEG图片");
                reader.setInput(stream, true, true);
                int width = reader.getWidth(0), height = reader.getHeight(0);
                if (width < 1 || height < 1 || width > 8192 || height > 8192 || (long) width * height > 16_000_000)
                    throw new IllegalArgumentException("图片宽高不能超过8192像素，且总像素不能超过1600万");
                var decoded = reader.read(0);
                if (decoded == null) throw new IllegalArgumentException("图片内容损坏");
                String extension = format.equals("jpeg") ? "jpg" : "png";
                String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
                temporary = Files.createTempFile(root, "upload-", ".tmp");
                try {
                    // Re-encode pixels; discard original names, metadata and appended payloads.
                    if (!ImageIO.write(decoded, format, temporary.toFile())) throw new IOException("图片编码失败");
                } finally { decoded.flush(); }
                if (Files.size(temporary) > MAX_BYTES) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "处理后的图片超过5MB，请缩小图片");
                Path destination = root.resolve(filename);
                try { Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE); }
                catch (AtomicMoveNotSupportedException e) { Files.move(temporary, destination); }
                return new Uploaded("/api/article/media/" + filename, filename, width, height, Files.size(destination));
            } finally { reader.dispose(); }
        } catch (javax.imageio.IIOException e) {
            throw new IllegalArgumentException("图片内容损坏或格式不支持");
        } finally { if (temporary != null) Files.deleteIfExists(temporary); }
    }

    public Stored read(String filename) {
        if (!filename.matches("[a-f0-9]{32}\\.(png|jpg)")) throw missing();
        Path path = root.resolve(filename).normalize();
        if (!path.getParent().equals(root) || !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) throw missing();
        return new Stored(path, filename.endsWith(".png") ? "image/png" : "image/jpeg");
    }
    public void delete(String filename) throws IOException { Files.delete(read(filename).path()); }
    private ResponseStatusException missing() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "图片不存在"); }
}
