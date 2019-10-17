package com.example.appconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.ArrayList;

import static com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> imagesArray = new  ArrayList<String>();
    File[] listFile ;

    public final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(android.os.Environment.getExternalStorageDirectory(),"Download/video-portfolio");

        File exitFile = new File(  android.os.Environment.getExternalStorageDirectory(),"Download/video-out/out.mp4"  );

        File imgSequences = new File(  android.os.Environment.getExternalStorageDirectory(),"Download/video-portfolio/temp%03d.png"  );

        //File imgSequences = new File(  android.os.Environment.getExternalStorageDirectory(),"Download/video-portfolio/temp000.png"  );

        getImagesFromExternal();


        //FFmpeg.execute("-start_number 1 -i "+imgSequences+" -c:v mpeg4  -pix_fmt yuv420p "+exitFile);

        FFmpeg.execute("-r 1/5 -i "+imgSequences+" -c:v mpeg4 -r 30 -pix_fmt yuv420p "+exitFile);

        //FFmpeg.execute("-loop 1 -i "+imgSequences+" -c:v mpeg4 -t 30 -pix_fmt yuv420p "+exitFile);


        int rc = FFmpeg.getLastReturnCode();
        String output = FFmpeg.getLastCommandOutput();

        if (rc == RETURN_CODE_SUCCESS) {
            Log.i(Config.TAG, "Command execution completed successfully.");
        } else if (rc == RETURN_CODE_CANCEL) {
            Log.i(Config.TAG, "Command execution cancelled by user.");
        } else {
            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and output=%s.", rc, output));
        }

        FFmpeg.cancel();

    }



    public void getImagesFromExternal(){

        File file = new File(android.os.Environment.getExternalStorageDirectory(),"Download/video-portfolio");


        requestForPermission();


        if(file.isDirectory())
        {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++)
            {
                imagesArray.add(listFile[i].getAbsolutePath());
            }
        }

    }

    public boolean requestForPermission() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if(version >= 23) {
            if(!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }





}
