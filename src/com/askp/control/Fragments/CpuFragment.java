/*
 * Copyright (C) 2013 AmperificSuperKANG Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.askp.control.Fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.askp.control.MainActivity;
import com.askp.control.R;
import com.askp.control.Utils.Control;
import com.askp.control.Utils.CpuValues;
import com.askp.control.Utils.LayoutStyle;
import com.askp.control.Utils.Utils;

public class CpuFragment extends Fragment implements OnSeekBarChangeListener,
		OnItemSelectedListener, OnCheckedChangeListener {

	private static LinearLayout mLayout;

	private static TextView mCurCpuFreq, mMaxCpuFreqText, mMinCpuFreqText,
			mMaxFreqScreenOffText, mMinFreqScreenOnText;

	private static CurCPUThread mCurCPUThread;

	private static SeekBar mMaxCpuFreqBar, mMinCpuFreqBar,
			mMaxScreenFreqOffBar, mMinFreqScreenOnBar;

	public static String[] mAvailableFreq;
	private static List<String> mAvailableFreqList = new ArrayList<String>();

	public static int mMinCpuFreqRaw, mMaxCpuFreqRaw;

	private static String mMaxFreqValue, mMinFreqValue, mMaxScreenOffValue,
			mMinScreenOnValue;

	private static SeekBar mMulticoreSavingBar;
	private static TextView mMulticoreSavingText;

	private static SeekBar mTempLimitBar;
	private static TextView mTempLimitText;

	private static Spinner mGovernorSpinner;
	private static String[] mAvailableGovernor;
	private static List<String> mAvailableGovernorList = new ArrayList<String>();
	public static String mCurGovernorRaw;

	private static CheckBox mCore, mIVA, mMPU;

	private static TextView[] mCoreVoltagesTexts;
	private static String[] mCoreVoltagesList;
	private static SeekBar[] mCoreVoltagesBars;
	private static List<String> mCoreVoltagesValuesList = new ArrayList<String>();

	private static TextView[] mIVAVoltagesTexts;
	private static String[] mIVAVoltagesList;
	private static SeekBar[] mIVAVoltagesBars;
	private static List<String> mIVAVoltagesValuesList = new ArrayList<String>();

	private static TextView[] mMPUVoltagesTexts;
	private static String[] mMPUVoltagesList;
	private static SeekBar[] mMPUVoltagesBars;
	private static List<String> mMPUVoltagesValuesList = new ArrayList<String>();

	private static TextView[] mRegulatorVoltagesTexts;
	private static String[] mRegulatorVoltagesList;
	private static SeekBar[] mRegulatorVoltagesBars;
	private static List<String> mRegulatorVoltagesValuesList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout, container, false);

		mLayout = (LinearLayout) rootView.findViewById(R.id.layout);

		// Current Freq Title
		TextView mCpuFreqTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mCpuFreqTitle, getString(R.string.cpufreq),
				getActivity());

		// Current Freq
		mCurCpuFreq = new TextView(getActivity());
		LayoutStyle.setCenterText(mCurCpuFreq, getString(R.string.unavailable),
				getActivity());
		mCurCpuFreq.setTextSize(40);
		mCurCpuFreq
				.setTextColor(getResources().getColor(android.R.color.white));

		if (Utils.existFile(CpuValues.FILENAME_CUR_CPU_FREQ)) {
			mLayout.addView(mCpuFreqTitle);
			mLayout.addView(mCurCpuFreq);
		}

		// Freq scaling Title
		TextView mFreqScalingTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mFreqScalingTitle,
				getString(R.string.cpuscaling), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MAX_FREQ)
				|| Utils.existFile(CpuValues.FILENAME_MIN_FREQ)
				|| Utils.existFile(CpuValues.FILENAME_MAX_SCREEN_OFF)
				|| Utils.existFile(CpuValues.FILENAME_MIN_SCREEN_ON)) {
			mLayout.addView(mFreqScalingTitle);
		}

		// Max Freq Title
		TextView mMaxCpuFreqTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mMaxCpuFreqTitle,
				getString(R.string.cpumaxfreq), getActivity());

		// Max Freq Summary
		TextView mMaxCpuFreqSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMaxCpuFreqSummary,
				getString(R.string.cpumaxfreq_summary), getActivity());

		// Max Freq SeekBar
		mAvailableFreq = CpuValues.mAvailableFreq().split(" ");

		mAvailableFreqList = Arrays.asList(mAvailableFreq);
		int mMax = mAvailableFreqList.indexOf(String.valueOf(mMaxCpuFreqRaw));
		int mMin = mAvailableFreqList.indexOf(String.valueOf(mMinCpuFreqRaw));

		mMaxCpuFreqBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mMaxCpuFreqBar, mAvailableFreq.length - 1, mMax);
		mMaxCpuFreqBar.setOnSeekBarChangeListener(this);
		mMaxFreqValue = String.valueOf(mMaxCpuFreqRaw);

		// Max Freq TextView
		mMaxCpuFreqText = new TextView(getActivity());
		LayoutStyle.setCenterText(mMaxCpuFreqText,
				String.valueOf(mMaxCpuFreqRaw / 1000) + " MHz", getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MAX_FREQ)) {
			mLayout.addView(mMaxCpuFreqTitle);
			mLayout.addView(mMaxCpuFreqSummary);
			mLayout.addView(mMaxCpuFreqBar);
			mLayout.addView(mMaxCpuFreqText);
		}

		// Min Freq Title
		TextView mMinCpuFreqTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mMinCpuFreqTitle,
				getString(R.string.cpuminfreq), getActivity());

		// Min Freq Summary
		TextView mMinCpuFreqSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMinCpuFreqSummary,
				getString(R.string.cpuminfreq_summary), getActivity());

		// Min Freq SeekBar
		mMinCpuFreqBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mMinCpuFreqBar, mAvailableFreq.length - 1, mMin);
		mMinCpuFreqBar.setOnSeekBarChangeListener(this);
		mMinFreqValue = String.valueOf(mMinCpuFreqRaw);

		// Min Freq TextView
		mMinCpuFreqText = new TextView(getActivity());
		LayoutStyle.setCenterText(mMinCpuFreqText,
				String.valueOf(mMinCpuFreqRaw / 1000) + " MHz", getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MIN_FREQ)) {
			mLayout.addView(mMinCpuFreqTitle);
			mLayout.addView(mMinCpuFreqSummary);
			mLayout.addView(mMinCpuFreqBar);
			mLayout.addView(mMinCpuFreqText);
		}

		// Max Freq Screen Off Title
		TextView mMaxFreqScreenOffTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mMaxFreqScreenOffTitle,
				getString(R.string.maxscreenoff), getActivity());

		// Max Freq Screen Off Summary
		TextView mMaxFreqScreenOffSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMaxFreqScreenOffSummary,
				getString(R.string.maxscreenoff_summary), getActivity());

		// Max Freq Screen Off SeekBar
		mMaxScreenOffValue = String.valueOf(CpuValues.mMaxScreenOffFreq());
		int mMaxScreenOff = mAvailableFreqList.indexOf(mMaxScreenOffValue);

		mMaxScreenFreqOffBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mMaxScreenFreqOffBar, mAvailableFreq.length - 1,
				mMaxScreenOff);
		mMaxScreenFreqOffBar.setOnSeekBarChangeListener(this);

		// Max Freq Screen Off TextView
		mMaxFreqScreenOffText = new TextView(getActivity());
		LayoutStyle.setCenterText(mMaxFreqScreenOffText,
				String.valueOf(CpuValues.mMaxScreenOffFreq() / 1000) + " MHz",
				getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MAX_SCREEN_OFF)) {
			mLayout.addView(mMaxFreqScreenOffTitle);
			mLayout.addView(mMaxFreqScreenOffSummary);
			mLayout.addView(mMaxScreenFreqOffBar);
			mLayout.addView(mMaxFreqScreenOffText);
		}

		// Min Freq Screen on Title
		TextView mMinFreqScreenOnTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mMinFreqScreenOnTitle,
				getString(R.string.minscreenon), getActivity());

		// Min Freq Screen On Summary
		TextView mMinFreqScreenOnSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMinFreqScreenOnSummary,
				getString(R.string.minscreenon_summary), getActivity());

		// Min Freq Screen On SeekBar
		mMinScreenOnValue = String.valueOf(CpuValues.mMinScreenOnFreq());
		int mMinScreenOn = mAvailableFreqList.indexOf(mMinScreenOnValue);

		mMinFreqScreenOnBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mMinFreqScreenOnBar, mAvailableFreq.length - 1,
				mMinScreenOn);
		mMinFreqScreenOnBar.setOnSeekBarChangeListener(this);

		// Min Freq Screen On TextView
		mMinFreqScreenOnText = new TextView(getActivity());
		LayoutStyle.setCenterText(mMinFreqScreenOnText,
				String.valueOf(CpuValues.mMinScreenOnFreq() / 1000) + " MHz",
				getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MIN_SCREEN_ON)) {
			mLayout.addView(mMinFreqScreenOnTitle);
			mLayout.addView(mMinFreqScreenOnSummary);
			mLayout.addView(mMinFreqScreenOnBar);
			mLayout.addView(mMinFreqScreenOnText);
		}

		// Multicore Saving Title
		TextView mMulticoreSavingTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mMulticoreSavingTitle,
				getString(R.string.multicoresaving), getActivity());

		// Multicore Saving Summary
		TextView mMulticoreSavingSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMulticoreSavingSummary,
				getString(R.string.multicoresaving_summary), getActivity());

		// Multicore Saving SeekBar
		mMulticoreSavingBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mMulticoreSavingBar, 2,
				CpuValues.mMulticoreSaving());
		mMulticoreSavingBar.setOnSeekBarChangeListener(this);

		// Multicore Saving Text
		mMulticoreSavingText = new TextView(getActivity());
		LayoutStyle.setCenterText(mMulticoreSavingText,
				String.valueOf(CpuValues.mMulticoreSaving()), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MULTICORE_SAVING)) {
			mLayout.addView(mMulticoreSavingTitle);
			mLayout.addView(mMulticoreSavingSummary);
			mLayout.addView(mMulticoreSavingBar);
			mLayout.addView(mMulticoreSavingText);
		}

		// Temp Limit Title
		TextView mTempLimitTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mTempLimitTitle,
				getString(R.string.templimit), getActivity());

		// Temp Limit Summary
		TextView mTempLimitSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mTempLimitSummary,
				getString(R.string.templimit_summary), getActivity());

		// Temp Limit SeekBar
		mTempLimitBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mTempLimitBar, 20,
				CpuValues.mTempLimit() / 1000 - 60);
		mTempLimitBar.setOnSeekBarChangeListener(this);

		// Temp Limit Text
		mTempLimitText = new TextView(getActivity());
		LayoutStyle.setCenterText(mTempLimitText,
				String.valueOf(CpuValues.mTempLimit() / 1000), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_TEMP_LIMIT)) {
			mLayout.addView(mTempLimitTitle);
			mLayout.addView(mTempLimitSummary);
			mLayout.addView(mTempLimitBar);
			mLayout.addView(mTempLimitText);
		}

		// Governor Title
		TextView mGovernorTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mGovernorTitle, getString(R.string.governor),
				getActivity());

		// Governor Summary
		TextView mGovernorSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mGovernorSummary,
				getString(R.string.governor_summary), getActivity());

		// Governor Spinner
		mAvailableGovernor = CpuValues.mAvailableGovernor().split(" ");
		mAvailableGovernorList = Arrays.asList(mAvailableGovernor);
		int mCurGovernor = mAvailableGovernorList.indexOf(mCurGovernorRaw);

		ArrayAdapter<String> adapterGovernor = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				mAvailableGovernor);
		adapterGovernor
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mGovernorSpinner = new Spinner(getActivity());
		LayoutStyle.setSpinner(mGovernorSpinner, adapterGovernor, mCurGovernor);
		mGovernorSpinner.setOnItemSelectedListener(this);

		if (Utils.existFile(CpuValues.FILENAME_AVAILABLE_GOVERNOR)
				&& Utils.existFile(CpuValues.FILENAME_CUR_GOVERNOR)) {
			mLayout.addView(mGovernorTitle);
			mLayout.addView(mGovernorSummary);
			mLayout.addView(mGovernorSpinner);
		}

		// Smartreflex Title
		TextView mSmartreflexTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mSmartreflexTitle,
				getString(R.string.smartreflex), getActivity());

		// Smartreflex Summary
		TextView mSmartreflexSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mSmartreflexSummary,
				getString(R.string.smartreflex_summary), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_CORE)
				|| Utils.existFile(CpuValues.FILENAME_IVA)
				|| Utils.existFile(CpuValues.FILENAME_MPU)) {
			mLayout.addView(mSmartreflexTitle);
			mLayout.addView(mSmartreflexSummary);
		}

		// Smartreflex Core
		boolean mCoreBoolean = false;
		if (CpuValues.mCore() == 1)
			mCoreBoolean = true;

		mCore = new CheckBox(getActivity());
		LayoutStyle.setCheckBox(mCore, getString(R.string.core), mCoreBoolean);
		mCore.setOnCheckedChangeListener(this);

		if (Utils.existFile(CpuValues.FILENAME_CORE))
			mLayout.addView(mCore);

		// Smartreflex IVA
		boolean mIVABoolean = false;
		if (CpuValues.mIVA() == 1)
			mIVABoolean = true;

		mIVA = new CheckBox(getActivity());
		LayoutStyle.setCheckBox(mIVA, getString(R.string.iva), mIVABoolean);
		mIVA.setOnCheckedChangeListener(this);

		if (Utils.existFile(CpuValues.FILENAME_IVA))
			mLayout.addView(mIVA);

		// Smartreflex mMPU
		boolean mMPUBoolean = false;
		if (CpuValues.mMPU() == 1)
			mMPUBoolean = true;

		mMPU = new CheckBox(getActivity());
		LayoutStyle.setCheckBox(mMPU, getString(R.string.mpu), mMPUBoolean);
		mMPU.setOnCheckedChangeListener(this);

		if (Utils.existFile(CpuValues.FILENAME_MPU))
			mLayout.addView(mMPU);

		// Core Voltages Title
		TextView mCoreVoltagesTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mCoreVoltagesTitle,
				getString(R.string.corevoltages), getActivity());

		// Core Voltages Summary
		TextView mCoreVoltagesSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mCoreVoltagesSummary,
				getString(R.string.warning), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_CORE_VOLTAGES)) {
			mLayout.addView(mCoreVoltagesTitle);
			mLayout.addView(mCoreVoltagesSummary);
		}

		mCoreVoltagesList = CpuValues.mCoreVoltagesFreq().split(" ");
		mCoreVoltagesBars = new SeekBar[mCoreVoltagesList.length];
		mCoreVoltagesTexts = new TextView[mCoreVoltagesList.length];
		for (int i = 0; i < mCoreVoltagesList.length; i++) {

			// Core Voltages Subtitle
			int mVoltageNumber = i + 1;
			TextView mCoreVoltagesSubtitle = new TextView(getActivity());
			LayoutStyle.setTextSubTitle(mCoreVoltagesSubtitle,
					getString(R.string.voltage) + " " + mVoltageNumber,
					getActivity());

			// Core Voltages SeekBar
			SeekBar mCoreVoltagesBar = new SeekBar(getActivity());
			LayoutStyle.setSeekBar(mCoreVoltagesBar, 102, Integer
					.parseInt(String.valueOf(Integer
							.parseInt(mCoreVoltagesList[i]) - 700)) / 6);
			mCoreVoltagesBar.setOnSeekBarChangeListener(this);
			mCoreVoltagesBars[i] = mCoreVoltagesBar;

			// Core Voltages TextView
			TextView mCoreVoltagesText = new TextView(getActivity());
			LayoutStyle.setCenterText(mCoreVoltagesText, mCoreVoltagesList[i]
					+ " mV", getActivity());
			mCoreVoltagesTexts[i] = mCoreVoltagesText;

			if (Utils.existFile(CpuValues.FILENAME_CORE_VOLTAGES)) {
				mLayout.addView(mCoreVoltagesSubtitle);
				mLayout.addView(mCoreVoltagesBar);
				mLayout.addView(mCoreVoltagesText);
			}
		}

		// IVA Voltages Title
		TextView mIVAVoltagesTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mIVAVoltagesTitle,
				getString(R.string.ivavoltages), getActivity());

		// IVA Voltages Summary
		TextView mIVAVoltagesSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mIVAVoltagesSummary,
				getString(R.string.warning), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_IVA_VOLTAGES)) {
			mLayout.addView(mIVAVoltagesTitle);
			mLayout.addView(mIVAVoltagesSummary);
		}

		mIVAVoltagesList = CpuValues.mIVAVoltagesFreq().split(" ");
		mIVAVoltagesBars = new SeekBar[mIVAVoltagesList.length];
		mIVAVoltagesTexts = new TextView[mIVAVoltagesList.length];
		for (int i = 0; i < mIVAVoltagesList.length; i++) {

			// IVA Voltages Subtitle
			int mVoltageNumber = i + 1;
			TextView mIVAVoltagesSubtitle = new TextView(getActivity());
			LayoutStyle.setTextSubTitle(mIVAVoltagesSubtitle,
					getString(R.string.voltage) + " " + mVoltageNumber,
					getActivity());

			// IVA Voltages SeekBar
			SeekBar mIVAVoltagesBar = new SeekBar(getActivity());
			LayoutStyle.setSeekBar(mIVAVoltagesBar, 102, Integer
					.parseInt(String.valueOf(Integer
							.parseInt(mIVAVoltagesList[i]) - 700)) / 8);
			mIVAVoltagesBar.setOnSeekBarChangeListener(this);
			mIVAVoltagesBars[i] = mIVAVoltagesBar;

			// IVA Voltages TextView
			TextView mIVAVoltagesText = new TextView(getActivity());
			LayoutStyle.setCenterText(mIVAVoltagesText, mIVAVoltagesList[i]
					+ " mV", getActivity());
			mIVAVoltagesTexts[i] = mIVAVoltagesText;

			if (Utils.existFile(CpuValues.FILENAME_IVA_VOLTAGES)) {
				mLayout.addView(mIVAVoltagesSubtitle);
				mLayout.addView(mIVAVoltagesBar);
				mLayout.addView(mIVAVoltagesText);
			}
		}

		// MPU Voltages Title
		TextView mMPUVoltagesTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mMPUVoltagesTitle,
				getString(R.string.mpuvoltages), getActivity());

		// MPU Voltages Summary
		TextView mMPUVoltagesSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mMPUVoltagesSummary,
				getString(R.string.warning), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_MPU_VOLTAGES)) {
			mLayout.addView(mMPUVoltagesTitle);
			mLayout.addView(mMPUVoltagesSummary);
		}

		mMPUVoltagesList = CpuValues.mMPUVoltagesFreq().split(";");
		mMPUVoltagesBars = new SeekBar[mMPUVoltagesList.length];
		mMPUVoltagesTexts = new TextView[mMPUVoltagesList.length];
		for (int i = 0; i < mMPUVoltagesList.length; i++) {

			// MPU Voltages Subtitle
			TextView mMPUVoltagesSubtitle = new TextView(getActivity());
			LayoutStyle.setTextSubTitle(mMPUVoltagesSubtitle,
					mMPUVoltagesList[i].split(" ")[0] + " MHz", getActivity());

			// MPU Voltages SeekBar
			SeekBar mMPUVoltagesBar = new SeekBar(getActivity());
			LayoutStyle
					.setSeekBar(mMPUVoltagesBar, 102, Integer.parseInt(String
							.valueOf(Integer.parseInt(mMPUVoltagesList[i]
									.split(" ")[1]) - 700)) / 9);
			mMPUVoltagesBar.setOnSeekBarChangeListener(this);
			mMPUVoltagesBars[i] = mMPUVoltagesBar;

			// MPU Voltages TextView
			TextView mMPUVoltagesText = new TextView(getActivity());
			LayoutStyle.setCenterText(mMPUVoltagesText,
					mMPUVoltagesList[i].split(" ")[1] + " mV", getActivity());
			mMPUVoltagesTexts[i] = mMPUVoltagesText;

			if (Utils.existFile(CpuValues.FILENAME_MPU_VOLTAGES)) {
				mLayout.addView(mMPUVoltagesSubtitle);
				mLayout.addView(mMPUVoltagesBar);
				mLayout.addView(mMPUVoltagesText);
			}
		}

		// Regulator Voltages Title
		TextView mRegulatorVoltagesTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mRegulatorVoltagesTitle,
				getString(R.string.regulatorvoltages), getActivity());

		// Regulator Voltages Summary
		TextView mRegulatorVoltagesSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mRegulatorVoltagesSummary,
				getString(R.string.warning), getActivity());

		if (Utils.existFile(CpuValues.FILENAME_REGULATOR_VOLTAGES)) {
			mLayout.addView(mRegulatorVoltagesTitle);
			mLayout.addView(mRegulatorVoltagesSummary);
		}

		mRegulatorVoltagesList = CpuValues.mRegulatorVoltagesFreq().split(";");
		mRegulatorVoltagesBars = new SeekBar[mRegulatorVoltagesList.length];
		mRegulatorVoltagesTexts = new TextView[mRegulatorVoltagesList.length];
		for (int i = 0; i < mRegulatorVoltagesList.length; i++) {

			// Regulator Voltages Subtitle
			TextView mRegulatorVoltagesSubtitle = new TextView(getActivity());
			LayoutStyle.setTextSubTitle(mRegulatorVoltagesSubtitle,
					mRegulatorVoltagesList[i].split(" ")[0], getActivity());

			// Regulator Voltages SeekBar
			SeekBar mRegulatorVoltagesBar = new SeekBar(getActivity());
			LayoutStyle
					.setSeekBar(mRegulatorVoltagesBar, 100, Integer
							.parseInt(String.valueOf(Integer
									.parseInt(mRegulatorVoltagesList[i]
											.split(" ")[1]) - 1500)) / 20);
			mRegulatorVoltagesBar.setOnSeekBarChangeListener(this);
			mRegulatorVoltagesBars[i] = mRegulatorVoltagesBar;

			// Regulator Voltages TextView
			TextView mRegulatorVoltagesText = new TextView(getActivity());
			LayoutStyle.setCenterText(mRegulatorVoltagesText,
					mRegulatorVoltagesList[i].split(" ")[1] + " mV",
					getActivity());
			mRegulatorVoltagesTexts[i] = mRegulatorVoltagesText;

			if (Utils.existFile(CpuValues.FILENAME_REGULATOR_VOLTAGES)) {
				mLayout.addView(mRegulatorVoltagesSubtitle);
				mLayout.addView(mRegulatorVoltagesBar);
				mLayout.addView(mRegulatorVoltagesText);
			}
		}

		return rootView;
	}

	@Override
	public void onResume() {
		mCurCPUThread = new CurCPUThread();
		mCurCPUThread.start();
		super.onResume();
	}

	protected class CurCPUThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					mCurCPUHandler.sendMessage(mCurCPUHandler.obtainMessage(0,
							CpuValues.mCurCpuFreq()));
					sleep(500);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	protected Handler mCurCPUHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (!String.valueOf(msg.obj).equals(0))
				mCurCpuFreq
						.setText(String.valueOf(CpuValues.mCurCpuFreq() / 1000)
								+ " Mhz");
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mCoreVoltagesValuesList.clear();
		for (int i = 0; i < mCoreVoltagesList.length; i++) {
			if (seekBar.equals(mCoreVoltagesBars[i]))
				mCoreVoltagesTexts[i].setText(String
						.valueOf(progress * 6 + 700) + " mV");
			mCoreVoltagesValuesList.add(mCoreVoltagesTexts[i].getText()
					.toString());
		}
		mIVAVoltagesValuesList.clear();
		for (int i = 0; i < mIVAVoltagesList.length; i++) {
			if (seekBar.equals(mIVAVoltagesBars[i]))
				mIVAVoltagesTexts[i].setText(String.valueOf(progress * 8 + 700)
						+ " mV");
			mIVAVoltagesValuesList.add(mIVAVoltagesTexts[i].getText()
					.toString());
		}
		mMPUVoltagesValuesList.clear();
		for (int i = 0; i < mMPUVoltagesList.length; i++) {
			if (seekBar.equals(mMPUVoltagesBars[i]))
				mMPUVoltagesTexts[i].setText(String.valueOf(progress * 9 + 700)
						+ " mV");
			mMPUVoltagesValuesList.add(mMPUVoltagesTexts[i].getText()
					.toString().replace(" mV", ""));
		}
		mRegulatorVoltagesValuesList.clear();
		for (int i = 0; i < mRegulatorVoltagesList.length; i++) {
			if (seekBar.equals(mRegulatorVoltagesBars[i]))
				mRegulatorVoltagesTexts[i].setText(String
						.valueOf(progress * 20 + 1500) + " mV");
			mRegulatorVoltagesValuesList.add(mRegulatorVoltagesTexts[i]
					.getText().toString().replace(" mV", ""));
		}
		if (seekBar.equals(mMaxCpuFreqBar)) {
			mMaxCpuFreqText.setText(String.valueOf(Integer
					.parseInt(mAvailableFreq[progress]) / 1000) + " MHz");
			mMaxFreqValue = mAvailableFreq[progress];
			if (Integer.parseInt(mMaxFreqValue) < Integer
					.parseInt(mMinFreqValue)) {
				mMaxCpuFreqBar.setProgress(progress);
				mMinCpuFreqBar.setProgress(progress);
			}
		} else if (seekBar.equals(mMinCpuFreqBar)) {
			mMinCpuFreqText.setText(String.valueOf(Integer
					.parseInt(mAvailableFreq[progress]) / 1000) + " MHz");
			mMinFreqValue = mAvailableFreq[progress];
			if (Integer.parseInt(mMaxFreqValue) < Integer
					.parseInt(mMinFreqValue)) {
				mMaxCpuFreqBar.setProgress(progress);
				mMinCpuFreqBar.setProgress(progress);
			}
		} else if (seekBar.equals(mMaxScreenFreqOffBar)) {
			mMaxFreqScreenOffText.setText(String.valueOf(Integer
					.parseInt(mAvailableFreq[progress]) / 1000) + " MHz");
			mMaxScreenOffValue = mAvailableFreq[progress];
		} else if (seekBar.equals(mMinFreqScreenOnBar)) {
			mMinFreqScreenOnText.setText(String.valueOf(Integer
					.parseInt(mAvailableFreq[progress]) / 1000) + " MHz");
			mMinScreenOnValue = mAvailableFreq[progress];
		} else if (seekBar.equals(mMulticoreSavingBar)) {
			mMulticoreSavingText.setText(String.valueOf(progress));
		} else if (seekBar.equals(mTempLimitBar)) {
			mTempLimitText.setText(String.valueOf(progress + 60));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		MainActivity.enableButtons();
		MainActivity.mCpuAction = true;
		for (int i = 0; i < mCoreVoltagesList.length; i++) {
			if (seekBar.equals(mCoreVoltagesBars[i])) {
				StringBuilder mCoreVoltagesValue = new StringBuilder();
				for (String s : mCoreVoltagesValuesList) {
					mCoreVoltagesValue.append(s);
					mCoreVoltagesValue.append("\t");
				}
				Control.CORE_VOLTAGE = mCoreVoltagesValue.toString();
			}
		}
		for (int i = 0; i < mIVAVoltagesList.length; i++) {
			if (seekBar.equals(mIVAVoltagesBars[i])) {
				StringBuilder mIVAVoltagesValue = new StringBuilder();
				for (String s : mIVAVoltagesValuesList) {
					mIVAVoltagesValue.append(s);
					mIVAVoltagesValue.append("\t");
				}
				Control.IVA_VOLTAGE = mIVAVoltagesValue.toString();
			}
		}
		for (int i = 0; i < mMPUVoltagesList.length; i++) {
			if (seekBar.equals(mMPUVoltagesBars[i])) {
				StringBuilder mMPUVoltagesValue = new StringBuilder();
				for (String s : mMPUVoltagesValuesList) {
					mMPUVoltagesValue.append(s);
					mMPUVoltagesValue.append("\t");
				}
				Control.MPU_VOLTAGE = mMPUVoltagesValue.toString();
			}
		}
		for (int i = 0; i < mRegulatorVoltagesList.length; i++) {
			if (seekBar.equals(mRegulatorVoltagesBars[i])) {
				StringBuilder mRegulatorVoltagesValue = new StringBuilder();
				for (String s : mRegulatorVoltagesValuesList) {
					mRegulatorVoltagesValue.append(s);
					mRegulatorVoltagesValue.append("\t");
				}
				Control.REGULATOR_VOLTAGE = mRegulatorVoltagesValue.toString();
			}
		}
		if (seekBar.equals(mMaxCpuFreqBar) || seekBar.equals(mMinCpuFreqBar)) {
			Control.MAX_CPU_FREQ = mMaxFreqValue;
			Control.MIN_CPU_FREQ = mMinFreqValue;
		} else if (seekBar.equals(mMaxScreenFreqOffBar)) {
			Control.MAX_SCREEN_OFF = mMaxScreenOffValue;
		} else if (seekBar.equals(mMinFreqScreenOnBar)) {
			Control.MIN_SCREEN_ON = mMinScreenOnValue;
		} else if (seekBar.equals(mMulticoreSavingBar)) {
			Control.MULTICORE_SAVING = mMulticoreSavingText.getText()
					.toString();
		} else if (seekBar.equals(mTempLimitBar)) {
			Control.TEMP_LIMIT = mTempLimitText.getText().toString() + "000";
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg0.equals(mGovernorSpinner)) {
			int mCurGovernor = mAvailableGovernorList.indexOf(mCurGovernorRaw);
			if (arg2 != mCurGovernor) {
				MainActivity.enableButtons();
				MainActivity.mCpuAction = true;
				Control.GOVERNOR = mAvailableGovernor[arg2];
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MainActivity.enableButtons();
		MainActivity.mCpuAction = true;
		if (buttonView.equals(mCore)) {
			Control.CORE = isChecked ? "1" : "0";
		} else if (buttonView.equals(mIVA)) {
			Control.IVA = isChecked ? "1" : "0";
		} else if (buttonView.equals(mMPU)) {
			Control.MPU = isChecked ? "1" : "0";
		}
	}
}