package transcribe.event;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import org.apache.log4j.Logger;
import transcribe.event.services.ServiceEvents;

import java.io.IOException;

public class LambdaHandler implements RequestHandler<S3Event, String> {
    private static final Logger LOGGER = Logger.getLogger(LambdaHandler.class);
    private ServiceEvents serviceEvents = new ServiceEvents();

    public LambdaHandler() throws IOException {
        //zero constructor
    }

    /**
     *This method collects the event when an S3 object is created in the Transcribeandtranslate bucket.
     * @param event
     * @param context
     * @return
     */
    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            serviceEvents.extractJson(event, context);
            return "Finished handleRequest()";
        } catch (Exception e){
            LOGGER.error(e);
            return "Error with handleRequest()";
        }
    }
}