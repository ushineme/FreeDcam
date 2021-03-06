package freed.jni;

import android.location.Location;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import freed.dng.DngProfile;
import freed.utils.StorageFileHandler;
import freed.utils.StringUtils;

/**
 * Created by troop on 15.02.2015.
 */
public class RawToDng
{
    static
    {
        System.loadLibrary("freedcam");
    }

    private final String TAG = RawToDng.class.getSimpleName();

    private String wbct;
    private byte[] opcode2;
    private byte[] opcode3;

    private static native long GetRawBytesSize();
    private static native int GetRawHeight();
    private static native void SetGPSData(double Altitude,float[] Latitude,float[] Longitude, String Provider, long gpsTime);
    private static native void SetThumbData(byte[] mThumb, int widht, int height);
    private static native void WriteDNG();
    private static native void SetOpCode3(byte[] opcode);
    private static native void SetOpCode2(byte[] opcode);
    private static native void SetRawHeight(int height);
    private static native void SetModelAndMake(String model, String make);
    private static native void SetBayerData(byte[] fileBytes, String fileout);
    private static native void SetBayerDataFD(byte[] fileBytes, int fileout, String filename);
    private static native void SetBayerInfo(float[] colorMatrix1,
                                     float[] colorMatrix2,
                                     float[] neutralColor,
                                     float[] fowardMatrix1,
                                     float[] fowardMatrix2,
                                     float[] reductionMatrix1,
                                     float[] reductionMatrix2,
                                     double[] noiseMatrix,
                                     int blacklevel,
                                     String bayerformat,
                                     int rowSize,
                                     String devicename,
                                     int rawType,int width,int height);
    private static native void SetExifData(int iso,
                                           double expo,
                                           int flash,
                                           float fNum,
                                           float focalL,
                                           String imagedescription,
                                           String orientation,
                                           double exposureIndex);

    private static native void SetDateTime(String datetime);

    public static RawToDng GetInstance()
    {
        return new RawToDng();
    }

    private RawToDng()
    {
        wbct = "";
        File op2 = new File(StringUtils.GetFreeDcamConfigFolder+"opc2.bin");
        if (op2.exists())
            try {
                opcode2 = readFile(op2);
                Log.d(TAG, "opcode2 size" + opcode2.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        File op3 = new File(StringUtils.GetFreeDcamConfigFolder+"opc3.bin");
        if (op3.exists())
            try {
                opcode3 = readFile(op3);
                Log.d(TAG, "opcode3 size" + opcode3.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void SetWBCT(String wbct)
    {
        this.wbct =wbct;
    }

    private float[] getWbCtMatrix(String wbct)
    {
        int wb = Integer.parseInt(wbct) / 100;
        double r,g,b;
        double tmpcol = 0;
        //red

        if( (double) wb <= 66 )
        {
            r = 255;
            g = (double) wb -10;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            if( (double) wb <= 19)
            {
                b = 0;
            }
            else
            {
                b = (double) wb -10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
            }
        }
        else
        {
            r = (double) wb - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            g = (double) wb -60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492);
            b = 255;
        }
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" + r + " g:" + g + " b:" + b);
        float rf,gf,bf = 0;

        rf = (float) getRGBToDouble(checkminmax((int)r))/2;
        gf = (float) getRGBToDouble(checkminmax((int)g));
        bf = (float) getRGBToDouble(checkminmax((int)b))/2;
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
            rf = rf / gf;
            bf = bf / gf;
            gf = 1;
        Log.d(TAG, "ColorTemp=" + (double) wb + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
        return new float[]{rf, gf,bf};
    }

    private double getRGBToDouble(int color)
    {
        double t = color;
        t = t * 3 *2;
        t = t / 255;
        t = t / 3;
        t += 1;

        return t;
    }

    private int checkminmax(int val)
    {
        if (val>255)
            return 255;
        else if(val < 0)
            return 0;
        else return val;
    }




    private long GetRawSize()
    {
        return GetRawBytesSize();
    }

    public void SetGpsData(double Altitude,double Latitude,double Longitude, String Provider, long gpsTime)
    {
        Log.d(TAG,"Latitude:" + Latitude + "Longitude:" +Longitude);
        SetGPSData(Altitude, parseGpsvalue(Latitude), parseGpsvalue(Longitude), Provider, gpsTime);
    }

    public void setExifData(int iso,
                            double expo,
                            int flash,
                            float fNum,
                            float focalL,
                            String imagedescription,
                            String orientation,
                            double exposureIndex)
    {
        SetExifData(iso, expo, flash, fNum, focalL, imagedescription, orientation, exposureIndex);
        SetDateTime(StorageFileHandler.getStringDatePAttern().format(new Date()));
    }

    private float[] parseGpsvalue(double val)
    {

        String[] sec = Location.convert(val, Location.FORMAT_SECONDS).split(":");

        double dd = Double.parseDouble(sec[0]);
        double dm = Double.parseDouble(sec[1]);
        double ds = Double.parseDouble(sec[2].replace(",","."));

        return new float[]{ (float)dd ,(float)dm,(float)ds};
    }

    public void setThumbData(byte[] mThumb, int widht, int height)
    {
        SetThumbData(mThumb, widht,height);
    }

    private void SetModelAndMake(String make)
    {
        SetModelAndMake(Build.MODEL, Build.MANUFACTURER);
    }

    public void setBayerData(byte[] fileBytes, String fileout) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        SetBayerData(fileBytes, fileout);
        if (opcode2 != null)
            SetOpCode2(opcode2);
        if (opcode3 != null)
            SetOpCode3(opcode3);

    }

    public void SetBayerDataFD(byte[] fileBytes, ParcelFileDescriptor fileout, String filename) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }

        SetBayerDataFD(fileBytes, fileout.getFd(), filename);
        if (opcode2 != null)
            SetOpCode2(opcode2);
        if (opcode3 != null)
            SetOpCode3(opcode3);
    }


    private void SetBayerInfo(float[] colorMatrix1,
                              float[] colorMatrix2,
                              float[] neutralColor,
                              float[] fowardMatrix1,
                              float[] fowardMatrix2,
                              float[] reductionMatrix1,
                              float[] reductionMatrix2,
                              double[] noise,
                              int blacklevel,
                              String bayerformat,
                              int rowSize,
                              int tight, int width, int height)
    {
        if (wbct.equals(""))
            SetBayerInfo(colorMatrix1, colorMatrix2, neutralColor, fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel, bayerformat, rowSize, Build.MODEL, tight, width, height);
        else if (!wbct.equals(""))
            SetBayerInfo(colorMatrix1, colorMatrix2, getWbCtMatrix(wbct), fowardMatrix1, fowardMatrix2, reductionMatrix1, reductionMatrix2, noise, blacklevel, bayerformat, rowSize, Build.MODEL, tight, width, height);

    }


    private void setRawHeight(int height)
    {
        SetRawHeight(height);
    }


    public void WriteDngWithProfile(DngProfile profile)
    {
        if (profile == null)
            return;
        SetModelAndMake(Build.MANUFACTURER);
        SetBayerInfo(profile.matrixes.ColorMatrix1, profile.matrixes.ColorMatrix2, profile.matrixes.NeutralMatrix,
                profile.matrixes.ForwardMatrix1,profile.matrixes.ForwardMatrix2,
                profile.matrixes.ReductionMatrix1,profile.matrixes.ReductionMatrix2,profile.matrixes.NoiseReductionMatrix,profile.blacklevel, profile.BayerPattern, profile.rowsize, profile.rawType,profile.widht,profile.height);
        WriteDNG();
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
