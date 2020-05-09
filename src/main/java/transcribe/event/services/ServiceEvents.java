package transcribe.event.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import org.apache.log4j.Logger;
import transcribe.event.provider.ProviderEvents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ServiceEvents {
    private static final Logger LOGGER = Logger.getLogger(ServiceEvents.class);
    private ProviderEvents providerEvents = new ProviderEvents();

    /**
     * This method takes the S3Event and extracts the records in the form of a json object. It then takes the 'key'
     * value, the email, and sends it to the provider sendEmail method.
     * @param event S3Event
     * @param context Context
     */
    public Boolean extractJson(S3Event event, Context context) {
        try {
            String email = null;
            String emailFile = event.getRecords().get(0).getS3().getObject().getKey();
            if (emailFile.contains("email")) {
                email = getEmail(emailFile, context);
            }
            if (emailFile.contains("email")) {
                String jobName = emailFile.substring(5,emailFile.length()-4);
                providerEvents.sendEmail(email, jobName);
            }
            return true;
        } catch (Exception e){
            LOGGER.info(e);
            return false;
        }
    }



    /**
     * This method gets the email from the s3 file
     * @param objectName String
     * @param context Context
     * @return
     */

    public String getEmail(String objectName, Context context) {
        File bucketFile;
        try {
            bucketFile = providerEvents.fetchFile("transcribeandtranslate", objectName, context);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e);
            return "error from fetchFile";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(bucketFile))){
            String email = br.readLine();
            return email;
        } catch (IOException e) {
            LOGGER.error(e);
            return "error from bufferedReader";
        }
    }
}
