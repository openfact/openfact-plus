package org.openfact.batchs.support.utils;

import org.jboss.logging.Logger;

import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.listener.StepListener;
import javax.inject.Named;

@Named
public class ChunkLoggerListener extends AbstractLoggerListener implements ChunkListener {

    private static final Logger logger = Logger.getLogger(ChunkLoggerListener.class);

    @Override
    public void beforeChunk() throws Exception {

    }

    @Override
    public void onError(Exception ex) throws Exception {

    }

    @Override
    public void afterChunk() throws Exception {

    }
}
