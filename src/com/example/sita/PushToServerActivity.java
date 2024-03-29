package com.example.sita;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class PushToServerActivity extends Activity {
	
	
    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
        
    String upLoadServerUri = null;
     
    /**********  File Path *************/
     String uploadFilePath = "/mnt/sdcard/SITA/"+ Integer.toString(GlobalVars.school_id)+"_"+Long.toString(GlobalVars.date_time)+"/";
     String uploadFileName;
    
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_to_server);
		
		uploadButton = (Button)findViewById(R.id.uploadButton);
        //messageText  = (TextView)findViewById(R.id.messageText);
         
       // messageText.setText("Uploading file path :- '/mnt/sdcard/SITA/"+uploadFileName+"'");
        
       
       // upLoadServerUri = "http://secret-basin-2251.herokuapp.com/upload";
        
        uploadButton.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                 
                dialog = ProgressDialog.show(PushToServerActivity.this, "", "Uploading files", true);
                 
                new Thread(new Runnable() {
                        public void run() {
                             runOnUiThread(new Runnable() {
                                    public void run() {
                                       // messageText.setText("Uploading Started");
                                    }
                                });              
                             
                             upLoadServerUri = "http://sita.bl.ee/upload.php";
                             // upload all images in this folder to sita.bl.ee
                             for(int i=0;i<GlobalVars.imagePaths.size(); i++)
                             {
                            	 uploadFileName = GlobalVars.imagePaths.get(i).toString();
                            	 uploadFile(uploadFilePath + uploadFileName);
                             }
                             
                             
                            /* upLoadServerUri = "http://secret-basin-2251.herokuapp.com/upload";*/
                             uploadFile(uploadFilePath + "data.json");
                             
                                                      
                        }
                      }).start();        
                }
            });
    }
	
	 public int uploadFile(String sourceFileUri) {
         
         
         String fileName = sourceFileUri;
 
         HttpURLConnection conn = null;
         DataOutputStream dos = null;  
         String lineEnd = "\r\n";
         String twoHyphens = "--";
         String boundary = "*****";
         int bytesRead, bytesAvailable, bufferSize;
         byte[] buffer;
         int maxBufferSize = 1 * 1024 * 1024; 
         File sourceFile = new File(sourceFileUri); 
          
         if (!sourceFile.isFile()) {
              
              dialog.dismiss(); 
               
              Log.e("uploadFile", "Source File not exist :"
                                  +uploadFilePath + "" + uploadFileName);
               
              runOnUiThread(new Runnable() {
                  public void run() {
                   //   messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                  }
              }); 
               
              return 0;
           
         }
         else
         {
              try { 
                   
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
                       
                      runOnUiThread(new Runnable() {
                           public void run() {
                                
                               /*String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                             +" http://www.androidexample.com/media/uploads/"
                                             +uploadFileName;*/
                                
                             //  messageText.setText(msg);
                               Toast.makeText(PushToServerActivity.this, "File Upload Complete.", 
                                            Toast.LENGTH_SHORT).show();
                           }
                       });                
                  }    
                   
                  //close the streams //
                  fileInputStream.close();
                  dos.flush();
                  dos.close();
                    
             } catch (MalformedURLException ex) {
                  
                 dialog.dismiss();  
                 ex.printStackTrace();
                  
                 runOnUiThread(new Runnable() {
                     public void run() {
                         /*messageText.setText("MalformedURLException Exception : check script url.");*/
                         Toast.makeText(PushToServerActivity.this, "MalformedURLException", 
                                                             Toast.LENGTH_SHORT).show();
                     }
                 });
                  
                 Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
             } catch (Exception e) {
                  
                 dialog.dismiss();  
                 e.printStackTrace();
                  
                 runOnUiThread(new Runnable() {
                     public void run() {
                         /*messageText.setText("Got Exception : see logcat ");*/
                         Toast.makeText(PushToServerActivity.this, "Got Exception : see logcat ", 
                                 Toast.LENGTH_SHORT).show();
                     }
                 });
                 Log.e("Upload file to server Exception", "Exception : "
                                                  + e.getMessage(), e);  
             }
             dialog.dismiss();       
             return serverResponseCode; 
              
          } // End else block 
        } 
}