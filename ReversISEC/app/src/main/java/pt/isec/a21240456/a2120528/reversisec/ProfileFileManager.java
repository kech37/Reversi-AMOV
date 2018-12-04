package pt.isec.a21240456.a2120528.reversisec;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProfileFileManager{
    private static final String FILE_NAME = "ReversISECProfileDetails";

    private String profileName = null, profilePicturePath = null;
    private Context context;

    public ProfileFileManager(Context context) {
        this.context = context;

        String dataString = readFromFile();

        if(dataString != null){
            String[] data = dataString.split(";");
            profileName = data[0];
            profilePicturePath = data[1];
        }
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void writeToFile(String dataName, Bitmap dataPicturePath){
        try {
            File file = new File(Environment.getExternalStorageDirectory().toString(), "profilePicture.png");
            OutputStream outputStream;

            outputStream = new FileOutputStream(file);
            dataPicturePath.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            if(outputStream != null){
                String output = dataName + ";" + file.getAbsolutePath();
                outputStream.write(output.getBytes());
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {
        String result = null;

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                result = new String(buffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
