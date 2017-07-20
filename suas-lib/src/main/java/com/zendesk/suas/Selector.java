package com.zendesk.suas;


/**
 * Definition to transform {@link State} into a view model
 * @param <E> type of the view model
 */
public interface Selector<E, F> {

    F selectData(E data);

}
