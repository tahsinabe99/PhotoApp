//package comp5216.sydney.edu.au.mediaaccess;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//
//
//import comp5216.sydney.edu.au.mediaaccess.databinding.ActivityMapsBinding;
//
//public class MapsActivity extends FragmentActivity {
//
//    private Activity activity;
//    private GoogleMap mMap;
//    private TextView locationTextView;
//    private static final String TAG = MapsActivity.class.getSimpleName();
//    // The entry point to the Fused Location Provider.
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
//    private Location mLastKnownLocation = new Location("");
//    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
//    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
//    private static final int DEFAULT_ZOOM = 15;
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private boolean mLocationPermissionGranted = false;
//    // Keys for storing activity state.
////    private static final String KEY_CAMERA_POSITION = "camera_position";
////    private static final String KEY_LOCATION = "location";
//
//
//    public MapsActivity(Activity activity){
//        this.activity=activity;
//        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(activity);
//    }
//
////    public void getLocationPermission() {
////        /*
////         * Request location permission, so that we can get the location of the
////         * device. The result of the permission request is handled by a callback,
////         * onRequestPermissionsResult.
////         */
////        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
////                android.Manifest.permission.ACCESS_FINE_LOCATION)
////                == PackageManager.PERMISSION_GRANTED) {
////            mLocationPermissionGranted = true;
////        } else {
////            ActivityCompat.requestPermissions(this,
////                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
////                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
////        }
////    }
//
//    /**
//     * Gets the current location of the device, and positions the map's camera.
//     */
//    @SuppressLint("MissingPermission")
//    public void getDeviceLocation() {
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Obtain the current location of the device
//                            mLastKnownLocation = task.getResult();
//                            String currentOrDefault = "Current";
//
//                            if (mLastKnownLocation != null) {
////                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
////                                        new LatLng(mLastKnownLocation.getLatitude(),
////                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//
//                            } else {
//                                Log.d(TAG, "Current location is null. Using defaults.");
//                                currentOrDefault = "Default";
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//
//                                // Set current location to the default location
//                                mLastKnownLocation = new Location("");
//                                mLastKnownLocation.setLatitude(mDefaultLocation.latitude);
//                                mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
//                            }
//
//                            // Show location details on the location TextView
//                            String msg = currentOrDefault + " Location: " +
//                                    Double.toString(mLastKnownLocation.getLatitude()) + ", " +
//                                    Double.toString(mLastKnownLocation.getLongitude());
//                            locationTextView.setText(msg);
//
//                            // Add a marker for my current location on the map
//                            MarkerOptions marker = new MarkerOptions().position(
//                                            new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
//                                    .title("I am here");
//                            mMap.addMarker(marker);
//                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }
//
//    public Location getLastKnownLocation(){
//        return mLastKnownLocation;
//    }
//
//}