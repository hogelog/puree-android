package com.cookpad.puree.storage;

import android.test.AndroidTestCase;

import org.json.JSONObject;

public class RecordsTest extends AndroidTestCase {

    public void testGetIdsAsString() {
        {
            Records records = new Records();
            assertEquals("", records.getIdsAsString());
        }
        {
            Records records = new Records();
            records.add(new Record(0, "logcat", new JSONObject()));
            assertEquals("0", records.getIdsAsString());
        }
        {
            Records records = new Records();
            for (int i = 0; i < 3; i++) {
                records.add(new Record(i, "logcat", new JSONObject()));
            }
            assertEquals("0,1,2", records.getIdsAsString());
        }
    }

    public void testGetSerializedLogs() {
        {
            Records records = new Records();
            assertEquals(0, records.getSerializedLogs().length());
        }
        {
            Records records = new Records();
            for (int i = 0; i < 3; i++) {
                records.add(new Record(i, "logcat", new JSONObject()));
            }
            assertEquals(3, records.getSerializedLogs().length());
        }
    }
}
