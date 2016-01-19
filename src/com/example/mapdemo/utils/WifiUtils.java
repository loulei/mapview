package com.example.mapdemo.utils;

public class WifiUtils {

	public static int FrequencyToChannel(final int frequency) {
		int channel = 0;
		switch (frequency) {
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		}
		return channel;
	}
	
	public static double calculateSignal(final double level) {
		final double maxSignal = -20.0, disassociationSignal = -95.0;
		final double percent = 100.0 - 80.0 * (maxSignal - level)
				/ (maxSignal - disassociationSignal);
		return percent;
	}
}
