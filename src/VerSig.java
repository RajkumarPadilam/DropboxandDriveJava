import java.io.*;
import java.security.*;
import java.security.spec.*;

class VerSig 
{

    public static void main(String[] args) {

        /* Verify a DSA signature */
    }
    
    public boolean verifyFile(String filename)
    {
    	boolean verifies=false;
    	try {
    		
    		/* public key   */
        	FileInputStream keyfis = new FileInputStream("E://CloudSyncFolder//pk");
        	byte[] encKey = new byte[keyfis.available()];  
        	keyfis.read(encKey);
        	keyfis.close();
        	
        	X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        	KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        	PublicKey pubKey =  keyFactory.generatePublic(pubKeySpec);
        	
        	/* signature bytes   */
        	FileInputStream sigfis = new FileInputStream("E://CloudSyncFolder//sig");
        	byte[] sigToVerify = new byte[sigfis.available()]; 
        	sigfis.read(sigToVerify);
        	sigfis.close();
        	
        	// To verify the signature
        	Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
        	sig.initVerify(pubKey);
        	
        	//Supply data that needs to be verified
        	FileInputStream datafis = new FileInputStream(filename);
        	BufferedInputStream bufin = new BufferedInputStream(datafis);

        	byte[] buffer = new byte[1024];
        	int len;
        	while (bufin.available() != 0) {
        	    len = bufin.read(buffer);
        	    sig.update(buffer, 0, len);
        	};
        	bufin.close();
        	
        	verifies = sig.verify(sigToVerify);
        	System.out.println("signature verifies: " + verifies);
        	
        }
    	catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
		return verifies;  	
    }
}