package com.shpp.p2p.cs.vtolmachov.assignment12;

import acm.graphics.GImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Assignment12Part1 {
    /**
     * Array with number of pixels in each silhouette
     */
    private static final ArrayList<Integer> pixelsInShadow = new ArrayList<>();

    private static final double RED_COEF = 0.299;
    private static final double GREEN_COEF = 0.587;
    private static final double BLUE_COEF = 0.114;

    /**
     * Boolean that's decides if background black or white
     * Default - Image has White background, black silhouettes
     *
     */
    private static boolean isBlackBackColor = false;

    /**
     * Array of white pixels that already been visited
     */
    private static boolean[][] visitedPixels;

    /**
     * Minimal RGB for pixel to be white for an eye (not recommended to use bigger number)
     */
    private static final int MINIMUM_COLOR_FOR_WHITE = 240;

    /**
     * Minimal percent of pixels compare to full image to be considered as silhouette (counting in percents(%))
     */
    private static final int MINIMAL_PERCENT_FOR_SILHOUETTE = 0;

    /**
     * Ideal gray parameter (128, 128, 128)
     */
    private static final int RGB_GRAY_PARAMETER = 128;

    static void main(String[] args) {
        String fileName;
        if (args == null || args.length == 0) {
            fileName = "test.jpg";
        } else {
            fileName = args[0];
        }
        try {
            BufferedImage image = createMonochromeVersion(fileName);
            visitedPixels = new boolean[image.getHeight()][image.getWidth()];
            int silhouettes = findSilhouettes(image);
            System.out.println("Number of silhouettes: " + silhouettes);
        } catch (IOException e) {
            System.out.println("Error! Wrong name of the file: " + e.getMessage());
        }
    }

    /**
     * Method, that's takes image, and creates monochrome version based on the original
     *
     * @param fileName Name of the file from where we take image to make it monochrome
     * @return Monochrome version of original image, that we pick
     * @throws IOException Input-output exception if there is no such file or directory
     */
    private static BufferedImage createMonochromeVersion(String fileName) throws IOException {
        BufferedImage image = ImageIO.read(new File("assets/images/" + fileName));
        int pixel, gray;
        int blackNumber = 0,  whiteNumber = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pixel = image.getRGB(x, y);
                gray = (int) (RED_COEF * GImage.getRed(pixel)
                        + GREEN_COEF * GImage.getGreen(pixel) + BLUE_COEF * GImage.getBlue(pixel));

                if (gray < RGB_GRAY_PARAMETER) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                    blackNumber++;
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                    whiteNumber++;
                }
            }
        }

        if(blackNumber > whiteNumber) {
            isBlackBackColor = true;
        }
        return image;
    }

    /**
     * Main method. It gets image, processes, find "white noise", delete it, and give back number of silhouettes
     *
     * @param image Take image from ../assets/images/xxx.jpg
     * @return Number silhouettes that left
     */
    static private int findSilhouettes(BufferedImage image) {
        scanTheImage(image);

        // lambda-function that goes through array, and get us percent that silhouette takes in image.
        // Removes silhouettes which number of pixels < MINIMAL_PERCENT_FOR_SILHOUETTE% comparatively to image square
        pixelsInShadow.removeIf(pixelsInShadow ->
                ((pixelsInShadow * 100) / (image.getHeight() * image.getWidth())) < MINIMAL_PERCENT_FOR_SILHOUETTE);

        return pixelsInShadow.size();
    }


    /**
     * Scanning all monochrome image, so bg must be dimmer than silhouette (task option), so we initialize BFS
     * to find all white pixels that appears
     *
     * @param image monochrome image
     */
    private static void scanTheImage(BufferedImage image) {
        int pixels;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (isPixelSilhouette(image.getRGB(x, y)) && !visitedPixels[x][y]) {
                    pixels = 0;
                    pixelsInShadow.add(goAroundPixels(x, y, image, pixels));
                }
            }
        }
    }


    private static int goAroundPixels(int x, int y, BufferedImage image, int pixels) {
        visitedPixels[x][y] = true;
        pixels++;
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newY >= 0 && newX < image.getWidth() && newY < image.getHeight()) {
                if (pixelInPointFit(newX, newY, image)) {
                    pixels = goAroundPixels(newX, newY, image, pixels);
                }
            }
        }
        return pixels;
    }


    /**
     * Statement that checks pixels to fit requirements for pixel to be part of the silhouette,
     * not in the list of checked and not in
     * current queue
     *
     * @param x current position of pixel by its x
     * @param y current position of pixel by its y
     * @return true if pixel is white and so on, false if one of those conditions failed
     */
    private static boolean pixelInPointFit(int x, int y, BufferedImage image) {
        return isPixelSilhouette(image.getRGB(x, y)) && !visitedPixels[x][y];
    }

    /**
     * Statement that check is current pixel is white
     * All of Red, Blue and green specters compare to "magical number", 'cause some pixels may be white for an eye,
     * but not actually white
     *
     * @param pixel current color of pixel in its specifically compressed form
     * @return true if pixel white, false if one of pixels dimmer than white
     */
    private static boolean isPixelWhite(int pixel) {

        return GImage.getRed(pixel) >= MINIMUM_COLOR_FOR_WHITE
                && GImage.getGreen(pixel) >= MINIMUM_COLOR_FOR_WHITE
                && GImage.getBlue(pixel) >= MINIMUM_COLOR_FOR_WHITE;
    }

    private static boolean isPixelSilhouette(int pixel){
        if(isBlackBackColor){
            return !isPixelWhite(pixel);
        }else{
            return isPixelWhite(pixel);
        }
    }
}
