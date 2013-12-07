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

package com.askp_control.Fragments;

import java.util.ArrayList;
import java.util.List;

import com.askp_control.DownloadActivity;
import com.askp_control.R;
import com.askp_control.Utils.GetConnection;
import com.askp_control.Utils.LayoutStyle;
import com.askp_control.Utils.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadFragment extends Fragment {

	private static Context context;

	private static final String mLink = "https://raw.github.com/AmperificSuperKANG/ASKP-Support/master/";
	private static LinearLayout mLayout;
	private static ProgressBar mProgress;
	private static ListView mListView;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout, container, false);
		context = getActivity();
		mLayout = (LinearLayout) rootView.findViewById(R.id.layout);
		mProgress = new ProgressBar(getActivity());
		mListView = new ListView(getActivity());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		mListView.setLayoutParams(new LinearLayout.LayoutParams(width,
				height - 200));
		refresh();
		return rootView;
	}

	private static class DisplayString extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			return GetConnection.mHtmlstring;
		}

		@Override
		protected void onPostExecute(String result) {
			mProgress.setVisibility(View.GONE);

			if (GetConnection.mHtmlstring.contains("Contact Support")) {
				TextView mNoSupport = new TextView(context);
				LayoutStyle.setCenterText(mNoSupport,
						context.getString(R.string.nosupport), context);
				mNoSupport.setTextSize(20);
				mLayout.addView(mNoSupport);
			} else {
				mLayout.addView(mListView);
				String[] resultRaw = GetConnection.mHtmlstring.split(System
						.getProperty("line.separator"));

				List<String> valueNameList = new ArrayList<String>();
				for (int i = 0; i < resultRaw.length; i++) {
					valueNameList.add(resultRaw[i].split(": ")[0]);
				}

				final List<String> valueLinkList = new ArrayList<String>();
				for (int i = 0; i < resultRaw.length; i++) {
					valueLinkList.add(resultRaw[i].split(": ")[1]);
				}

				ListAdapter adapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, valueNameList);

				mListView.setAdapter(adapter);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						DownloadActivity.mDownloadlink = valueLinkList
								.get(arg2);
						DownloadActivity.mDownloadname = mListView.getAdapter()
								.getItem(arg2).toString().replace(" ", "")
								+ ".zip";
						confirmDownload(context);
					}
				});
			}
		}
	}

	public static void refresh() {
		mLayout.addView(mProgress);
		GetConnection.getconnection(mLink
				+ InformationFragment.mModel.replace(" ", "").toLowerCase());
		DisplayString task = new DisplayString();
		task.execute();
	}

	private static void confirmDownload(final Context context) {
		AlertDialog.Builder mConfirm = new AlertDialog.Builder(context);
		mConfirm.setTitle(context.getString(R.string.download))
				.setMessage(
						context.getString(R.string.doyouwant)
								+ " "
								+ context.getString(R.string.download)
										.toLowerCase() + " "
								+ DownloadActivity.mDownloadname + "?")
				.setNegativeButton(context.getString(android.R.string.no),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setPositiveButton(context.getString(android.R.string.yes),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (Utils.existFile(Environment
										.getExternalStorageDirectory()
										.toString()
										+ "/askp-kernel/"
										+ DownloadActivity.mDownloadname))
									deleteFile(context);
								else
									startDownload(context);
							}
						}).show();
	}

	private static void deleteFile(final Context context) {
		AlertDialog.Builder mDelete = new AlertDialog.Builder(context);
		mDelete.setTitle(context.getString(R.string.delete))
				.setMessage(context.getString(R.string.filealreadyexist))
				.setNegativeButton(context.getString(android.R.string.no),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setPositiveButton(context.getString(android.R.string.yes),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Utils.runCommand("rm -f "
										+ Environment
												.getExternalStorageDirectory()
												.toString() + "/askp-kernel/"
										+ DownloadActivity.mDownloadname);
								startDownload(context);
							}
						}).show();
	}

	private static void startDownload(Context context) {
		context.startActivity(new Intent(context, DownloadActivity.class));
	}
}
