package org.openfact.batch;

import java.util.List;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named
public class MyItemWriter extends AbstractItemWriter {

    @Override
    public void writeItems(List list) {
        System.out.println("writeItems: " + list);
    }
}
