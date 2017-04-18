package org.openfact.util;

import com.google.api.services.gmail.model.Message;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FindMaxHistoryIdTask extends RecursiveTask<BigInteger> {

    private final int threshold;
    private final List<Message> messages;
    private int start;
    private int end;

    public FindMaxHistoryIdTask(List<Message> messages, int start, int end, int threshold) {
        this.threshold = threshold;
        this.messages = messages;
        this.start = start;
        this.end = end;
    }

    protected BigInteger compute() {
        if (end - start < threshold) {
            BigInteger max = BigInteger.ONE.negate();
            for (int i = start; i <= end; i++) {
                BigInteger n = messages.get(i).getHistoryId();
                if (n.compareTo(max) > 0) {
                    max = n;
                }
            }
            return max;
        } else {
            int midway = (end - start) / 2 + start;
            FindMaxHistoryIdTask a1 = new FindMaxHistoryIdTask(messages, start, midway, threshold);
            a1.fork();
            FindMaxHistoryIdTask a2 = new FindMaxHistoryIdTask(messages, midway + 1, end, threshold);

            BigInteger compute = a2.compute();
            BigInteger join = a1.join();
            if (compute.compareTo(join) > 0) {
                return compute;
            } else {
                return join;
            }
        }
    }

}
