package mc.recraftors.dumpster.utils.accessors;

import com.google.gson.JsonObject;
import mc.recraftors.dumpster.utils.Objectable;

public interface IObjectable extends Objectable {
    JsonObject dumpster$toJson();

    default JsonObject toJson(){
        return this.dumpster$toJson();
    }
}
