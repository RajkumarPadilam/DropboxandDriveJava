import com.dropbox.core.*;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.swing.text.DefaultEditorKit.CopyAction;

public class DropBoxEncryption
{
	
    public static void main(String[] args) throws IOException, DbxException {
        // Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = "9kdn1poop6m7aa5";
        final String APP_SECRET = "4od8dwbh20nopyl";
        
        encryption encryptObject=null;
		try {
			encryptObject = new encryption();
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		} 
        
		//Dropbox API to access the files
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
            Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        // Have the user sign in and authorize your app.
        String authorizeUrl = webAuth.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

        // This will fail if the user enters an invalid authorization code.
        DbxAuthFinish authFinish = webAuth.finish(code);
        String accessToken = authFinish.accessToken;

        DbxClient client = new DbxClient(config, accessToken);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);

     
        
        
        // encrypt the file before storing on dropbox
        	try {
				
        		encryptObject.makeKey();
				encryptObject.encrypt("E://CloudSyncFolder//UserFile.txt", "E://CloudSyncFolder//UserFileE.txt");
				
				// Signing the file before storing on dropbox
		        GenSig SigObject =  new GenSig();
		        SigObject.signFile("E://CloudSyncFolder//UserFileE.txt");
		        
		        
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
        				
		//Uploading the file onto Dropbox
        File inputFile = new File("E://CloudSyncFolder//UserFileE.txt");
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = client.uploadFile("/UserFile.txt",
                DbxWriteMode.add(), inputFile.length(), inputStream);
            //System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }
        
        //Syncing between google drive and drop box
        boolean syncComplete=false;
        while(!syncComplete)
        {
        	try {
	        	System.out.println("Waiting for files to sync between dropbox and google drive");
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
        	
	        File dir=new File("C://Users//DELL//Dropbox/Apps//RajCloudProject//");
	        File files[] = dir.listFiles();
	        for(File file: files)
	        {
	        	if(file.getName().contains("UserFile"))
	        		syncComplete=true;
	        }
	        
        }
        syncFiles("C://Users//DELL//Dropbox/Apps//RajCloudProject//UserFile.txt", "C:/Users/DELL/Google Drive/CloudProject/UserFileE.txt");
        

        // Download and decrypt the file        
		        FileOutputStream outputStream = new FileOutputStream("E://CloudSyncFolder//DownloadedFromBoxFile.txt");
		        try {
		           DbxEntry.File downloadedFile = client.getFile("/UserFile.txt", null,
		                outputStream);
		            
		            syncFiles("C:/Users/DELL/Google Drive/CloudProject/UserFileE.txt", "E:/CloudSyncFolder/DownloadedFileFromDrive.txt");
		            System.out.println("File Downloaded successfully");	            
		              
		         // Verifiy the signature before downloading
		            VerSig verObject=new VerSig();
		            if(verObject.verifyFile("E:/CloudSyncFolder/DownloadedFromBoxFile.txt"))
		            {
		        	encryptObject.decrypt("E:/CloudSyncFolder/DownloadedFromBoxFile.txt", "E:/CloudSyncFolder/UserFileB.txt");
		        	encryptObject.decrypt("E:/CloudSyncFolder/DownloadedFileFromDrive.txt", "E:/CloudSyncFolder/UserFileD.txt");
		            }
		            
		            System.out.println("File has been Decrypted");
		        } catch (Exception e) {
					e.printStackTrace();
				} finally {
		            outputStream.close();
		        }
    }
    
    public static void syncFiles(String source, String destination)
    {
    	
    	File dropBoxFile= new File(source);
    	File drivefile= new File(destination);
    	
    	try {
			Files.copy(dropBoxFile.toPath(), drivefile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}