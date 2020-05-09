import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import transcribe.event.LambdaHandler;
import transcribe.event.provider.ProviderEvents;
import transcribe.event.services.ServiceEvents;

import javax.xml.ws.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class ServiceEventsTest {

    @Test
    @DisplayName("Test catch block of extractJson")
    void testCatchBlockHandleRequest() throws IOException, URISyntaxException {
        ServiceEvents serviceEvents = new ServiceEvents();

        new MockUp<ProviderEvents>() {
            @Mock
            public void sendEmail(String email, String jobName) throws Exception {
                throw new Exception("Forced Exception");
            }
        };

        assertFalse(serviceEvents.extractJson(generateS3Event(), null));
    }

    @Test
    @DisplayName("Test catch block of getEmail")
    void testCatchBlockGetEmail() {
        ServiceEvents serviceEvents = new ServiceEvents();

        new MockUp<ProviderEvents>() {
            @Mock
            public File fetchFile(String bucket, String jobName, Context context) throws IOException {
                throw new IOException("Forced Exception");
            }
        };

        assertEquals("error from fetchFile", serviceEvents.getEmail("Test object", null));
    }

    @Test
    @DisplayName("Test catch block of getEmail bufferedReader")
    void testCatchBlockOfBufferedReader() {
        ServiceEvents serviceEvents = new ServiceEvents();
        File file = new File("NA");

        new MockUp<ProviderEvents>() {
            @Mock
            public File fetchFile(String bucket, String jobName, Context context) throws IOException {
                return file;
            }
        };

        new MockUp<BufferedReader>() {
            @Mock
            public String readLine() throws IOException {
                throw new IOException("Forced exception");
            }
        };

        assertEquals("error from bufferedReader", serviceEvents.getEmail("Test object", null));
    }


    public S3Event generateS3Event() throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("s3Event.json")).toURI());
        S3Event s3Event = new ObjectMapper().readValue(file, S3Event.class);

        return s3Event;
    }
}
