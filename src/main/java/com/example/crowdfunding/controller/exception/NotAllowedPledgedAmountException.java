package com.example.crowdfunding.controller.exception;

import java.util.Arrays;

public class NotAllowedPledgedAmountException extends IllegalArgumentException {

    public NotAllowedPledgedAmountException(long[] allowed, long pledgedCKB) {
        super("Pledged amount " + pledgedCKB + " is not allowed. Allowed amounts are "
                + Arrays.toString(allowed));
    }
}
