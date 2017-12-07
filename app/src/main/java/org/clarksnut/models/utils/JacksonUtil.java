package org.clarksnut.models.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class JacksonUtil {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T fromString(String string, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(string, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given string value: " + string + " cannot be transformed to Json object");
        }
    }

    public static String toString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The given Json object value: " + value + " cannot be transformed to a String");
        }
    }

    public static JsonNode toJsonNode(String value) {
        try {
            return OBJECT_MAPPER.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode merge(JsonNode target, JsonNode source) {
        if (target instanceof ArrayNode && source instanceof ArrayNode) {
            // Both the target and source are array nodes, then append the
            // contents of the source array to the target array without
            // performing a deep copy. This is safe because either:
            // 1) Successive merges are also arrays and are appended.
            // 2) Successive merge(s) are not arrays and replace the array.
            // Note: this assumes that the target node is modifiable.
            ((ArrayNode) target).addAll((ArrayNode) source);
            return target;
        } else if (target instanceof ObjectNode && source instanceof ObjectNode) {
            // Both the target and source are object nodes, then recursively
            // merge the fields of the source node over the same fields in
            // the target node. Any unmatched fields from the source node are
            // simply added to the target node; this requires a deep copy
            // since subsequent merges may modify it.
            final Iterator<Map.Entry<String, JsonNode>> iterator = source.fields();
            while (iterator.hasNext()) {
                final Map.Entry<String, JsonNode> sourceFieldEntry = iterator.next();
                final JsonNode targetFieldValue = target.get(sourceFieldEntry.getKey());
                if (targetFieldValue != null) {
                    // Recursively merge the source field value into the target
                    // field value and replace the target value with the result.
                    final JsonNode newTargetFieldValue = merge(targetFieldValue, sourceFieldEntry.getValue());
                    ((ObjectNode) target).set(sourceFieldEntry.getKey(), newTargetFieldValue);
                } else {
                    // Add a deep copy of the source field to the target.
                    ((ObjectNode) target).set(sourceFieldEntry.getKey(), sourceFieldEntry.getValue().deepCopy());
                }
            }
            return target;
        } else {
            // The target and source nodes are of different types. Replace the
            // target node with the source node. This requires a deep copy of
            // the source node since subsequent merges may modify it.
            return source.deepCopy();
        }
    }

    public static JsonNode override(JsonNode target, JsonNode source) {
        target = target.deepCopy();
        source = source.deepCopy();
        if (target instanceof ObjectNode && source instanceof ObjectNode) {
            final Iterator<Map.Entry<String, JsonNode>> iterator = source.fields();
            while (iterator.hasNext()) {
                final Map.Entry<String, JsonNode> sourceFieldEntry = iterator.next();
                ((ObjectNode) target).set(sourceFieldEntry.getKey(), sourceFieldEntry.getValue().deepCopy());
            }
            return target;
        }
        return source;
    }

}
