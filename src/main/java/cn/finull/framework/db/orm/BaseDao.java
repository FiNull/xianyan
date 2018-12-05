package cn.finull.framework.db.orm;

import cn.finull.framework.core.bean.Bean;
import cn.finull.framework.db.annotation.Delete;
import cn.finull.framework.db.annotation.Insert;
import cn.finull.framework.db.annotation.Select;
import cn.finull.framework.db.annotation.Update;
import java.util.List;

public interface BaseDao<T> extends Bean {
    @Override
    default Class getClassKey() {
        return null;
    }

    @Insert(generatedKey = true)
    int insert(T t);

    @Update
    int update(T t);

    @Delete
    <I> int deleteById(I id);

    @Select
    <I> T selectById(I id);

    @Select
    List<T> select();
}
