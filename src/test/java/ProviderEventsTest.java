import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import transcribe.event.provider.ProviderEvents;
import transcribe.event.services.ServiceEvents;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Provider;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProviderEventsTest {

    @Test
    @DisplayName("Test sendEmail in the ProviderEvents")
    void testSendEmail(){
        ProviderEvents providerEvents = new ProviderEvents();

        new MockUp<AmazonSimpleEmailServiceClient>(){

            @Mock
            public SendEmailResult sendEmail(SendEmailRequest request){

                return null;
            }
        };

        assertTrue(providerEvents.sendEmail("Any","Any"));
    }

    @Test
    @DisplayName("Test catch block of sendEmail method")
    void testCatchBlockSendEmail() throws IOException, URISyntaxException {
        ProviderEvents providerEvents = new ProviderEvents();

        new MockUp<Destination>() {
            @Mock
            public Destination withToAddresses(String... toAddresses) throws Exception {
                throw new Exception("Forced Exception");
            }
        };

        assertFalse(providerEvents.sendEmail("Any","Any"));
    }
}
