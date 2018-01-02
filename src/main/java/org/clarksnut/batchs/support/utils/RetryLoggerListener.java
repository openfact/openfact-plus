package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.inject.Named;
import java.util.List;

@Named
public class RetryLoggerListener extends AbstractLoggerListener implements RetryReadListener, RetryProcessListener, RetryWriteListener {

    private static final Logger logger = Logger.getLogger(RetryLoggerListener.class);

    @Override
    public void onRetryProcessException(Object item, Exception ex) throws Exception {

    }

    @Override
    public void onRetryReadException(Exception ex) throws Exception {

    }

    @Override
    public void onRetryWriteException(List<Object> items, Exception ex) throws Exception {

    }

}
