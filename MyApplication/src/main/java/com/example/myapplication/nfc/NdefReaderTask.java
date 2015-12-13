package com.example.myapplication.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Background task for reading the data. Do not block the UI thread while reading.
 *
 * @author Ralf Wondratschek
 *
 */

