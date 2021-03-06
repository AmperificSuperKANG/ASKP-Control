/*
 * Copyright (C) 2014 AmperificSuperKANG Project
 *
 * This file is part of ASKP Control.
 *
 * ASKP Control is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASKP Control is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ASKP Control.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.askp.control.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

public class Utils {

	private static final String FILENAME_PROC_VERSION = "/proc/version";
	private static final String FILENAME_PROC_MEMINFO = "/proc/meminfo";

	public static String replaceLastChar(String s, int length) {
		int slength = s.length();
		if (slength < length)
			return "0";
		return s.substring(0, slength - length) + "";
	}

	public static void mkdir(String file) {
		if (!new File(file).exists())
			new File(file).mkdirs();
	}

	public static String listSplit(List<String> value) {
		StringBuilder mValue = new StringBuilder();
		for (String s : value) {
			mValue.append(s);
			mValue.append("\t");
		}
		return mValue.toString();
	}

	public static int getInt(String name, int defaults, Context context) {
		return context.getSharedPreferences("prefs", 0).getInt(name, defaults);
	}

	public static void saveInt(String name, int value, Context context) {
		context.getSharedPreferences("prefs", 0).edit().putInt(name, value)
				.commit();
	}

	public static String getString(String name, Context context) {
		return context.getSharedPreferences("prefs", 0).getString(name,
				"nothing");
	}

	public static boolean getBoolean(String name, boolean defaults,
			Context context) {
		return context.getSharedPreferences("prefs", 0).getBoolean(name,
				defaults);
	}

	public static void saveBoolean(String name, boolean value, Context context) {
		context.getSharedPreferences("prefs", 0).edit().putBoolean(name, value)
				.commit();
	}

	public static boolean existFile(String file) {
		return new File(file).exists();
	}

	public static void saveString(String name, String value, Context context) {
		context.getSharedPreferences("prefs", 0).edit().putString(name, value)
				.commit();
	}

	public static void toast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void runCommand(String run) {
		try {
			RootTools.getShell(true).add(new CommandCapture(0, run))
					.commandCompleted(0, 0);
		} catch (IOException e) {
		} catch (TimeoutException e) {
		} catch (RootDeniedException e) {
		}
	}

	public static String readBlock(String filename) throws IOException {
		BufferedReader buffreader = new BufferedReader(
				new FileReader(filename), 256);
		String line;
		StringBuilder text = new StringBuilder();
		while ((line = buffreader.readLine()) != null) {
			text.append(line);
			text.append("\n");
		}
		buffreader.close();
		return text.toString();
	}

	public static String readLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename),
				256);
		try {
			return reader.readLine();
		} finally {
			reader.close();
		}
	}

	public static int getMemInfo() {
		int result = 0;
		try {
			String firstLine = readLine(FILENAME_PROC_MEMINFO);
			if (firstLine != null) {
				String parts[] = firstLine.split("\\s+");
				if (parts.length == 3)
					result = Integer.parseInt(parts[1]) / 1024;
			}
		} catch (IOException e) {
		}
		return result;
	}

	public static String getFormattedKernelVersion() {
		try {
			return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
		} catch (IOException e) {
			return "Unavailable";
		}
	}

	private static String formatKernelVersion(String rawKernelVersion) {
		final String PROC_VERSION_REGEX = "Linux version (\\S+) "
				+ "\\((\\S+?)\\) " + "(?:\\(gcc.+? \\)) " + "(#\\d+) "
				+ "(?:.*?)?" + "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)";

		Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(
				rawKernelVersion);

		return !m.matches() || m.groupCount() < 4 ? "Unavailable" : m.group(1)
				+ "\n" + m.group(2) + " " + m.group(3) + "\n" + m.group(4);
	}
}
