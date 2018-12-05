package cn.finull.framework.db.orm;

import cn.finull.framework.core.BeanRepertory;
import cn.finull.framework.db.TransactionManager;
import cn.finull.framework.db.annotation.*;
import cn.finull.framework.except.DBParamterException;
import cn.finull.framework.util.ClassUtil;
import cn.finull.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 初始化dao层需要继承的类
 */
public abstract class DaoInitialize {

    private static final Logger LOG = LoggerFactory.getLogger(DaoInitialize.class);

    private List<Class<? extends BaseDao>> classList = new ArrayList<>();

    public DaoInitialize() {
        addDaoClass(classList);
        addDao();
    }

    public abstract void addDaoClass(List<Class<? extends BaseDao>> list);

    private TransactionManager transactionManager;

    private void addDao() {
        BeanRepertory repertory = BeanRepertory.getInstance();
        // 事务控制器
        transactionManager = repertory.get(TransactionManager.class);
        // 生成dao层的代理对象
        classList.forEach(clz -> {

            BaseDao baseDao = (BaseDao) Proxy.newProxyInstance(clz.getClassLoader(),
                    new Class[] {clz}, (p,m,a) -> {

                Annotation[] annos = m.getDeclaredAnnotations();
                if (contanier(annos,Insert.class)) {
                    Insert insert = m.getAnnotation(Insert.class);
                    return insert(insert.value(),a,insert.generatedKey());
                }
                else if (contanier(annos,Update.class)) {
                    Update update = m.getAnnotation(Update.class);
                    return update(update.value(),a);
                }
                else if (contanier(annos,Select.class)) {
                    Select select = m.getAnnotation(Select.class);
                    if (m.getReturnType().getSimpleName().equals("List")) {
                        // 获得List上的泛型类型
                        Class resultClz;
                        Type type = ((ParameterizedType)m.getGenericReturnType()).getActualTypeArguments()[0];
                        if (type.getTypeName().equals("T")) {
                            resultClz = (Class) ((ParameterizedType)(clz.getGenericInterfaces()[0])).getActualTypeArguments()[0];
                        }
                        else {
                            resultClz = (Class) type;
                        }
                        return selectAll(select.value(),a,resultClz);
                    }
                    Class resultClz = m.getReturnType();
                    if (m.getGenericReturnType().getTypeName().equals("T")) {
                        resultClz = (Class) ((ParameterizedType)(clz.getGenericInterfaces()[0])).getActualTypeArguments()[0];
                    }
                    return select(select.value(),a,resultClz);
                }
                else if (contanier(annos,Delete.class)) {
                    Delete delete = m.getAnnotation(Delete.class);
                    // 获取接口上的泛型类型
                    Class delClz = (Class) ((ParameterizedType)clz.getGenericInterfaces()[0]).getActualTypeArguments()[0];
                    return delete(delete.value(),a,delClz);
                }
                return null;
            });

            repertory.put(clz,baseDao);
        });
    }

    private boolean contanier(Annotation[] annos,Class clz) {
        return Arrays.stream(annos).anyMatch(a -> a.annotationType() == clz);
    }

    private List<String> getParamNamesBySql(String sql) {
        List<String> paramNames = new ArrayList<>();

        boolean flag = false;
        StringBuilder sb = new StringBuilder();
        for (char ch : sql.toCharArray()) {
            if (flag) {
                sb.append(ch);
            }
            if (ch == '{') {
                flag = true;
            }
            if (ch == '}') {
                flag = false;
                paramNames.add(sb.toString());
                sb.delete(0,sb.length());
            }
        }

        return paramNames;
    }

    private int executeSql(String sql,List<Object> param) throws SQLException {
        LOG.debug("execute sql: {}",sql);

        // 执行 sql
        Connection conn = transactionManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < param.size(); i ++) {
            statement.setObject(i+1,param.get(i));
        }

