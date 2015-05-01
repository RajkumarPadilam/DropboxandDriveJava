import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;

public class DropboxAuthText
{
	
    public static void main(String[] args) throws IOException, DbxException {
        // Get your app key and secret from the Dropbox developers website.
        final String APP_KEY = "9kdn1poop6m7aa5";
        final String APP_SECRET = "4od8dwbh20nopyl";
        
        
        
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

        // Signing the file before storing on dropbox
        GenSig SigObject =  new GenSig();
        SigObject.signFile("raj.txt");
        File inputFile = new File("raj.txt");
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            DbxEntry.File uploadedFile = client.uploadFile("/magnum-opus.txt",
                DbxWriteMode.add(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());
        } finally {
            inputStream.close();
        }

        /*DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        System.out.println("Files in the root path:");
        for (DbxEntry child : listing.children) {
            System.out.println("	" + child.name + ": " + child.toString());
        }*/

        
        // Verifiy the signature before downloading
        VerSig verObject=new VerSig();
        if(verObject.verifyFile())
        {
		        FileOutputStream outputStream = new FileOutputStream("output.txt");
		        try {
		            DbxEntry.File downloadedFile = client.getFile("/magnum-opus.txt", null,
		                outputStream);
		            System.out.println("File Downloaded successfully");
		            //System.out.println("Metadata: " + downloadedFile.toString());
		        } finally {
		            outputStream.close();
		        }
        }
    }
}