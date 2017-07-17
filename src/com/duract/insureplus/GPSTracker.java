package com.duract.insureplus;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSTracker extends Service implements LocationListener {

	private final Context context;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	private double latitude;
	private double longitude;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
	private static final long MIN_TIME_BW_UPDATES = 2000;

	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.context = context;
	}

	protected Location getLocation() {
		try {
			if (canGetLocation()) {
				if (isGPSEnabled) {
					if (AppData.GPS_LOCATION == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

						if (locationManager != null) {
							AppData.GPS_LOCATION = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (AppData.GPS_LOCATION != null) {
								latitude = AppData.GPS_LOCATION.getLatitude();
								longitude = AppData.GPS_LOCATION.getLongitude();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return AppData.GPS_LOCATION;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public boolean canGetLocation() {
		locationManager = (LocationManager) context
				.getSystemService(LOCATION_SERVICE);
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		this.canGetLocation = isGPSEnabled;
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("GPS Settings.");
		builder.setMessage("GPS is not enabled. Do you want to setup GPS ?.");
		builder.setPositiveButton("Settings", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});

		builder.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		builder.show();
	}

	@Override
	public void onLocationChanged(Location arg) {
		// if (location != null) {
		// // Toast.makeText(
		// // context,
		// // "Your Location\nLat - " + arg.getLatitude() + "\nLng - "
		// // + arg.getLongitude(), Toast.LENGTH_LONG).show();
		// } else {
		// getLocation();
		// // Toast.makeText(context, "Getting Location Please wait...",
		// // Toast.LENGTH_LONG).show();
		// }

		if (AppData.GPS_LOCATION == null) {
			AppData.GPS_LOCATION = getLocation();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		getLocation();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getLongitude() {
		if (AppData.GPS_LOCATION != null) {
			longitude = AppData.GPS_LOCATION.getLongitude();
		}
		return longitude;
	}

	public double getLattitude() {
		if (AppData.GPS_LOCATION != null) {
			latitude = AppData.GPS_LOCATION.getLatitude();
		}
		return latitude;
	}

}
