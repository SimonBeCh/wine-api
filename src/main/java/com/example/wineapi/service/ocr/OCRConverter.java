package com.example.wineapi.service.ocr;

import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class OCRConverter {

    @Value("${ocr.tessdata-path}")
    private String TESSDATA_PATH;

    @Value("${ocr.language}")
    private String LANGUAGE_PATH;

    private static final Logger LOGGER = LoggerFactory.getLogger(OCRConverter.class);

    @PostConstruct
    public void init() {
        checkIfTesseractPathIsSet();
    }

    public String processImage(MultipartFile image) throws IOException, TesseractException {

        try {
            BufferedImage bi = convertToImage(image);
            return runTesseract(bi);
        } catch (IllegalArgumentException e) {
            LOGGER.error("File could not be read");
            throw new IOException("File could not be read");
        }
    }

    private BufferedImage convertToImage(MultipartFile multipartFile) throws IOException, IllegalArgumentException {

        if (multipartFile == null) {
            LOGGER.error("File passed to convertToImage was null");
            throw new IllegalArgumentException("File cannot be null");
        }
        byte[] array = multipartFile.getBytes();
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(array));
        if (bi == null) {
            LOGGER.debug("Conversion result was null");
            throw new IOException("result of conversion was null");
        }
        return bi;
    }

    public String runTesseract(BufferedImage bi) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(TESSDATA_PATH);
        tesseract.setLanguage(LANGUAGE_PATH);
        return tesseract.doOCR(bi);
    }

    public void checkIfTesseractPathIsSet() {
        if (TESSDATA_PATH == null || TESSDATA_PATH.isEmpty()) {
            LOGGER.error("Tessdata path is not set");
            throw new IllegalArgumentException("Tessdata path is not set");
        }
    }
}