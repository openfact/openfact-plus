package org.openfact.batchs.refresh;

import org.openfact.broker.BrokerType;
import org.openfact.models.UserModel;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.List;

@Named
@Dependent
public class TestWriter extends AbstractItemWriter {

    @Override
    public void writeItems(List<Object> items) throws Exception {
        for (Object o : items) {
            if (o instanceof OfflineRefreshTokenProcessor.MappedUser) {
                refreshIdp((OfflineRefreshTokenProcessor.MappedUser) o);
            }
        }
    }

    private void refreshIdp(OfflineRefreshTokenProcessor.MappedUser mappedUser) {
        UserModel user = mappedUser.getUser();
        for (BrokerType idp : mappedUser.getIdps()) {
            switch (idp) {
                case GOOGLE:
                    break;
                default:
                    break;
            }
        }
    }

}
