package mybatis.spring.dao;

public interface BasicDao {


    <T> T get(Object pk, Class<T> tClass);

    <T> T selectOne(T entity);


}
