package com.hillelevo.cityelf.webutils;

import static com.hillelevo.cityelf.Constants.TAG;

import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import com.hillelevo.cityelf.Constants;
import com.hillelevo.cityelf.Constants.Prefs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class AdvertsTask extends AsyncTask<String, Void, String> {

  int responseCode;
  private AdvertsResponse response = null;

  public AdvertsTask(AdvertsResponse listener) {
    response = listener;
  }


  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  @Override
  protected String doInBackground(String... params) {
    URL url = null;

    HttpURLConnection connection = null;
    OutputStream outputStream = null;
    BufferedReader reader = null;
    StringBuffer buffer = new StringBuffer();

    try {
      if (params[1] != null && params[1].equals("POST")) {

        try {
          url = new URL(params[0]);
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("POST");
          connection.setDoInput(true);
          connection.setDoOutput(true);

//          connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
//          connection.setReadTimeout(Constants.CONNECTION_TIMEOUT);

          if (params[3] != null) {
            connection.setRequestProperty(Prefs.AUTH, params[3]);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("content-type", "application/json");
          }
          connection.connect();

          String bodyParams = params[2];
          outputStream = connection.getOutputStream();
          outputStream.write(bodyParams.getBytes());
          outputStream.close();

          int responseCode = connection.getResponseCode();
          if (responseCode != 200) {
            Log.d(TAG, "Response Code is: " + responseCode);
            return "Error " + Constants.POST + " " + responseCode;
          }
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (ProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (params[1] != null && params[1].equals("PUT")) {

        try {
          url = new URL(params[0]);
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("PUT");
          connection.setDoInput(true);
          connection.setDoOutput(true);

          String authCertificate = "Basic " + Base64
              .encodeToString(("authorized_role@cityelf.com.ua" + ":" + 123456).getBytes(),
                  Base64.URL_SAFE | Base64.NO_WRAP);

          connection.setRequestProperty(Prefs.AUTH, authCertificate);
          connection.setRequestProperty("Accept", "application/json");
          connection.setRequestProperty("content-type", "application/json");
          connection.connect();

          outputStream = connection.getOutputStream();
          outputStream.write(params[2].getBytes());
          outputStream.close();

          responseCode = connection.getResponseCode();

          if (responseCode != 200) {

          }
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (ProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (params[1] != null && params[1].equals("GET")) {
        url = new URL(params[0]);
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty(Prefs.AUTH, params[2]);

//        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpsURLConnection.HTTP_OK) {
          Log.d(TAG, "Response Code is: " + responseCode);
          return "error" + responseCode;
        }

      }

      //receive & read data response
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      buffer = new StringBuffer();

      String line = "";

      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }
    } catch (
        MalformedURLException e)

    {
      e.printStackTrace();
    } catch (
        IOException e)

    {
      e.printStackTrace();
    } finally

    {
      if (connection != null) {
        connection.disconnect();
      }
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (buffer == null)

    {
      return null;
    } else

    {
      return buffer.toString();
    }
  }

  @Override
  protected void onPostExecute(String result) {
    super.onPostExecute(result);
    response.advertsResponse(result);
  }

  public interface AdvertsResponse {

    void advertsResponse(String output);
  }

}