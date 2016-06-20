package org.openmrs.mobile.api.promise;

import org.jdeferred.*;

/**
 * Promise interface to observe when some action has occurred on the corresponding {@link Deferred} object.
 *
 * @param <T> Type used for {@link #done(org.jdeferred.DoneCallback)}
 */
public interface SimplePromise<T> extends Promise<T, Throwable, Void> {
}
