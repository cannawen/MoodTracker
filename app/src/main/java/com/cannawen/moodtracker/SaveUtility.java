package com.cannawen.moodtracker;

import android.os.Environment;
import android.text.format.DateFormat;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class SaveUtility {

    static public boolean saveMood(String mood, String rating) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "MoodTracker");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filePath = folder.getPath() + File.separator + "data.csv";
            File file = new File(filePath);
            CSVWriter writer;
            FileWriter fileWriter;


            if (file.exists() && !file.isDirectory()) {
                fileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(fileWriter);
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            Date now = new Date();
            String[] data = {String.valueOf(now.getTime()), mood, rating, DateFormat.format("yyyy-MM-dd hh:mm:ss a", now).toString()};

            writer.writeNext(data);

            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
