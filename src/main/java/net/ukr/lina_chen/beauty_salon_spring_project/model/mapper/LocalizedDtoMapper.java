package net.ukr.lina_chen.beauty_salon_spring_project.model.mapper;

@FunctionalInterface
public interface LocalizedDtoMapper<D, E> {
    D map(E e);
}
