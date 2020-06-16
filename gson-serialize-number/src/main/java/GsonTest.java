import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class GsonTest {

    public static void main(String[] args){

        // 构造一个 POJO 对象，为了使代码看起来更加简洁，没有使用 getter 和 setter（请勿模仿）
        Person person = new Person();
        person.name = "Bob";
        person.age = 23;
        person.other = new HashMap<String, Object>();
        person.other.put("int", 66);
        person.other.put("long", 126378436112387463L);

        Gson gson = new GsonBuilder().create();
        String jsonStr = gson.toJson(person);
        System.out.println(jsonStr); // 输出反序列化之后的结果
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

        person = gson.fromJson(jsonStr, Person.class); // 对 JSON 字符串反序列化
        jsonStr = gson.toJson(person); // 再对反序列化之后的对象序列化
        System.out.println(jsonStr); // 输出反序列化之后的结果
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

    }

    static class Person {
        String name;
        int age;
        Map<String, Object> other;
    }
}