        return statement.executeUpdate();
    }

    /**
     * 插入操作
     * @param sql sql语句
     * @param params 参数
     * @return 影响行数
     */
    private int insert(String sql,Object[] params,boolean insertKey) throws SQLException, NoSuchFieldException, IllegalAccessException {

        String exeSql = sql;
        List<Object> paramList = new ArrayList<>();

        if ("".equals(sql)) {
            if (params == null || params.length != 1) {
                throw new DBParamterException("数据库插入操作参数错误！");
            }
            // 数据库实体类
            Object param = params[0];
            // 实体类的所有字段名和值
            Map<String,Object> paramMap = ClassUtil.getClassFieldNamesAndValues(param);

            // 实体类的表注解
            Table table = param.getClass().getAnnotation(Table.class);
            // 获得表名
            String tableName = StringUtil.humpToUnderline(param.getClass().getSimpleName());
            if (table != null) {
                tableName = table.value();
            }

            // 构建sql
            StringBuilder resultSql = new StringBuilder("INSERT INTO");
            resultSql.append(" `" + tableName + "`");

            StringBuilder nameList = new StringBuilder("(");
            StringBuilder valueList = new StringBuilder("VALUE(");
            for (Map.Entry<String,Object> entry : paramMap.entrySet()) {
                if (entry.getValue() != null) {
                    nameList.append("`" + StringUtil.humpToUnderline(entry.getKey()) + "`,");
                    valueList.append("?,");
                    paramList.add(entry.getValue());
                }
            }
            nameList.deleteCharAt(nameList.length() - 1);
            valueList.deleteCharAt(valueList.length() - 1);
            nameList.append(")");
            valueList.append(")");

            resultSql.append(nameList.toString());
            resultSql.append(" " + valueList.toString() + ";");

            exeSql = resultSql.toString();
        }
        else {
            if (sql.contains("?")) {
                if (params == null || params.length == 0) {
                    throw new DBParamterException("数据库插入操作参数错误！");
                }
                paramList.addAll(Arrays.stream(params).collect(Collectors.toList()));
            }
            else {
                if (params == null || params.length != 1) {
                    throw new DBParamterException("数据库插入操作参数错误！");
                }

                Map<String,Object> paramMap = ClassUtil.getClassFieldNamesAndValues(params[0]);
                List<String> paramNames = getParamNamesBySql(sql);
                for (String name : paramNames) {
                    paramList.add(paramMap.get(name));
                    exeSql = exeSql.replaceFirst("\\{" + name + "}","?");
//                    exeSql = exeSql.replace("{" + name + "}","?");
                }
            }
        }

        LOG.debug("execute sql: {}", exeSql);

        // 执行 sql
        Connection conn = transactionManager.getConnection();
        PreparedStatement statement;
        if (insertKey) {
            statement = conn.prepareStatement(exeSql,Statement.RETURN_GENERATED_KEYS);
        }
        else {
            statement = conn.prepareStatement(exeSql);
        }
        for (int i = 0; i < paramList.size(); i++) {
            statement.setObject(i + 1, paramList.get(i));
        }

        int result = statement.executeUpdate();

        if (insertKey && result > 0) {
            String idName = getIdName(params[0].getClass());
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                Object keyValue = resultSet.getObject(1);
                if (keyValue instanceof BigInteger) {
                    keyValue = ((BigInteger) keyValue).longValue();
                }
                ClassUtil.setValue(params[0],StringUtil.underlineToHump(idName),keyValue);
            }
        }

        return result;
    }

    /**
     * 修改操作
     * @param sql sql语句
     * @param params 参数
     * @return 影响行数
     */
    private int update(String sql,Object[] params) throws SQLException {

        String updateSql = sql;
        List<Object> paramValues = new ArrayList<>();

        if ("".equals(sql)) {
            if (params == null || params.length != 1) {
                throw new DBParamterException("数据库修改操作参数错误！");
            }

            Object param = params[0];
            // 需要将ID特殊标识出来
            Map<String,Object> fields = ClassUtil.getClassFieldNamesAndValues(param);
            // 表名
            String tableName = StringUtil.humpToUnderline(param.getClass().getSimpleName());
            Table table = param.getClass().getAnnotation(Table.class);
            if (table != null) {
                tableName = table.value();
            }

            // 构造sql语句
            String idName = getIdName(param.getClass());
            Object idValue = null;
            StringBuilder resultSql = new StringBuilder("UPDATE");
            resultSql.append(" `" + tableName + "` SET ");
            for (Map.Entry<String,Object> entry : fields.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if (key.equals(idName)) {
                    idValue = value;
                } else {
                    resultSql.append("`" + StringUtil.humpToUnderline(key) + "`= ?,");
                    paramValues.add(value);
                }
            }
            resultSql.deleteCharAt(resultSql.length() - 1);
            resultSql.append(" WHERE `" + idName + "` = ?;");
            paramValues.add(idValue);
            updateSql = resultSql.toString();
        }
        else {
            if (sql.contains("?")) {
                paramValues.addAll(Arrays.stream(params).collect(Collectors.toList()));
            }
            else {
                if (params == null || params.length != 1) {
                    throw new DBParamterException("数据库插入操作参数错误！");
                }

                List<String> paramNames = getParamNamesBySql(sql);
                Map<String,Object> data = ClassUtil.getClassFieldNamesAndValues(params[0]);
                for (String name : paramNames) {
                    updateSql = updateSql.replaceFirst("\\{"+name+"}","?");
//                    updateSql = updateSql.replace("{"+name+"}","?");
                    paramValues.add(data.get(name));
                }
            }
        }

        return executeSql(updateSql,paramValues);
    }

    /**
     * 查询操作
     * @param sql sql语句
     * @param params 参数
     * @param clz 返回类型
     * @return 返回查询数据
     */
    private Object select(String sql, Object[] params, Class clz) throws SQLException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {

        List<Object> paramValues = new ArrayList<>();
        String selectSql = sql;

        if ("".equals(sql)) {
            if (params == null || params.length != 1) {
                throw new DBParamterException("数据库查询操作参数错误！");
            }
            String tableName = StringUtil.humpToUnderline(clz.getSimpleName());
            Table table = (Table) clz.getAnnotation(Table.class);
            if (table != null) {
                tableName = table.value();
            }
            // 生成sql
            StringBuilder resultSql = new StringBuilder("SELECT ");
            List<String> fieldNames = ClassUtil.getClassFiledNames(clz);
            for (String fieldName : fieldNames) {
                resultSql.append("`" + StringUtil.humpToUnderline(fieldName) + "`,");
            }
            resultSql.deleteCharAt(resultSql.length() - 1);
            resultSql.append(" FROM `" + tableName + "` WHERE `" + getIdName(clz) + "` = ?;");
            selectSql = resultSql.toString();
            paramValues.add(params[0]);
        }
        else {
            if (sql.contains("?")) {
                paramValues.addAll(Arrays.stream(params).collect(Collectors.toList()));
            }
            else {
                if (params == null || params.length != 1) {
                    throw new DBParamterException("数据库查询操作参数错误！");
                }

                List<String> names = getParamNamesBySql(sql);
                Map<String,Object> nameValues = ClassUtil.getClassFieldNamesAndValues(params[0]);
                for (String name : names) {
                    selectSql = selectSql.replaceFirst("\\{" + name + "}","?");
//                    selectSql = selectSql.replace("{" + name + "}","?");
                    paramValues.add(nameValues.get(name));
                }
            }
        }

        LOG.debug("execute sql: {}",selectSql);

        Connection conn = transactionManager.getConnection();
        PreparedStatement statement = conn.prepareStatement(selectSql);
        for (int i = 0; i < paramValues.size(); i ++) {
            statement.setObject(i + 1,paramValues.get(i));
        }
        // 数据库结果集
        ResultSet resultSet = statement.executeQuery();
        // 获得结果集的元数据
        ResultSetMetaData metaData = resultSet.getMetaData();
        if (resultSet.next()) {
            Map<String,Object> data = new HashMap<>();
            for (int i = 0; i < metaData.getColumnCount(); i ++) {
                String fieldName = metaData.getColumnName(i + 1);
                Object value = resultSet.getObject(i + 1);
                data.put(StringUtil.underlineToHump(fieldName),value);
            }
            if (clz.getSimpleName().equals("Map")) {
                return data;
            }
            return ClassUtil.copyObject(data,clz);
        }

        return null;
    }

    /**
     * 查询列表
     * @param sql sql语句
     * @param clz 返回类型
     * @return 返回查询数据
     */
    @SuppressWarnings("all")
    private List<Object> selectAll(String sql,Object[] params,Class clz) throws SQLException, InvocationTargetException, IntrospectionException, InstantiationException, IllegalAccessException {

        List<Object> paramValues = new ArrayList<>();
        String selectSql = sql;

        if ("".equals(sql)) {
            String tableName = StringUtil.humpToUnderline(clz.getSimpleName());
            Table table = (Table) clz.getAnnotation(Table.class);
            if (table != null) {
                tableName = table.value();
            }
            // 生成sql
            List<String> fieldNames = ClassUtil.getClassFiledNames(clz);
            StringBuilder resultSql = new StringBuilder("SELECT ");
            for (String fieldName : fieldNames) {
                resultSql.append("`" + StringUtil.humpToUnderline(fieldName) + "`,");
            }
            resultSql.deleteCharAt(resultSql.length() - 1);
            resultSql.append(" FROM `" + tableName + "`;");
            selectSql = resultSql.toString();
        }
        else {
            if (sql.contains("?")) {
                paramValues.addAll(Arrays.stream(params).collect(Collectors.toList()));
            }
            else {
                if (params == null || params.length != 1) {
                    throw new DBParamterException("查询操作参数错误！");
                }

                Map<String,Object> fieldValues = ClassUtil.getClassFieldNamesAndValues(params[0]);
                List<String> fieldNames = getParamNamesBySql(sql);
                for (String fieldName : fieldNames) {
                    selectSql = selectSql.replaceFirst("\\{" + fieldName + "}","?");
//                    selectSql = selectSql.replace("{" + fieldName + "}","?");
                    paramValues.add(fieldValues.get(fieldName));
                }
            }
        }

        Connection conn = transactionManager.getConnection();

        LOG.debug("execute sql: {}",selectSql);

        Page page = null;

        if (PageHelper.isPage()) {
            String countSql = selectSql.replace(selectSql.substring(7,selectSql.indexOf("FROM") - 1),"COUNT(*)");
            PreparedStatement statement = conn.prepareStatement(countSql);
            ResultSet result = statement.executeQuery();
            int cols = 0;
            if (result.next()) {
                cols = result.getInt(1);
            }
            page = new Page(PageHelper.getPageNum(),PageHelper.getPageSize(),cols);
            selectSql = selectSql + " LIMIT " + page.getStartIndex() + ":" + page.getPageSize();
        }

        PreparedStatement statement = conn.prepareStatement(selectSql);

        for (int i = 0; i < paramValues.size(); i ++) {
            statement.setObject(i + 1,paramValues.get(i));
        }

        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        List list = new ArrayList();

        while (resultSet.next()) {
            Map<String,Object> data = new HashMap<>();
            for (int i = 0; i < metaData.getColumnCount(); i ++) {
                String name = metaData.getColumnName(i + 1);
                Object value = resultSet.getObject(i + 1);
                data.put(StringUtil.underlineToHump(name),value);
            }
            if (!clz.getSimpleName().equals("Map")) {
                list.add(ClassUtil.copyObject(data,clz));
            }
            else {
                list.add(data);
            }
        }

        if (PageHelper.isPage()) {
            page.setList(list);
            return page;
        }

        return list;
    }

    /**
     * 删除操作
     * @param sql sql语句
     * @param params 参数
     * @return 影响行数
     */
    private int delete(String sql,Object[] params,Class delClz) throws SQLException {

        String delSql = sql;
        List<Object> paramValues = new ArrayList<>();

        if ("".equals(sql)) {
            if (params == null || params.length != 1) {
                throw new DBParamterException("数据库删除参数错误！");
            }
            String tableName = StringUtil.humpToUnderline(delClz.getSimpleName());
            Table table = (Table) delClz.getAnnotation(Table.class);
            if (table != null) {
                tableName = table.value();
            }
            // 生成sql
            StringBuilder resultSql = new StringBuilder("DELETE FROM");
            resultSql.append(" `" + tableName + "` WHERE");
            resultSql.append(" `" + getIdName(delClz) + "` = ?;");
            delSql = resultSql.toString();
            paramValues.add(params[0]);
        }
        else {
            if (sql.contains("?")) {
                paramValues.addAll(Arrays.stream(params).collect(Collectors.toList()));
            }
            else {
                if (params == null || params.length != 1) {
                    throw new DBParamterException("数据库删除操作参数错误！");
                }
                List<String> parmaNames = getParamNamesBySql(sql);
                Map<String,Object> data = ClassUtil.getClassFieldNamesAndValues(params[0]);
                for (String name : parmaNames) {
                    delSql = delSql.replaceFirst("\\{"+name+"}","?");
//                    delSql = delSql.replace("{"+name+"}","?");
                    paramValues.add(data.get(name));
                }
            }
        }

        return executeSql(delSql,paramValues);
    }

    private String getIdName(Class clz) {
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                return StringUtil.humpToUnderline(field.getName());
            }
        }
        return "id";
    }
}
