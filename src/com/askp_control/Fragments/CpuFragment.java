package com.askp_control.Fragments;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.askp_control.MainActivity;
import com.askp_control.R;
import com.askp_control.Utils.Control;
import com.askp_control.Utils.CpuValues;
import com.askp_control.Utils.Utils;

public class CpuFragment extends Fragment implements OnSeekBarChangeListener,
		OnItemSelectedListener {

	private static TextView mCurCpuFreq, mMaxCpuFreq, mMinCpuFreq;

	private static CurCPUThread mCurCPUThread;

	private static SeekBar mMaxCpuFreqBar, mMinCpuFreqBar;

	private static String[] mAvailableFreq;
	private static List<String> mAvailableFreqList;

	public static int mMinCpuFreqRaw;
	public static int mMaxCpuFreqRaw;

	private static String mMaxFreqValue;
	private static String mMinFreqValue;

	private static Spinner mGovernor;
	private static String[] mAvailableGovernor;
	private static List<String> mAvailableGovernorList;
	public static String mCurGovernorRaw;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cpu, container, false);

		mCurCpuFreq = (TextView) rootView.findViewById(R.id.curcpufreq);

		mMaxCpuFreq = (TextView) rootView.findViewById(R.id.curmaxcpufreq);
		mMaxCpuFreq.setText(getActivity().getString(R.string.curmaxfreq) + ": "
				+ String.valueOf(mMaxCpuFreqRaw / 1000) + " MHz");
		mMaxFreqValue = String.valueOf(mMaxCpuFreqRaw);

		mMinCpuFreq = (TextView) rootView.findViewById(R.id.curmincpufreq);
		mMinCpuFreq.setText(getActivity().getString(R.string.curminfreq) + ": "
				+ String.valueOf(mMinCpuFreqRaw / 1000) + " MHz");
		mMinFreqValue = String.valueOf(mMinCpuFreqRaw);

		mAvailableFreq = CpuValues.mAvailableFreq().split(" ");

		mAvailableFreqList = Arrays.asList(mAvailableFreq);
		int mMax = mAvailableFreqList.indexOf(String.valueOf(mMaxCpuFreqRaw));
		int mMin = mAvailableFreqList.indexOf(String.valueOf(mMinCpuFreqRaw));

		mMaxCpuFreqBar = (SeekBar) rootView.findViewById(R.id.maxcpuseekbar);
		mMaxCpuFreqBar.setMax(mAvailableFreq.length - 1);
		mMaxCpuFreqBar.setProgress(mMax);
		mMaxCpuFreqBar.setOnSeekBarChangeListener(this);

		mMinCpuFreqBar = (SeekBar) rootView.findViewById(R.id.mincpuseekbar);
		mMinCpuFreqBar.setMax(mAvailableFreq.length - 1);
		mMinCpuFreqBar.setProgress(mMin);
		mMinCpuFreqBar.setOnSeekBarChangeListener(this);

		mAvailableGovernor = CpuValues.mAvailableGovernor().split(" ");
		mAvailableGovernorList = Arrays.asList(mAvailableGovernor);
		int mCurGovernor = mAvailableGovernorList.indexOf(mCurGovernorRaw);

		mGovernor = (Spinner) rootView.findViewById(R.id.governor_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, mAvailableGovernor);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mGovernor.setAdapter(adapter);
		mGovernor.setSelection(mCurGovernor);
		mGovernor.setOnItemSelectedListener(this);

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
					sleep(1000);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	protected Handler mCurCPUHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (!String.valueOf(msg.obj).equals(0)) {
				mCurCpuFreq.setText(CpuValues.mCurCpuFreq() + " Mhz");
			}
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(mMaxCpuFreqBar)) {
			mMaxCpuFreq
					.setText(getActivity().getString(R.string.curmaxfreq)
							+ ": "
							+ String.valueOf(Integer
									.parseInt(mAvailableFreq[progress]) / 1000)
							+ " MHz");
			mMaxFreqValue = mAvailableFreq[progress];
		} else if (seekBar.equals(mMinCpuFreqBar)) {
			mMinCpuFreq
					.setText(getActivity().getString(R.string.curminfreq)
							+ ": "
							+ String.valueOf(Integer
									.parseInt(mAvailableFreq[progress]) / 1000)
							+ " MHz");
			mMinFreqValue = mAvailableFreq[progress];
		}
		if (Integer.parseInt(mMaxFreqValue) < Integer.parseInt(mMinFreqValue)) {
			mMaxCpuFreqBar.setProgress(progress);
			mMinCpuFreqBar.setProgress(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		MainActivity.mChange = true;
		if (seekBar.equals(mMaxCpuFreqBar)) {
			Control.MAX_CPU_FREQ = mMaxFreqValue;
		} else if (seekBar.equals(mMinCpuFreqBar)) {
			Control.MIN_CPU_FREQ = mMinFreqValue;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		MainActivity.mChange = true;
		if (arg0.equals(mGovernor)) {
			int mCurGovernor = mAvailableGovernorList.indexOf(mCurGovernorRaw);
			if (arg2 != mCurGovernor)
				Control.GOVERNOR = mAvailableGovernor[arg2];
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}