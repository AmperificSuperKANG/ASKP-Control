package com.askp_control.Utils;

import java.io.IOException;

public class MiscellaneousValues {

	public static final String FILENAME_FSYNC_CONTROL = "/sys/devices/virtual/misc/fsynccontrol/fsync_enabled";
	public static final String FILENAME_DYNAMIC_FSYNC = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
	public static final String FILENAME_HEADPHONE_BOOST = "/sys/devices/virtual/misc/soundcontrol/volume_boost";
	public static final String FILENAME_SOUND_HIGH = "/sys/devices/virtual/misc/soundcontrol/highperf_enabled";
	public static final String FILENAME_BATTERY_EXTENDER = "/sys/devices/virtual/misc/batterylifeextender/charging_limit";
	public static final String FILENAME_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
	public static final String FILENAME_WIFI_HIGH = "/sys/module/bcmdhd/parameters/wifi_fast";

	public static int mFsyncControl() {
		if (Utils.existFile(FILENAME_FSYNC_CONTROL))
			try {
				return Integer.parseInt(Utils.readLine(FILENAME_FSYNC_CONTROL));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static int mDynamicFsync() {
		if (Utils.existFile(FILENAME_DYNAMIC_FSYNC))
			try {
				return Integer.parseInt(Utils.readLine(FILENAME_DYNAMIC_FSYNC));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static int mHeadphoneBoost() {
		if (Utils.existFile(FILENAME_HEADPHONE_BOOST))
			try {
				return Integer.parseInt(Utils
						.readLine(FILENAME_HEADPHONE_BOOST));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static int mSoundHigh() {
		if (Utils.existFile(FILENAME_SOUND_HIGH))
			try {
				return Integer.parseInt(Utils.readLine(FILENAME_SOUND_HIGH));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static int mBatterExtender() {
		if (Utils.existFile(FILENAME_BATTERY_EXTENDER))
			try {
				return Integer.parseInt(Utils
						.readLine(FILENAME_BATTERY_EXTENDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static int mFastCharge() {
		if (Utils.existFile(FILENAME_FAST_CHARGE))
			try {
				return Integer.parseInt(Utils.readLine(FILENAME_FAST_CHARGE));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return 0;
	}

	public static String mWifiHigh() {
		if (Utils.existFile(FILENAME_WIFI_HIGH))
			try {
				return Utils.readLine(FILENAME_WIFI_HIGH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return "0";
	}
}
