package com.zendesk.suas;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class ReduxStoreBuilderNullTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_reducers_null() {
        final Collection<Reducer> r = null;
        new ReduxStore.Builder(r).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_reducers_emptyList() {
        final Collection<Reducer> r = new ArrayList<>();
        new ReduxStore.Builder(r).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_state_null() {
        new ReduxStore.Builder(new TestReducer())
                .withInitialState(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_middleware_nullList() {
        Collection<Middleware> middleware = null;
        new ReduxStore.Builder(new TestReducer())
                .withMiddleware(middleware)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_filter_null() {
        new ReduxStore.Builder(new TestReducer())
                .withDefaultFilter(null)
                .build();
    }

    private static class TestReducer extends Reducer<String> {

        @Nullable
        @Override
        public String reduce(@NonNull String oldState, @NonNull Action<?> action) {
            return "new";
        }

        @NonNull
        @Override
        public String getEmptyState() {
            return "empty";
        }
    }

}
