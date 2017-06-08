package pes.twochange.services;

import pes.twochange.domain.model.ModelAdapter;

public class ModelAdapterFactory<T> {

    public ModelAdapter<T> build(final Class classType, final T object) {
        return new ModelAdapter<T>() {
            @Override
            public Class classType() {
                return classType;
            }

            @Override
            public T object() {
                return object;
            }
        };
    }
}
