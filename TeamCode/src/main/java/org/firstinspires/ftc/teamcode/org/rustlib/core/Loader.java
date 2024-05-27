package org.firstinspires.ftc.teamcode.org.rustlib.core;

import android.os.Environment;

import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

public class Loader {
    public static final File defaultStorageDirectory = new File(Environment.getExternalStorageDirectory() + "/Download");

    public static String loadString(File filePath) {
        StringBuilder data = new StringBuilder();
        try {
            FileInputStream input = new FileInputStream(filePath);

            int character;
            while ((character = input.read()) != -1) {
                data.append((char) character);
            }

            return data.toString();
        } catch (IOException e) {
            Server.log(e.toString());
            e.printStackTrace();
        }
        return "";
    }

    public static JsonObject loadJsonObject(File filePath) {
        return getJsonObject(loadString(filePath));
    }

    public static String loadString(String parentDir, String child, String fileExtension) {
        return loadString(new File(parentDir + "\\" + child + "." + fileExtension));
    }

    public static JsonObject loadJsonObject(String parentDir, String child, String fileExtension) {
        return getJsonObject(loadString(parentDir, child, fileExtension));
    }

    public static String loadString(String filePath, String fileExtension) {
        filePath = filePath.replace(" ", "_");
        return loadString(new File(filePath + "." + fileExtension));
    }

    public static JsonObject loadJsonObject(String filePath, String fileExtension) {
        return getJsonObject(loadString(filePath, fileExtension));
    }

    public static String loadString(String filePath) {
        return loadString(filePath, ".txt");
    }

    public static JsonObject loadJsonObject(String filePath) {
        return getJsonObject(loadString(filePath));
    }

    public static JsonObject getJsonObject(String jsonString) {
        return Json.createReader(new StringReader(jsonString)).readObject();
    }
}
