package br.com.filazero.service;

import java.util.Set;

public final class QueueListParams {

    public static final Set<String> ALLOWED_STATUSES =
            Set.of("WAITING", "CHECKED_IN", "CALLED", "DONE", "NO_SHOW");

    private QueueListParams() {}
}
