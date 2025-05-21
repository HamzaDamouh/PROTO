package edu.ezip.ing1.pds.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.server.Dispatcher;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final Connection connection;
    private final Thread self;
    private static final String threadNamePrfx = "core-request-handler";
    private final InputStream instream;
    private final OutputStream outstream;
    // private final Connection connection;
    private final static String LoggingLabel = "C o re - B a c k e n d - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private int requestCount = 0;

    private final CoreBackendServer father;
    private final Dispatcher dispatcher;


    private static final int maxTimeLapToGetAClientPayloadInMs = 5000;
    private static final int timeStepMs = 300;
    private final BlockingDeque<Integer> waitArtifact = new LinkedBlockingDeque<Integer>(1);

    protected RequestHandler(final Socket socket,
                             final Connection connection,
                             final int myBirthDate,
                             final CoreBackendServer father,
                             final Dispatcher dispatcher) throws IOException {
        this.socket = socket;
        this.connection = connection;
        this.father = father;
        this.dispatcher = dispatcher;

        final StringBuffer threadName = new StringBuffer();
        threadName.append(threadNamePrfx).append("★").append(String.format("%04d",myBirthDate));
        self = new Thread(this, threadName.toString());
        instream = socket.getInputStream();
        outstream = socket.getOutputStream();
        self.start();
    }


    /*
    @Override
    public void run() {
        try {

            int timeout = maxTimeLapToGetAClientPayloadInMs;
            while (0 == instream.available() && 0 < timeout) {
                waitArtifact.pollFirst(timeStepMs, TimeUnit.MILLISECONDS);
                timeout-=timeStepMs;
            }
            if (0>timeout) return;

            final byte [] inputData = new byte[instream.available()];
            instream.read(inputData);
            final Request request = getRequest(inputData);



            final Response response = dispatcher.dispatch(request, connection);

            final byte [] outoutData = getResponse(response);
            LoggingUtils.logDataMultiLine(logger, Level.DEBUG, outoutData);
            outstream.write(outoutData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            father.completeRequestHandler(this);
        }
    }

     */
    @Override
    public void run() {
        boolean responded = false;
        boolean isSaturation = false;
        try {
            // 1) Wait for request bytes
            int timeout = maxTimeLapToGetAClientPayloadInMs;
            while (instream.available() == 0 && timeout > 0) {
                waitArtifact.pollFirst(timeStepMs, TimeUnit.MILLISECONDS);
                timeout -= timeStepMs;
            }
            if (timeout < 0) return;

            // 2) Read and parse request
            byte[] inputData = new byte[instream.available()];
            instream.read(inputData);
            Request request = getRequest(inputData);

            // detect saturation
            isSaturation = "saturation".equals(request.getRequestOrder());

            // 3) If no connection → immediate “Pool occupé”
            if (connection == null) {
                Response err = new Response(request.getRequestId(), "Pool occupé");
                byte[] errBytes = getResponse(err);
                LoggingUtils.logDataMultiLine(logger, Level.DEBUG, errBytes);
                outstream.write(errBytes);
                outstream.flush();
                responded = true;
                return;
            }

            // 4) Dispatch and build response
            Response response = dispatcher.dispatch(request, connection);
            byte[] outData = getResponse(response);
            LoggingUtils.logDataMultiLine(logger, Level.DEBUG, outData);

            // 5) Send back to client
            outstream.write(outData);
            outstream.flush();
            responded = true;
        } catch (Exception e) {
            logger.error("Error in RequestHandler: ", e);
        } finally {
            try {
                if (responded) {
                    // close the socket to signal end of response
                    socket.close();
                }
            } catch (IOException e) {
                logger.warn("Failed to close client socket", e);
            }
            // only release the connection if we actually held one and not during saturation
            if (connection != null && !isSaturation) {
                father.completeRequestHandler(this);
            }
        }
    }



    private final Request getRequest(byte [] data) throws IOException {
        logger.debug("data received {} bytes", data.length);
        LoggingUtils.logDataMultiLine(logger, Level.DEBUG, data);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        final Request request = mapper.readValue(data, Request.class);
        logger.debug(request.toString());
        return request;
    }

    private final byte [] getResponse(final Response response) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(response);
    }

    public final Connection getConnection() {
        return connection;
    }

    public final Socket getSocket() {
        return socket;
    }


}
