import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GsonEnhancerTest {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        // 构造一个 POJO 对象，为了使代码看起来更加简洁，没有使用 getter 和 setter（请勿模仿）
        Person person = new Person();
        person.name = "Bob";
        person.age = 23;
        person.other = new HashMap<String, Object>();
        person.other.put("int", 66);
        person.other.put("long", 126378436112387463L);

        // 注册自定义的 ObjectTypeAdapter 工厂
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                if(Object.class == type.getRawType()){
                    return (TypeAdapter<T>)new ObjectTypeAdapter();
                }
                return null;
            }
        }).create();

        // 通过反射把原来的 com.google.gson.internal.bind.ObjectTypeAdapter.FACTORY 清除掉
        // 让自定义的 ObjectTypeAdapter 工厂生效
        Field factories = Gson.class.getDeclaredField("factories");
        factories.setAccessible(true);
        List<TypeAdapterFactory> typeAdapterFactories =  (List<TypeAdapterFactory>)factories.get(gson);
        ArrayList<TypeAdapterFactory> adapterFactories = new ArrayList<>(typeAdapterFactories);
        adapterFactories.remove(com.google.gson.internal.bind.ObjectTypeAdapter.FACTORY);
        factories.set(gson, adapterFactories);

        String jsonStr = gson.toJson(person);   // 序列化
        System.out.println(jsonStr);
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

        person = gson.fromJson(jsonStr, Person.class); // 反序列化
        jsonStr = gson.toJson(person); // 序列化
        System.out.println(jsonStr); // 输出反序列化之后的结果
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

    }

    static class Person {
        String name;
        int age;
        Map<String, Object> other;
    }

    static class ObjectTypeAdapter extends TypeAdapter<Object> {

        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    String numberStr = in.nextString();
                    if(numberStr.contains(".")){
                        return Double.valueOf(numberStr);
                    }else {
                        return Long.valueOf(numberStr);
                    }
                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        public void write(JsonWriter out, Object value) throws IOException {
            if(value == null){
                out.nullValue();
            }else {
                out.beginObject();
                out.endObject();
            }
        }
    }

}
