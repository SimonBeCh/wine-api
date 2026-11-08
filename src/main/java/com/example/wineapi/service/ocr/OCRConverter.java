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
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.Buffer;

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
            bi = binarize(bi);
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

    public static BufferedImage grayScale(BufferedImage image) {

        BufferedImage grayScaledImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = grayScaledImage.getRaster();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb & 0xff);

                float rr = (float) Math.pow(r / 255.0, 2.2);
                float gg = (float) Math.pow(g / 255.0, 2.2);
                float bb = (float) Math.pow(b / 255.0, 2.2);

                float lum = (float) (0.2126 * rr + 0.7152 * gg + 0.0722 * bb);

                int grayLevel = (int) (255.0 * Math.pow(lum, 1.0 / 2.2));
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;

                raster.setSample(x, y, 0, gray);
            }
        }
        return grayScaledImage;
    }

    /*
    public static BufferedImage binarize(BufferedImage originalImage) {

        BufferedImage grayScaledImg = grayScale(originalImage);
        double setImageThreshold = optimizeIsoData(grayScaledImg);

        BufferedImage binarizedImage = new BufferedImage(grayScaledImg.getWidth(), grayScaledImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster binarizedRaster = binarizedImage.getRaster();

        Raster greyImgRaster = grayScaledImg.getRaster();
        for (int x = 0; x < grayScaledImg.getWidth(); x++) {
            for (int y = 0; y < grayScaledImg.getHeight(); y++) {

            }
        }
        return binarizedImage;
    }
*/
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