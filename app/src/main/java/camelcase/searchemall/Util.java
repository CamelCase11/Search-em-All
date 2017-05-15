package camelcase.searchemall;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
    private Context mContext;

    public Util(Context c) {
        mContext = c;
    }

    public String InputStreamToString(InputStream inputStream) {
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public InputStream openUrl(String Url) {
        try {
            URL myUrl = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            return conn.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public void writeToFile(String filename, String content) {
        try {
            FileOutputStream stream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            stream.write(content.getBytes());
            stream.close();
        } catch (IOException e) {
            new File(mContext.getFilesDir(), filename);
        }
    }

    public String readFile(String filename) {
        try {
            FileInputStream stream = mContext.openFileInput(filename);
            int c;
            String result = "";
            while ((c = stream.read()) != -1) {
                result += Character.toString((char) c);
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

}
