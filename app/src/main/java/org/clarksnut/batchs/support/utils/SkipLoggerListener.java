package org.clarksnut.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.inject.Named;
import java.util.List;

@Named
public class SkipLoggerListener extends AbstractLoggerListener implements SkipReadListener, SkipProcessListener, SkipWriteListener {

    private static final Logger logger = Logger.getLogger(SkipLoggerListener.class);

    @Override
    public void onSkipProcessItem(Object item, Exception ex) throws Exception {

    }

    @Override
    public void onSkipReadItem(Exception ex) throws Exception {

    }

    @Override
    public void onSkipWriteItem(List<Object> items, Exception ex) throws Exception {

    }

}
