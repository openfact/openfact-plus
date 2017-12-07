package org.clarksnut.models.db.type.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.clarksnut.models.utils.JacksonUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class JsonNodeType extends ImmutableType<JsonNode> {

    public JsonNodeType() {
        super(JsonNode.class);
    }

    @Override
    protected JsonNode get(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(names[0]);
        return (value != null && value.length() > 0) ? JacksonUtil.toJsonNode(value) : null;
    }

    @Override
    protected void set(PreparedStatement st, JsonNode value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.NVARCHAR);
        } else {
            st.setString(index, JacksonUtil.toString(value));
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.NVARCHAR};
    }

}
