package org.firstinspires.ftc.teamcode.org.rustlib.core;

import android.os.Environment;

import org.firstinspires.ftc.teamcode.org.rustlib.rustboard.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

public class Loader {
    public static final File defaultStorageDirectory = new File(Environment.getExternalStorageDirectory() + "\\Download");

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

    public static String loadString(String fileName, String fileExtension) {
        fileName = fileName.replace(" ", "_");
        return loadString(new File(defaultStorageDirectory, fileName + "." + fileExtension));
    }

    public static JsonObject loadJsonObject(String fileName, String fileExtension) {
        return getJsonObject(loadString(fileName, fileExtension));
    }

    public static String loadString(String fileName) {
        return loadString(fileName, ".txt");
    }

    public static JsonObject loadJsonObject(String fileName) {
        return getJsonObject(loadString(fileName));
    }

    public static JsonObject getJsonObject(String jsonString) {
        return Json.createReader(new StringReader(jsonString)).readObject();
    }

    public static void writeString(File output, String string) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(output.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(fileOut);
        writer.write(string);
        writer.close();
    }
}
