/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.typed;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Configuration;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class TypedService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME = "group";
	private static final String CONFIG_NAME = "bfh-grouped-typed";
	private static final String NON_GROUPED_MODE = "singleType";

	private static final Logger logger = Logger.getLogger(TypedService.class.getName());

	@EJB
	PostService postSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {

		Configuration p = this.configurationManager.getConfiguration(CONFIG_NAME);
		if (p == null) {
			logger.log(Level.SEVERE, "Configuration for component " + CONFIG_NAME + " is missing.");
			beta.add(Attributes.ERROR,
					new StringValue("BGT-003 This UniBoard instance is down due to a configuration error."));
			return beta;
		}

		if (p.getEntries().containsKey(NON_GROUPED_MODE)) {
			String schemaPath = p.getEntries().get(NON_GROUPED_MODE);

			if (this.validate(new String(message, Charset.forName("UTF-8")), schemaPath)) {
				return beta;
			} else {
				beta.add(Attributes.REJECTED, new StringValue("BGT-005 Message does not match the schema."));
				return beta;
			}
		} else {

			if (!alpha.containsKey(ATTRIBUTE_NAME)) {
				beta.add(Attributes.REJECTED, new StringValue("BGT-001 Missing required attribute: " + ATTRIBUTE_NAME));
				return beta;
			}
			if (!(alpha.getValue(ATTRIBUTE_NAME) instanceof StringValue)) {
				beta.add(Attributes.REJECTED, new StringValue("BGT-002 Required attribute: " + ATTRIBUTE_NAME
						+ " is not of type string."));
				return beta;
			}
			StringValue group = (StringValue) alpha.getValue(ATTRIBUTE_NAME);
			if (!p.getEntries().containsKey(group.getValue())) {
				beta.add(Attributes.REJECTED,
						new StringValue("BGT-004 Unknown " + ATTRIBUTE_NAME + ": " + group.getValue()));
				return beta;
			}
			String schemaPath = p.getEntries().get(group.getValue());

			if (this.validate(new String(message, Charset.forName("UTF-8")), schemaPath)) {
				return beta;
			} else {
				beta.add(Attributes.REJECTED, new StringValue("BGT-006 Message is not of type " + group.getValue()));
				return beta;
			}
		}
	}

	public boolean validate(String jsonData, String jsonSchema) {

		try {
			//JsonNode schemaNode = JsonLoader.fromPath(jsonSchema);
			JsonNode data = JsonLoader.fromString(jsonData);

			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonSchema schema = factory.getJsonSchema("file://" + jsonSchema);
			ProcessingReport report = schema.validate(data);

			return report.isSuccess();
		} catch (IOException | ProcessingException ex) {
			logger.log(Level.WARNING, "Can not validate message." + jsonData, ex);
			return false;
		}
	}
}
