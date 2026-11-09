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
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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

    public String processImage(MultipartFile multipartFile) throws IOException, TesseractException {

        try {
            BufferedImage bi = convertToImage(multipartFile);
            BufferedImage scale = scaleImage(bi);
            BufferedImage binarized = binarize(scale);
            return runTesseract(binarized);
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

    public static BufferedImage scaleImage(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();

        BufferedImage scaled = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        AffineTransform at = AffineTransform.getScaleInstance(2.0, 2.0);

        g.drawImage(bi, at, null);
        g.dispose();
        return scaled;
    }

    public static BufferedImage grayScale(BufferedImage image) {

        BufferedImage grayScaledImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayScaledImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayScaledImage;
    }

    public static double optimizeIsoData(BufferedImage image) {

        Raster raster = image.getRaster();

        double pixelValueSum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pixelValueSum += raster.getSample(x, y, 0);
            }
        }
        // Start value // T-sub-1 counter
        double currentThreshold = pixelValueSum / (image.getWidth() * image.getHeight());

        double oldThreshold;
        do {
            long sumBackGround = 0, sumForeGround = 0;
            int backGroundCount = 0, foreGroundCount = 0;

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixelValues = raster.getSample(x, y, 0);

                    if (pixelValues <= currentThreshold) {
                        sumBackGround += pixelValues;
                        backGroundCount++;
                    }

                    if (pixelValues > currentThreshold) {
                        sumForeGround += pixelValues;
                        foreGroundCount++;
                    }
                }
            }

            if (backGroundCount == 0 || foreGroundCount == 0) {
                break;
            }

            double backAve = (double) sumBackGround / backGroundCount;
            double frontAve = (double) sumForeGround / foreGroundCount;

            oldThreshold = currentThreshold;
            currentThreshold = (backAve + frontAve) / 2;

        } while(Math.abs(oldThreshold - currentThreshold) >= 1);

        return currentThreshold;
    }

    public static BufferedImage binarize(BufferedImage originalImage) {

        BufferedImage grayScaledImg = grayScale(originalImage);
        double setImageThreshold = optimizeIsoData(grayScaledImg);

        BufferedImage binarizedImage = new BufferedImage(grayScaledImg.getWidth(), grayScaledImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster binarizedRaster = binarizedImage.getRaster();

        Raster greyImgRaster = grayScaledImg.getRaster();
        for (int x = 0; x < grayScaledImg.getWidth(); x++) {
            for (int y = 0; y < grayScaledImg.getHeight(); y++) {
                    int value = greyImgRaster.getSample(x, y, 0);

                int output = (value > setImageThreshold) ? 255 : 0;

                binarizedRaster.setSample(x, y, 0, output);
            }
        }
        return binarizedImage;
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

