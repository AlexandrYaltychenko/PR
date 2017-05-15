package lab6;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexandr on 14.05.17.
 */
public class Message {
    private JSONObject message;
    private String type;

    public String getParam(String param){
        return (String)message.get(param);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString(){
        return message.toString();
    }

    private Message(Builder builder){
        message = new JSONObject();
        this.type = builder.type;
        message.put("type",builder.type);
        for (String key: builder.params.keySet())
            message.put(key,builder.params.get(key));
    }

    public Message(String jsonString) throws ParseException{
        this.message = (JSONObject) new JSONParser().parse(jsonString);
        this.type = (String) message.get("type");
    }

    public static class Builder {
        private String type;
        private Map<String, String> params;
        public Builder(String type){
            this.type = type;
            this.params = new HashMap<>();
        }
        public Builder addParam(String name, String value){
            params.put(name, value);
            return this;
        }
        public Message build(){
            return new Message(this);
        }
    }
}
