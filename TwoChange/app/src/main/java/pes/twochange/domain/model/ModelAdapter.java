package pes.twochange.domain.model;

public interface ModelAdapter<T> {

    Class classType();
    T object();

}
