package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.inject.Named;
import java.util.List;

@Named
public class ItemLoggerListener extends AbstractLoggerListener implements ItemReadListener, ItemProcessListener, ItemWriteListener {

    private static final Logger logger = Logger.getLogger(ItemLoggerListener.class);

    @Override
    public void beforeRead() throws Exception {

    }

    @Override
    public void afterRead(Object item) throws Exception {

    }

    @Override
    public void onReadError(Exception ex) throws Exception {

    }

    @Override
    public void beforeProcess(Object item) throws Exception {

    }

    @Override
    public void afterProcess(Object item, Object result) throws Exception {

    }

    @Override
    public void onProcessError(Object item, Exception ex) throws Exception {

    }

    @Override
    public void beforeWrite(List<Object> items) throws Exception {

    }

    @Override
    public void afterWrite(List<Object> items) throws Exception {

    }

    @Override
    public void onWriteError(List<Object> items, Exception ex) throws Exception {

    }

}
