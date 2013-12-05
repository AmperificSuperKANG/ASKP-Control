package com.askp_control.Fragments;

import com.askp_control.MainActivity;
import com.askp_control.R;
import com.askp_control.Utils.Control;
import com.askp_control.Utils.GpuDisplayValues;
import com.askp_control.Utils.LayoutStyle;
import com.askp_control.Utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GpuDisplayFragment extends Fragment implements
		OnSeekBarChangeListener {

	private static LinearLayout mLayout;

	private static SeekBar mGpuMaxFreqBar;
	private static TextView mGpuMaxFreqText;
	private static String mGpuValue;
	private static int mGpuValueRaw;

	private static SeekBar mTrinityContrastBar;
	private static TextView mTrinityContrastText;

	private static SeekBar mGammaControlBar;
	private static TextView mGammaControlText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout, container, false);

		mLayout = (LinearLayout) rootView.findViewById(R.id.layout);

		// Gpu Scaling Title
		TextView mGpuScalingTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mGpuScalingTitle,
				getString(R.string.gpuscaling), getActivity());

		// Gpu Max Freq SubTitle
		TextView mGpuMaxFreqSubTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mGpuMaxFreqSubTitle,
				getString(R.string.gpumaxfreq), getActivity());

		// Gpu Max Freq Summary
		TextView mGpuMaxFreqSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mGpuMaxFreqSummary,
				getString(R.string.gpumaxfreq_summary), getActivity());

		// Gpu Max Freq SeekBar
		mGpuMaxFreqBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mGpuMaxFreqBar, 2,
				GpuDisplayValues.mVariableGpu());
		mGpuMaxFreqBar.setOnSeekBarChangeListener(this);

		// Gpu Max Freq Text
		mGpuValue = "";
		switch (GpuDisplayValues.mVariableGpu()) {
		case 0:
			mGpuValue = "307";
			break;
		case 1:
			mGpuValue = "384";
			break;
		case 2:
			mGpuValue = "512";
			break;
		}
		mGpuMaxFreqText = new TextView(getActivity());
		LayoutStyle.setCenterText(mGpuMaxFreqText, mGpuValue + " MHz",
				getActivity());

		if (Utils.existFile(GpuDisplayValues.FILENAME_VARIABLE_GPU)) {
			mLayout.addView(mGpuScalingTitle);
			mLayout.addView(mGpuMaxFreqSubTitle);
			mLayout.addView(mGpuMaxFreqSummary);
			mLayout.addView(mGpuMaxFreqBar);
			mLayout.addView(mGpuMaxFreqText);
		}

		ImageView mColor = new ImageView(getActivity());
		mColor.setImageResource(R.drawable.ic_color);

		if (Utils.existFile(GpuDisplayValues.FILENAME_TRINITY_CONTRAST)
				|| Utils.existFile(GpuDisplayValues.FILENAME_GAMMA_CONTROL)) {
			mLayout.addView(mColor);
		}

		// Trinity Contrast Title
		TextView mTrinityContrastTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mTrinityContrastTitle,
				getString(R.string.trinitycontrast).replace("ss", "'s"),
				getActivity());

		// Trinity Contrast SubTitle
		TextView mTrinityContrastSubTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mTrinityContrastSubTitle,
				getString(R.string.contrast), getActivity());

		// Trinity Constrast Summary
		TextView mTrinityContrastSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mTrinityContrastSummary,
				getString(R.string.contrast_summary), getActivity());

		// Trinity Constrast SeekBar
		mTrinityContrastBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mTrinityContrastBar, 41,
				GpuDisplayValues.mTrinityContrast() + 25);
		mTrinityContrastBar.setOnSeekBarChangeListener(this);

		// Trinity Constrast Text
		mTrinityContrastText = new TextView(getActivity());
		LayoutStyle.setCenterText(mTrinityContrastText,
				String.valueOf(GpuDisplayValues.mTrinityContrast()),
				getActivity());

		if (Utils.existFile(GpuDisplayValues.FILENAME_TRINITY_CONTRAST)) {
			mLayout.addView(mTrinityContrastTitle);
			mLayout.addView(mTrinityContrastSubTitle);
			mLayout.addView(mTrinityContrastSummary);
			mLayout.addView(mTrinityContrastBar);
			mLayout.addView(mTrinityContrastText);
		}

		ImageView mGray = new ImageView(getActivity());
		mGray.setImageResource(R.drawable.ic_gray);

		if (Utils.existFile(GpuDisplayValues.FILENAME_GAMMA_CONTROL)) {
			mLayout.addView(mGray);
		}

		// Gamma Control Title
		TextView mGammaControlTitle = new TextView(getActivity());
		LayoutStyle.setTextTitle(mGammaControlTitle,
				getString(R.string.gammacontrol), getActivity());

		// Gamma Control SubTitle
		TextView mGammaControlSubTitle = new TextView(getActivity());
		LayoutStyle.setTextSubTitle(mGammaControlSubTitle,
				getString(R.string.setgamma), getActivity());

		// Gamma Control Summary
		TextView mGammaControlSummary = new TextView(getActivity());
		LayoutStyle.setTextSummary(mGammaControlSummary,
				getString(R.string.gammacontrol_summary), getActivity());

		// Gamma Control SeekBar
		mGammaControlBar = new SeekBar(getActivity());
		LayoutStyle.setSeekBar(mGammaControlBar, 10,
				GpuDisplayValues.mGammaControl());
		mGammaControlBar.setOnSeekBarChangeListener(this);

		// Gamma Control Text
		mGammaControlText = new TextView(getActivity());
		LayoutStyle
				.setCenterText(mGammaControlText,
						String.valueOf(GpuDisplayValues.mGammaControl()),
						getActivity());

		if (Utils.existFile(GpuDisplayValues.FILENAME_GAMMA_CONTROL)) {
			mLayout.addView(mGammaControlTitle);
			mLayout.addView(mGammaControlSubTitle);
			mLayout.addView(mGammaControlSummary);
			mLayout.addView(mGammaControlBar);
			mLayout.addView(mGammaControlText);
		}

		return rootView;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(mGpuMaxFreqBar)) {
			mGpuValueRaw = progress;
			switch (progress) {
			case 0:
				mGpuValue = "307";
				break;
			case 1:
				mGpuValue = "384";
				break;
			case 2:
				mGpuValue = "512";
				break;
			}
			mGpuMaxFreqText.setText(mGpuValue + " MHz");
		} else if (seekBar.equals(mTrinityContrastBar)) {
			mTrinityContrastText.setText(String.valueOf(progress - 25));
		} else if (seekBar.equals(mGammaControlBar)) {
			mGammaControlText.setText(String.valueOf(progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		MainActivity.mChange = true;
		MainActivity.mGpuDisplayAction = true;
		if (seekBar.equals(mGpuMaxFreqBar)) {
			Control.GPU_VARIABLE = String.valueOf(mGpuValueRaw);
		} else if (seekBar.equals(mTrinityContrastBar)) {
			Control.TRINITY_CONTRAST = mTrinityContrastText.getText()
					.toString();
		} else if (seekBar.equals(mGammaControlBar)) {
			Control.GAMMA_CONTROL = mGammaControlText.getText().toString();
		}
	}
}
