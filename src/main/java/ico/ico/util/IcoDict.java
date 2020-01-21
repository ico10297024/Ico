package ico.ico.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 数据字典
 * 用于保存数据集合，每个字典对象对应一个数据集合
 * 该字典提供用户可以通过数据对象任一一个字段的值获取相应值所在对象
 * ----------------------------------我是分隔符--------------------------------------
 * 特点：
 * 数据唯一性，可以设置字段索引，通过字段索引来搜索对象
 * ----------------------------------我是分隔符--------------------------------------
 * 使用方式：
 * class Entity{id1,id2}
 * Entity en1(1,2),en2(3,4)
 * new EntityDict<Entity>("id1","id2");
 * IcoDict.addT(en1,en2)
 * IcoDict.get("id1",1)==en1
 * IcoDict.get("id1",2)==null
 * IcoDict.get("id1",3)==en2
 * IcoDict.get("id1",2)==null
 * IcoDict.get("id2",1)==null
 * IcoDict.get("id2",2)==en1
 * IcoDict.get("id2",3)==null
 * IcoDict.get("id2",2)==en2
 */
public class IcoDict<T> extends ArrayList<T> {
    private Class clazz;

    private String[] uniqueFields;//唯一索引字段
    private String[] groupFields;//分组索引字段
    /**
     * 根据唯一索引进行分组存放的字典集
     * 以下称为唯一索引字典集
     */
    private LinkedHashMap<String, LinkedHashMap<Object, T>> uniqueMap = new LinkedHashMap<>();
    /**
     * 根据分组索引进行分组存放的字典集
     * 以下称为分组索引字典集
     */
    private LinkedHashMap<String, LinkedHashMap<Object, ArrayList<T>>> groupMap = new LinkedHashMap<>();

    public IcoDict(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * 设置唯一索引字段
     */
    public void setUnique(String... _uniqueFields) {
        uniqueFields = _uniqueFields;
        //创建每个唯一索引字段所对应的字典集
        for (int i = 0; i < uniqueFields.length; i++) {
            LinkedHashMap<Object, T> map = new LinkedHashMap<>();
            uniqueMap.put(uniqueFields[i], map);
        }
    }

    /**
     * 设置分组索引字段
     */
    public void setGroup(String... _groupFields) {
        groupFields = _groupFields;
        //创建每个分组索引字段所对应的字典集
        for (int i = 0; i < groupFields.length; i++) {
            LinkedHashMap<Object, ArrayList<T>> map = new LinkedHashMap<>();
            groupMap.put(groupFields[i], map);
        }
    }

    /**
     * 初始化字典集
     */
    public void initDict() throws NoSuchFieldException, IllegalAccessException {
        //清空数据字典集
        clearDict();
        //分析当前集合中所有数据，将数据存入字典集中
        for (int i = 0; i < super.size(); i++) {
            analysisT(get(i));
        }
    }

    /**
     * 清空数据字典集
     */
    public void clearDict() {
        /*重置两个字典集*/
        //根据唯一索引进行分组存放的字典集
        uniqueMap = new LinkedHashMap<>();
        //根据分组索引进行分组存放的字典集
        groupMap = new LinkedHashMap<>();
        /*重新设置索引字段*/
        setUnique(uniqueFields);
        setGroup(groupFields);
    }

    /**
     * 插入一个数据,并分析该数据，将该数据插入到字典集中
     *
     * @param t
     */
    public void addT(T t) throws NoSuchFieldException, IllegalAccessException {
        if (super.contains(t)) {
            return;
        }
        analysisT(t);
        super.add(t);
    }

    /**
     * 解析t，然后将t插入到字典集中
     * 该方法一般和{@link #add}一起用，否则只在字典中存在t对象，而自身集合中不存在
     *
     * @param t
     */
    public void analysisT(T t) throws NoSuchFieldException, IllegalAccessException {
        /*唯一字典集*/
        for (int i = 0; i < uniqueFields.length; i++) {
            //获取索引字段名称
            String fieldName = uniqueFields[i];
            //获取索引字段对应的字典集
            LinkedHashMap<Object, T> map = uniqueMap.get(fieldName);
            //获取数据中索引字段的值
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(t);
            //在字典集中若不存在，则存入
            if (map.get(value) == null) {
                map.put(value, t);
            }
        }
        /*分组字典集*/
        for (int i = 0; i < groupFields.length; i++) {
            //获取索引字段名称
            String fieldName = groupFields[i];
            //获取索引字段对应的字典集
            LinkedHashMap<Object, ArrayList<T>> map = groupMap.get(fieldName);
            //获取数据中索引字段的值
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(t);
            //获取字典集中的集合列表
            ArrayList<T> list = new ArrayList<>();
            if (map.get(value) != null) {
                list = map.get(value);
            }
            //集合列表中若不存在t，则存入
            if (!list.contains(t)) {
                list.add(t);
            }
            //存入字典集中
            map.put(value, list);
        }
    }

    /**
     * 插入一组数据,返回插入失败的数据集合
     *
     * @param list
     * @return List<T>
     */
    public void addList(List<T> list) throws NoSuchFieldException, IllegalAccessException {
        for (T t : list) {
            addT(t);
        }
    }

    /**
     * 通过地址比较的方式删除一个数据对象
     * 这里的地址比较方式仅限于删除数据集，对于索引对集的删除始终使用值比较的方式
     *
     * @param t
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public void removeT(T t) throws NoSuchFieldException, IllegalAccessException {
        if (super.contains(t)) {
            return;
        }
        /*唯一字典集*/
        for (int i = 0; i < uniqueFields.length; i++) {
            //获取索引字段名称
            String fieldName = uniqueFields[i];
            //获取索引字段对应的字典集
            LinkedHashMap<Object, T> map = uniqueMap.get(fieldName);
            //获取数据中索引字段的值
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(t);
            //删除字典集中的数据
            map.remove(value);
        }
        /*分组字典集*/
        for (int i = 0; i < groupFields.length; i++) {
            //获取索引字段名称
            String fieldName = groupFields[i];
            //获取索引字段对应的字典集
            LinkedHashMap<Object, ArrayList<T>> map = groupMap.get(fieldName);
            //获取数据中索引字段的值
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(t);
            //获取字典集中的集合列表
            ArrayList<T> list = new ArrayList<>();
            if (map.get(value) != null) {
                list = map.get(value);
            }
            //集合列表中若存在t，则删除
            list.remove(value);
        }
        super.remove(t);
    }


    /**
     * 删除数据
     *
     * @param list
     */
    public void removeList(List<T> list) throws NoSuchFieldException, IllegalAccessException {
        for (T t : list) {
            removeT(t);
        }
    }


    public LinkedHashMap<String, LinkedHashMap<Object, T>> getUniqueMap() {
        return uniqueMap;
    }

    public void setUniqueMap(LinkedHashMap<String, LinkedHashMap<Object, T>> uniqueMap) {
        this.uniqueMap = uniqueMap;
    }

    public LinkedHashMap<String, LinkedHashMap<Object, ArrayList<T>>> getGroupMap() {
        return groupMap;
    }

    public void setGroupMap(LinkedHashMap<String, LinkedHashMap<Object, ArrayList<T>>> groupMap) {
        this.groupMap = groupMap;
    }
}
