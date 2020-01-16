package teamcode.state;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.Bitmap.createBitmap;
import static android.graphics.Bitmap.createScaledBitmap;

public class VisionOnInit {

    VuforiaLocalizer vuforia;
    private static final String VUFORIA_KEY = "AQR2KKb/////AAABmcBOjjqXfkjtrjI9/Ps5Rs1yoVMyJe0wdjaX8pHqOaPu2gRcObwPjsuWCCo7Xt52/kJ4dAZfUM5Gy73z3ogM2E2qzyVObda1EFHZuUrrYkJzKM3AhY8vUz6R3fH0c/R9j/pufFYAABOAFoc5PtjMQ2fbeFI95UYXtl0u+6OIkCUJ3Zw71tvoD9Fs/cOiLB45FrWrxHPbinEhsOlCTWK/sAC2OK2HuEsBFCebaV57vKyATHW4w2LMWEZaCByHMk9RJDR38WCqivXz753bsiBVMbCzPYzwzc3DKztTbK8/cXqPPBLBKwU8ls0RN52akror1xE9lPwwksMXwJwolpyIZGnZngWcBWX4lLH+HlDNZ8Qm";

    public VisionOnInit(HardwareMap hardwareMap) {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().
                getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        WebcamName webcamName = hardwareMap.get(WebcamName.class, Constants.WEBCAM);
        parameters.cameraName = webcamName;
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    public enum SkystonePos {
        LEFT, CENTER, RIGHT;
    }

    public SkystonePos vuforiascan(boolean saveBitmaps, boolean red) {
        Image rgbImage = null;
        int rgbTries = 0;
        /*
        double colorcountL = 0;
        double colorcountC = 0;
        double colorcountR = 0;
        */
        double yellowCountL = 1;
        double yellowCountC = 1;
        double yellowCountR = 1;

        double blackCountL = 1;
        double blackCountC = 1;
        double blackCountR = 1;
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        VuforiaLocalizer.CloseableFrame closeableFrame = null;
        this.vuforia.setFrameQueueCapacity(1);
        while (rgbImage == null) {
            try {
                closeableFrame = this.vuforia.getFrameQueue().take();
                long numImages = closeableFrame.getNumImages();

                for (int i = 0; i < numImages; i++) {
                    if (closeableFrame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                        rgbImage = closeableFrame.getImage(i);
                        if (rgbImage != null) {
                            break;
                        }
                    }
                }
            } catch (InterruptedException exc) {

            } finally {
                if (closeableFrame != null) closeableFrame.close();
            }
        }

        if (rgbImage != null) {

            // copy the bitmap from the Vuforia frame
            Bitmap bitmap = Bitmap.createBitmap(rgbImage.getWidth(), rgbImage.getHeight(), Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(rgbImage.getPixels());

            String path = Environment.getExternalStorageDirectory().toString();
            FileOutputStream out = null;

            String bitmapName;
            String croppedBitmapName;

            if (red) {
                bitmapName = "BitmapRED.png";
                croppedBitmapName = "BitmapCroppedRED.png";
            } else {
                bitmapName = "BitmapBLUE.png";
                croppedBitmapName = "BitmapCroppedBLUE.png";
            }

            //Save bitmap to file
            if (saveBitmaps) {
                try {
                    File file = new File(path, bitmapName);
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            int cropStartX;
            int cropStartY;
            int cropWidth;
            int cropHeight;

            if (red) {
                cropStartX = (int) ((120.0 / 720.0) * bitmap.getWidth());
                cropStartY = (int) ((100.0 / 480.0) * bitmap.getHeight());
                cropWidth = (int) ((590.0 / 720.0) * bitmap.getWidth());
                cropHeight = (int) ((170.0 / 480.0) * bitmap.getHeight());
            } else {
                cropStartX = (int) ((370.0 / 1280.0) * bitmap.getWidth());
                cropStartY = (int) ((130.0 / 720.0) * bitmap.getHeight());
                cropWidth = (int) ((890.0 / 1280.0) * bitmap.getWidth());
                cropHeight = (int) ((165.0 / 720.0) * bitmap.getHeight());
            }

//            Debug.log("vuforiascan"
//                    + " cropStartX: " + cropStartX
//                    + " cropStartY: " + cropStartY
//                    + " cropWidth: " + cropWidth
//                    + " cropHeight: " + cropHeight
//                    + " Width: " + bitmap.getWidth()
//                    + " Height: " + bitmap.getHeight()
//           );


            bitmap = createBitmap(bitmap, cropStartX, cropStartY, cropWidth, cropHeight); //Cropped Bitmap to show only stones

            // Save cropped bitmap to file
            if (saveBitmaps) {
                try {
                    File file = new File(path, croppedBitmapName);
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            bitmap = createScaledBitmap(bitmap, 110, 20, true); //Compress bitmap to reduce scan time


            int height;
            int width;
            int pixel;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int colWidth = (int) ((double) bitmapWidth / 6.0);
            int colorLStartCol = (int) ((double) bitmapWidth * (1.0 / 6.0) - ((double) colWidth / 2.0));
            int colorCStartCol = (int) ((double) bitmapWidth * (3.0 / 6.0) - ((double) colWidth / 2.0));
            int colorRStartCol = (int) ((double) bitmapWidth * (5.0 / 6.0) - ((double) colWidth / 2.0));

            for (height = 0; height < bitmapHeight; ++height) {
                for (width = colorLStartCol; width < colorLStartCol + colWidth; ++width) {
                    pixel = bitmap.getPixel(width, height);
                    if (Color.red(pixel) < 200 || Color.green(pixel) < 200 || Color.blue(pixel) < 200) {
                        yellowCountL += Color.red(pixel);
                        blackCountL += Color.blue(pixel);
                    }

                    /*
                    if (Color.red(pixel) > 120 && Color.green(pixel) > 80 && Color.blue(pixel) < 20) {
                        yellowCountL += 1;
                    } else if (Color.red(pixel) < 120 && Color.green(pixel) < 120 && Color.blue(pixel) < 120) {
                        blackCountL += 1;
                    }
                     */

                    //colorcountL += Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                }
                for (width = colorCStartCol; width < colorCStartCol + colWidth; ++width) {
                    pixel = bitmap.getPixel(width, height);

                    if (Color.red(pixel) < 200 || Color.green(pixel) < 200 || Color.blue(pixel) < 200) {
                        yellowCountC += Color.red(pixel);
                        blackCountC += Color.blue(pixel);
                    }
                    /*
                    if (Color.red(pixel) > 120 && Color.green(pixel) > 80 && Color.blue(pixel) < 20) {
                        yellowCountC += 1;
                    } else if (Color.red(pixel) < 120 && Color.green(pixel) < 120 && Color.blue(pixel) < 120) {
                        blackCountC += 1;
                    }
                    */
                    //colorcountC += Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                }

                for (width = colorRStartCol; width < colorRStartCol + colWidth; ++width) {
                    pixel = bitmap.getPixel(width, height);

                    if (Color.red(pixel) < 200 || Color.green(pixel) < 200 || Color.blue(pixel) < 200) {
                        yellowCountR += Color.red(pixel);
                        blackCountR += Color.blue(pixel);
                    }
                    /*
                    if (Color.red(pixel) > 120 && Color.green(pixel) > 80 && Color.blue(pixel) < 20) {
                        yellowCountR += 1;
                    } else if (Color.red(pixel) < 120 && Color.green(pixel) < 120 && Color.blue(pixel) < 120) {
                        blackCountR += 1;
                    }
                    */
                    //colorcountR += Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                }
            }
        }

        double blackYellowRatioL = blackCountL / yellowCountL;
        double blackYellowRatioC = blackCountC / yellowCountC;
        double blackYellowRatioR = blackCountR / yellowCountR;


        SkystonePos pos;
        /*
        DbgLog.msg("color L: " + Double.toString(colorcountL));
        DbgLog.msg("color C: " + Double.toString(colorcountC));
        DbgLog.msg("color R: " + Double.toString(colorcountR));
        if (colorcountL < colorcountC && colorcountL < colorcountR) {
            pos = skystonePos.LEFT;
        } else if (colorcountC < colorcountL && colorcountC < colorcountR) {
            pos = skystonePos.CENTER;
        } else {
            pos = skystonePos.RIGHT;
        }
*/
        if (blackYellowRatioL > blackYellowRatioC && blackYellowRatioL > blackYellowRatioR) {
            pos = SkystonePos.LEFT;
        } else if (blackYellowRatioC > blackYellowRatioL && blackYellowRatioC > blackYellowRatioR) {
            pos = SkystonePos.CENTER;
        } else {
            pos = SkystonePos.RIGHT;
        }

//        Debug.log("black/yellow L: " + blackCountL + "/" + yellowCountL);
//        Debug.log("black/yellow C: " + blackCountC + "/" + yellowCountC);
//        Debug.log("black/yellow R: " + blackCountR + "/" + yellowCountR);

        return pos;
    }
}