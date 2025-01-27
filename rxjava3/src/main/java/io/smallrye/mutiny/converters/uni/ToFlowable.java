package io.smallrye.mutiny.converters.uni;

import java.util.function.Function;

import io.reactivex.rxjava3.core.Flowable;
import io.smallrye.mutiny.Uni;

public class ToFlowable<T> implements Function<Uni<T>, Flowable<T>> {
    public static final ToFlowable INSTANCE = new ToFlowable();

    private ToFlowable() {
        // Avoid direct instantiation
    }

    @Override
    public Flowable<T> apply(Uni<T> uni) {
        return Flowable.fromPublisher(uni.convert().toPublisher());
    }
}
