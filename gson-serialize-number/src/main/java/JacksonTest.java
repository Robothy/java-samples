import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JacksonTest {

    public static void main(String[] args) throws JsonProcessingException {

        Person person = new Person();
        person.name = "Bob";
        person.age = 23;
        person.other = new HashMap<String, Object>();
        person.other.put("int", 66);
        person.other.put("long", 126378436112387463L);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(person); // 序列化
        System.out.println(jsonStr);
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

        person = objectMapper.readValue(jsonStr, Person.class); // 反序列化
        jsonStr = objectMapper.writeValueAsString(person);
        System.out.println(jsonStr);
        System.out.println(person.other.get("int").getClass());
        System.out.println(person.other.get("long").getClass());

    }

    static class Person {
        String name;
        int age;
        Map<String, Object> other;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Map<String, Object> getOther() {
            return other;
        }

    }

}
