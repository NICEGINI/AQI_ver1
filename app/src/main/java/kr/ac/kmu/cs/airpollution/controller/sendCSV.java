package kr.ac.kmu.cs.airpollution.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pabel on 2016-08-13.
 */
public class sendCSV {
        Context context;

        int i = 1;
        int serverResponseCode = 0;

        ProgressDialog dialog = null;
        String upLoadServerUri = "http://teamb-iot.calit2.net/week3b/bluebase/main/receivecsv.php";
        // final String uploadFilePath = "storage/emulated/0/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
        String uploadFilePath = Environment.getExternalStorageDirectory() + "/Download/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
        String uploadFileName; //전송하고자하는 파일 이름
        String fullpath;

        public sendCSV(Context con,String file){
            context = con;
            dialog = ProgressDialog.show(context, "", "Uploading file...", true);
            uploadFileName = file;
            fullpath = uploadFilePath+uploadFileName;
        }


        /**********  File Path *************/



        /************* Php script path ****************/



        public int uploadFile() {
            Log.w("csv uploadFilePath",uploadFilePath);
            Log.w("csv uploadFileName",uploadFileName);
            Log.w("csv path",fullpath);


            String fileName = fullpath;



            HttpURLConnection conn = null;

            DataOutputStream dos = null;

            String lineEnd = "\r\n";

            String twoHyphens = "--";

            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;

            byte[] buffer;

            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(fileName);



            if (!sourceFile.isFile()) {



                dialog.dismiss();



                Log.e("uploadFile", "Source File not exist :"

                        +uploadFilePath + "" + uploadFileName);

                return 0;

            }

            else

            {

                try {


                    StrictMode.enableDefaults();
                    StrictMode.allowThreadDiskReads();
                    StrictMode.allowThreadDiskWrites();


                    // open a URL connection to the Servlet

                    FileInputStream fileInputStream = new FileInputStream(sourceFile);

                    URL url = new URL(upLoadServerUri);



                    // Open a HTTP  connection to  the URL

                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); // Allow Inputs

                    conn.setDoOutput(true); // Allow Outputs

                    conn.setUseCaches(false); // Don't use a Cached Copy

                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");

                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    conn.setRequestProperty("uploaded_file", fileName);

                    Log.d("csv","잘됨00");

                    dos = new DataOutputStream(conn.getOutputStream());



                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""

                            + fileName + "\"" + lineEnd);



                    dos.writeBytes(lineEnd);



                    // create a buffer of  maximum size

                    bytesAvailable = fileInputStream.available();



                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    buffer = new byte[bufferSize];



                    // read file and write it into form...

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                    while (bytesRead > 0) {



                        dos.write(buffer, 0, bufferSize);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                    }



                    // send multipart form data necesssary after file data...

                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                    // Responses from the server (code and message)

                    serverResponseCode = conn.getResponseCode();

                    String serverResponseMessage = conn.getResponseMessage();



                    Log.i("uploadFile", "HTTP Response is : "

                            + serverResponseMessage + ": " + serverResponseCode);



                    if(serverResponseCode == 200){

                        //Toast.makeText(context,"CSV Upload complete!",Toast.LENGTH_SHORT);

                    }



                    //close the streams //

                    fileInputStream.close();

                    dos.flush();

                    dos.close();



                } catch (MalformedURLException ex) {

                    dialog.dismiss();

                    ex.printStackTrace();

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

                } catch (Exception e) {


                    dialog.dismiss();
                    e.printStackTrace();


                    Log.e("Upload Exception", "Exception : "
                            + e.getMessage(), e);

                }
                dialog.dismiss();

                return serverResponseCode;

            } // End else block

        }


}

