package it.sephiroth.android.app.simplelogger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import junit.framework.Assert;

import java.io.IOException;

import it.sephiroth.android.library.simplelogger.LoggerFactory;

public class MainActivity extends AppCompatActivity {

    LoggerFactory.Logger logger = LoggerFactory.getLogger("MainActivity", LoggerFactory.LoggerType.Console);

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERMAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        logger.info("Info message: %s", "test string");
        logger.verbose("Verbose message: %d", 1);
        logger.debug("Debug message: %b", false);
        logger.error("Error message: %g", 1.5f);
        logger.warn("Warning message");
        logger.log(new IOException("test io exception"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERMAL_STORAGE: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted(true);
                } else {
                    onPermissionGranted(false);
                }
            }
        }
    }

    private void onPermissionGranted(boolean granted) {

        if (!granted) {
            Snackbar.make(findViewById(R.id.activity_main),
                "You need to allow permissions moron", Snackbar.LENGTH_LONG
            )
                .setAction(
                    android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXTERMAL_STORAGE
                            );

                        }
                    }
                ).show();
        } else {
            initActivity();
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            onPermissionGranted(false);
        } else {
            ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_WRITE_EXTERMAL_STORAGE
            );
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissions();
        } else {
            initActivity();
        }
    }

    private void initActivity() {
        LoggerFactory.FileLogger fileLogger = LoggerFactory.getFileLogger("test.log");
        fileLogger.clear();

        fileLogger.setLevel(Log.ERROR);
        fileLogger.info("Info message: %s", "test string");
        fileLogger.verbose("Verbose message: %d", 1);
        fileLogger.debug("Debug message: %b", false);
        fileLogger.error("Error message: %g", 1.5f);
        fileLogger.warn("Warning message");
        fileLogger.log(new IOException("test io exception"));

        LoggerFactory.FileLogger fileLogger2 = LoggerFactory.getFileLogger("log.txt");
        fileLogger2.setLevel(Log.INFO);
        fileLogger2.info("Info message: %s", "test string");
        fileLogger2.verbose("Verbose message: %d", 1);
        fileLogger2.debug("Debug message: %b", false);
        fileLogger2.error("Error message: %g", 1.5f);
        fileLogger2.warn("Warning message");
        fileLogger2.log(new IOException("test io exception"));

        LoggerFactory.FileLogger fileLogger3 = LoggerFactory.getFileLogger("test.log");
        Assert.assertEquals(fileLogger, fileLogger3);

        fileLogger.close();
        fileLogger2.close();
    }

}
