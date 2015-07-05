package org.revenj.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.revenj.patterns.Serialization;
import org.revenj.patterns.ServiceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

class JsonSerialization implements Serialization<String> {

	private final ObjectMapper mapper;

	public JsonSerialization(ServiceLocator locator) {
		mapper = new ObjectMapper()
				.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setInjectableValues(new InjectableValues.Std().addValue("__locator", locator))
				.registerModule(new Jdk8Module())
				.registerModule(new JSR310Module());
	}

	byte[] serializeToBytes(Object value) throws IOException {
		return mapper.writeValueAsBytes(value);
	}

	void serializeTo(Object value, OutputStream stream) throws IOException {
		mapper.writeValue(stream, value);
	}

	@Override
	public String serialize(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	Object deserialize(Type type, byte[] content, int length) throws IOException {
		JavaType javaType = mapper.getTypeFactory().constructType(type);
		return mapper.readValue(content, 0, length, javaType);
	}

	Object deserialize(Type type, InputStream stream) throws IOException {
		JavaType javaType = mapper.getTypeFactory().constructType(type);
		return mapper.readValue(stream, javaType);
	}

	@Override
	public Object deserialize(Type type, String input) throws IOException {
		JavaType javaType = mapper.getTypeFactory().constructType(type);
		return mapper.readValue(input, javaType);
	}
}
