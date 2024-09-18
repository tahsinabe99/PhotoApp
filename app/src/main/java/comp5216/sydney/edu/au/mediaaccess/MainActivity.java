package comp5216.sydney.edu.au.mediaaccess;

import static android.content.ContentValues.TAG;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;


public class MainActivity extends Activity {

    //request codes
    private static final int MY_PERMISSIONS_REQUEST_OPEN_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_VIDEO = 103;
    private static final int MY_PERMISSIONS_REQUEST_READ_VIDEOS = 104;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 105;
    public final String APP_TAG = "MobileComputingTutorial";
    public String photoFileName = "photo.jpg";
    public String videoFileName = "video.mp4";
    public String audioFileName = "audio.3gp";
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);


    private File file;
    private final MediaRecorder recorder = null;
    private final MediaPlayer player = null;
    //private MapsActivity mapsActivity;

    private TextView locationTextView;
    //private static final String TAG = MapsActivity.class.getSimpleName();
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation = new Location("");
    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week05);
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        //mapsActivity=new MapsActivity(this);
        getLocationPermission();
    }

    public void onLoadPhotoClick(View view) {

        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Bring up gallery to select a photo
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);

    }

    public void onLoadVideoClick(View view) {

        // Create intent for picking a video from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        // Bring up gallery to select a video
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_VIDEOS);
    }


    public void onTakePhotoClick(View v) {
        // Check permissions
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();
        } else {

            getDeviceLocation(() -> {
                if (mLastKnownLocation == null) {
                    Log.e(TAG, "Location is not available. Cannot proceed with photo.");
                    return;
                }

            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // set file name

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            photoFileName = "IMG_" + timeStamp + ".jpg";

            photoFileName = "IMG_" +"_"+timeStamp+"_CityName_"+ cityName+"_LAT_"+mLastKnownLocation.getLatitude()+"_LON_"+ mLastKnownLocation.getLongitude() + ".jpg";
            Log.d(TAG, "photoFileName");
            Log.d(TAG, photoFileName);

            // Create a photo file reference
            Uri file_uri = getFileUri(photoFileName, 0);

            // Add extended data to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, MY_PERMISSIONS_REQUEST_OPEN_CAMERA);
            }
            });
        }
    }

    public void onRecordVideoClick(View v) {
        // Check permissions
        if (!marshmallowPermission.checkPermissionForCamera()) {
            marshmallowPermission.requestPermissionForCamera();
        } else {

            getDeviceLocation(() -> {
                if (mLastKnownLocation == null) {
                    Log.e(TAG, "Location is not available. Cannot proceed with photo.");
                    return;
                }
            // create Intent to capture a video and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            // set file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
             videoFileName = "VIDEO_"+timeStamp+"_CityName_"+ cityName+"_LAT_"+mLastKnownLocation.getLatitude()+"_LON_"+ mLastKnownLocation.getLongitude() + ".mp4";

            // Create a video file reference
            Uri file_uri = getFileUri(videoFileName, 1);

            // add extended data to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);

            // Start the video record intent to capture video
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_RECORD_VIDEO);
            });
        }
    }

    // Returns the Uri for a photo/media stored on disk given the fileName
    public Uri getFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.getExternalStorageDirectory().toString()), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(MainActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    // Returns the Uri for a photo/media stored on disk given the fileName and type
    public Uri getFileUri(String fileName, int type) {
        Uri fileUri = null;
        try {
            String typestr = "images"; //default to images type
            if (type == 1) {
                typestr = "videos";
            } else if (type != 0) {
                typestr = "audios";
            }

            File mediaStorageDir = new File(getExternalMediaDirs()[0], APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }

            // Create the file target for the media based on filename
            file = new File(mediaStorageDir, fileName);

            // Wrap File object into a content provider, required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(this.getApplicationContext(), "comp5216.sydney.edu.au.mediaaccess.fileProvider", file);
            } else {
                fileUri = Uri.fromFile(mediaStorageDir);
            }
        } catch (Exception ex) {
            Log.e("getFileUri", ex.getStackTrace().toString());
        }
        return fileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final VideoView mVideoView = findViewById(R.id.videoview);
        ImageView ivPreview = findViewById(R.id.photopreview);

        mVideoView.setVisibility(View.GONE);
        ivPreview.setVisibility(View.GONE);

        if (requestCode == MY_PERMISSIONS_REQUEST_OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                scanFile(file.getAbsolutePath());
                ivPreview.setImageBitmap(takenImage);
                ivPreview.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                  Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_VIDEOS) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setVideoURI(videoUri);
                mVideoView.requestFocus();
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_VIDEO) {
            //if you are running on emulator remove the if statement
            if (resultCode == RESULT_OK) {
                Uri takenVideoUri = getFileUri(videoFileName, 1);
                scanFile(file.getAbsolutePath());
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setVideoURI(takenVideoUri);
                mVideoView.requestFocus();
                mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });
            }
        }
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    public void getDeviceLocation(Runnable action) {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Obtain the current location of the device
                            mLastKnownLocation = task.getResult();
                            double latitude=mLastKnownLocation.getLatitude();
                            double longitude=mLastKnownLocation.getLongitude();
                            Log.d(TAG, "Current Location: Lat = " + latitude + ", Lng = " + longitude);
                            cityName=getCityNameFromCoordinates(latitude, longitude);
                            Log.d(TAG, "City Name: " + cityName);

                            String currentOrDefault = "Current";

//                            // Show location details on the location TextView
//                            String msg = currentOrDefault + " Location: " +
//                                    Double.toString(mLastKnownLocation.getLatitude()) + ", " +
//                                    Double.toString(mLastKnownLocation.getLongitude());
//                            locationTextView.setText(msg);

                            // Add a marker for my current location on the ma
                        } else {
                            Log.d(TAG, "Failed to retrieve location. Using default location.");
                            double defaultLatitude=mDefaultLocation.latitude;
                            double defaultLongitude=mDefaultLocation.longitude;
                            cityName="Default: Sydney";
                            Log.d(TAG, "Default Location: Lat = " + defaultLatitude + ", Lng = " + defaultLongitude);
                        }

                        action.run();
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private String getCityNameFromCoordinates(double latitude, double longitude) {
        String cityName="Unknown City"; // Default in case of failure

        Geocoder geocoder=new Geocoder(this, Locale.getDefault());
        try {
            //Get address from latitude and longitude
            List<Address> addresses=geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses!=null && !addresses.isEmpty()) {
                //Get the first address and extract the city name
                Address address=addresses.get(0);
                cityName=address.getLocality();
            } else {
                Log.d(TAG, "No address found for the given coordinates.");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
        }
        return cityName;
    }



}
