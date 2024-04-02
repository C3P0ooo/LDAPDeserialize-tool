//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.io.Serializable;

public abstract class BaseJsonNode extends JsonNode implements Serializable {
    private static final long serialVersionUID = 1L;

//    Object writeReplace() {
//        return NodeSerialization.from(this);
//    }

    protected BaseJsonNode() {
    }

    public final JsonNode findPath(String fieldName) {
        JsonNode value = this.findValue(fieldName);
        return (JsonNode)(value == null ? MissingNode.getInstance() : value);
    }

    public abstract int hashCode();

    public JsonNode required(String fieldName) {
        return (JsonNode)this._reportRequiredViolation("Node of type `%s` has no fields", new Object[]{this.getClass().getSimpleName()});
    }

    public JsonNode required(int index) {
        return (JsonNode)this._reportRequiredViolation("Node of type `%s` has no indexed values", new Object[]{this.getClass().getSimpleName()});
    }

    public JsonParser traverse() {
        return new TreeTraversingParser(this);
    }

    public JsonParser traverse(ObjectCodec codec) {
        return new TreeTraversingParser(this, codec);
    }

    public abstract JsonToken asToken();

    public JsonParser.NumberType numberType() {
        return null;
    }

    public abstract void serialize(JsonGenerator var1, SerializerProvider var2) throws IOException;

    public abstract void serializeWithType(JsonGenerator var1, SerializerProvider var2, TypeSerializer var3) throws IOException;

    public String toString() {
        return InternalNodeMapper.nodeToString(this);
    }

    public String toPrettyString() {
        return InternalNodeMapper.nodeToPrettyString(this);
    }
}
