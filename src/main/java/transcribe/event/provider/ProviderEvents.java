package transcribe.event.provider;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProviderEvents {
    private static final Logger LOGGER = Logger.getLogger(ProviderEvents.class);
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();

    /**
     * This method sends the email out using AWS SES
     * @param email String
     * @param jobName
     */
    public Boolean sendEmail(String email, String jobName) {

        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();

            SendEmailRequest request = new SendEmailRequest().withDestination(
                    new Destination().withToAddresses(email))
                    .withMessage(new com.amazonaws.services.simpleemail.model.Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF8")
                                            .withData("Downloading " + jobName + " is complete.")))
                            .withSubject(new Content()
                                    .withCharset("UTF-8")
                                    .withData("Message from Passport Submission Service.")))
                    .withSource(email);
            client.sendEmail(request);
            return true;

        } catch (Exception e){
            LOGGER.info(e);
            return false;
        }
    }

    /**
     * Gets the file from the bucket
     * @param bucket String
     * @param jobName String
     * @param context Context
     * @return File
     */
    public File fetchFile(String bucket, String jobName, Context context) throws IOException {

        try {
            InputStream in = s3.getObject(bucket, jobName).getObjectContent();
            Files.copy(in, Paths.get("/tmp/" + "email"+jobName));
            return new File("/tmp/" + "email"+jobName);
        } catch (AmazonServiceException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
