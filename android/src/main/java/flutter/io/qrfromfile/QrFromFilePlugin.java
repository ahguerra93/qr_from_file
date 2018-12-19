package flutter.io.qrfromfile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import static android.content.ContentValues.TAG;

/** QrFromFilePlugin */
public class QrFromFilePlugin implements MethodCallHandler{
  /** Plugin registration. */
  private Registrar registrar;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    QrFromFilePlugin(Registrar registrar){      //constructor 1
        this.registrar = registrar;
    }

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "qr_from_file");
    channel.setMethodCallHandler(new QrFromFilePlugin(registrar));
  }

  private InputStream getImageStream(String path) {
      AssetManager assetManager = registrar.context().getAssets();
      String key = registrar.lookupKeyForAsset(path);

      InputStream stream = new InputStream() {
          @Override
          public int read() throws IOException {
              return 0;
          }
      };
      try {
      AssetFileDescriptor fd = assetManager.openFd(key);
      stream = assetManager.open(key);

      } catch (IOException e) {
          e.printStackTrace();
      }


      return stream;
  }

  private String scanImage(String path, Context c) {
        String response = "";
      Bitmap myBitmap = BitmapFactory.decodeStream(getImageStream(path));

      BarcodeDetector detector =
              new BarcodeDetector.Builder(c)
                      .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                      .build();
      if(!detector.isOperational()){
          Log.e(TAG, "scanImage: Could not set up the detector!");
          return null;
      }
      Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
      SparseArray<Barcode> barcodes = detector.detect(frame);
      Barcode thisCode = barcodes.valueAt(0);
      response = thisCode.rawValue;
      Log.e(TAG, "scanImage: "+response);
      return response;
  }
    private String scanImageFromFile(String path, Context c) {
        String response = "...";

//        final Uri.Builder builder = new Uri.Builder();
        Uri imageUri;
        imageUri =  Uri.parse(new File("file://"+path).toString());
        Log.e(TAG, "scanImage: Path: file://"+path);
        try {
            final InputStream imageStream = c.getContentResolver().openInputStream(imageUri);
            Bitmap myBitmap = BitmapFactory.decodeStream(imageStream);
//            Bitmap myBitmap = BitmapFactory.decodeStream(getImageStream("file://"+path));
            BarcodeDetector detector =
                    new BarcodeDetector.Builder(c)
                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                            .build();
            if(!detector.isOperational()){
                Log.e(TAG, "scanImage: Could not set up the detector!");
                return null;
            }
            Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            if (barcodes.size() > 0) {
                Barcode thisCode = barcodes.valueAt(0);
                response = thisCode.rawValue;
                Log.e(TAG, "scanImage: "+response);
            }
            else {
                response = "Could not decode QR image";
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return response;
    }


  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
      return;
    }
    if (call.method.equals("scanImageFromAssets")) {
        String arg1 = call.argument("path");
        Log.i(TAG, "onMethodCall: "+arg1);
        String response = scanImage(arg1, registrar.activity().getBaseContext());
        result.success(response);
        return;
    }
      if (call.method.equals("scanImageFromFile")) {
          String arg1 = call.argument("path");
          Log.i(TAG, "onMethodCall: "+arg1);
          String response = scanImageFromFile(arg1, registrar.activity().getBaseContext());
          result.success(response);
          return;
      }

    result.notImplemented();
  }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    Log.i(TAG, "onRequestPermissionsResult: Requested Granted");
//
//                } else {
//                    Log.i(TAG, "onRequestPermissionsResult: Requested Denied");
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
}
