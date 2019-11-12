package io.smallrye.reactive.operators.multi;

import io.smallrye.reactive.Multi;
import io.smallrye.reactive.helpers.ParameterValidation;
import org.reactivestreams.Subscriber;

import java.util.function.Predicate;

/**
 * Emits the items from upstream while the given predicate returns {@code true} for the item.
 * The stream is completed once the predicate return {@code false}.
 *
 * @param <T> the type of item
 */
public final class MultiTakeWhileOp<T> extends AbstractMultiWithUpstream<T, T> {

    private final Predicate<? super T> predicate;

    public MultiTakeWhileOp(Multi<? extends T> upstream, Predicate<? super T> predicate) {
        super(upstream);
        this.predicate = ParameterValidation.nonNull(predicate, "predicate");
    }

    @Override
    public void subscribe(Subscriber<? super T> actual) {
        upstream.subscribe(new TakeWhileSubscriber<>(actual, predicate));
    }

    static final class TakeWhileSubscriber<T> extends MultiOperatorSubscriber<T, T> {
        private final Predicate<? super T> predicate;

        TakeWhileSubscriber(Subscriber<? super T> downstream, Predicate<? super T> predicate) {
            super(downstream);
            this.predicate = predicate;
        }

        @Override
        public void onNext(T t) {
            if (isDone()) {
                return;
            }

            boolean pass;
            try {
                pass = predicate.test(t);
            } catch (Throwable e) {
                onError(e);
                return;
            }

            if (!pass) {
                cancel();
                downstream.onComplete();
                return;
            }

            downstream.onNext(t);
        }
    }
}
