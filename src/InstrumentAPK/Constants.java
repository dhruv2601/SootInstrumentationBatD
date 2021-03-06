package InstrumentAPK;

class Constants {

    // Variables for paths to Android and APKs
    final static String ANDROID_JAR = "/home/dhruv2601/Android/Sdk/platforms/";
    final static String APK_DIR = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/APK/";
    final static String OUTPUT_DIR = "/home/dhruv2601/IdeaProjects/Soot_Instrumenter/InstrumentAPK/";

//    public static final String APK_NAME = "com.MobileNumberTracker.GPS.Tracking.GPSnavigation.MAPS.Location.Tracker.apk";
//    public final static String APK_NAME = "locationshare.apk";
//    public final static String APK_NAME = "MapDisplay.apk";
//    public final static String APK_NAME = "app-debug.apk";
//    public final static String APK_NAME = "com.okcupid.okcupid.apk";
//    public final static String APK_NAME = "com.groupon.apk";
//    public final static String APK_NAME = "LocationTeseterDhruv.apk";
//    public final static String APK_NAME = "LocationTesterDhruv2.apk";
//    public final static String APK_NAME = "LocationTesterDhruv3.apk";
    public final static String APK_NAME = "LocationTesterDhruv4.apk";
//    public final static String APK_NAME = "com.spotify.apk";

//    public final static String APK_NAME = "com.tinder.apk";
//    public final static String APK_NAME = "com.whatsapp.apk";

//    public final static String APK_NAME = "com.ubercab.apk";
//    public final static String APK_NAME = "com.facebook.mlite.apk";


//    public final static String APK_NAME = "ch.publisheria.bring.apk";

//    public final static String APK_NAME = "com.apps.wallpaperhd.power.rangers.apk";


    final static String[] GPSMethodList = new String []{
        "requestLocationUpdates",
            "getLastKnownLocation",
            "getGpsStatus",
            "isLocationEnabled",
            "isProviderEnabled",
            "requestSingleUpdate",
            "getFromLocation",
            "getFromLocationName",
            "getLastLocation",
            "getLocationAvailability",
            "removeLocationUpdates",
            "requestLocationUpdates",
            "setMockLocation",
            "setMockMode",
            "flushLocations",
            "getLastLocation",
            "getLocationAvailability",
            "removeLocationUpdates",
            "requestLocationUpdates",
            "setMockMode",
            "addGeofences",
            "extractLocationAvailability",
            "hasLocationAvailability",
            "isLocationAvailable",
            "onLocationResult",
            "onLocationAvailability",
            "onLocationChanged",
            "getExpirationTime",
            "getFastestInterval",
            "setExpirationDuration",
            "setInterval",
            "setFastestInterval",
            "setMaxWaitTime",
            "setPriority",
            "getLastLocation",
            "getLocations",
            "getFusedLocationProviderClient",
            "getGeofencingClient",
            "getSettingsClient",
            "checkLocationSettings"
    };
}