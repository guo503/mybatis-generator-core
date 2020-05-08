package mybatis.base.mapper;

public interface Mapper<T> extends SelectMapper<T>, InsertMapper<T>, UpdateMapper<T>, DeleteMapper<T> {
}
