import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.*;
import transcribe.event.LambdaHandler;
import transcribe.event.provider.ProviderEvents;
import transcribe.event.services.ServiceEvents;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static junit.framework.Assert.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LambdaHandlerTest {

    static LambdaHandler lambdaHandler;

    public LambdaHandlerTest() throws IOException {
    }

    @BeforeAll()
    static void setup() throws IOException {
        lambdaHandler = new LambdaHandler();
    }

    @Test
    @Order(1)
    @DisplayName("Testing the handleRequest")
    void testTheHandleRequest() throws URISyntaxException, IOException {

        new MockUp<ProviderEvents>(){

            @Mock
            public File fetchFile(String bucket, String jobName, Context context) throws IOException{
                File file = new File ("src/test/resources/testEmailFile.txt");

                return file;
            }
        };

        new MockUp<AmazonSimpleEmailServiceClient>(){

            @Mock
            public SendEmailResult sendEmail(SendEmailRequest request){

                return null;
            }
        };

        assertEquals ("Finished handleRequest()", lambdaHandler.handleRequest(generateS3Event(), null));
    }

    @Test
    @Order(2)
    @DisplayName("Test catch block of handleRequest")
    void testCatchBlockHandleRequest() throws IOException, URISyntaxException {

        new MockUp<ServiceEvents>() {
            @Mock
            public Boolean extractJson(S3Event event, Context context) throws Exception {
                throw new Exception("Forced Exception");
            }
        };

        S3Event s3Event = generateS3Event();

        assertEquals("Error with handleRequest()", lambdaHandler.handleRequest(s3Event, null));
    }

    public S3Event generateS3Event() throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("s3Event.json")).toURI());
        S3Event s3Event = new ObjectMapper().readValue(file, S3Event.class);

        return s3Event;
    }
}
