package io.miscellanea.vertx.example;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigStoreHelper {
    // Fields
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigStoreHelper.class);

    // Constructors
    private ConfigStoreHelper(){

    }

    public static ConfigRetrieverOptions buildDefaultRetrieverOptions(String pathToConfigFile){
        assert pathToConfigFile != null : "pathToConfigFile must not be null.";

        LOGGER.debug("Building default configuration retriever options for config file '{}'.",pathToConfigFile);

        var defaultStore =
                new ConfigStoreOptions()
                        .setType("file")
                        .setFormat("json")
                        .setConfig(new JsonObject().put("path", pathToConfigFile));
        var systemPropsStore = new ConfigStoreOptions().setType("sys");
        var envVarStore = new ConfigStoreOptions().setType("env");

        var configRetrieverOpts =
                new ConfigRetrieverOptions()
                        .addStore(defaultStore)
                        .addStore(envVarStore)
                        .addStore(systemPropsStore);

        LOGGER.debug("");

        return configRetrieverOpts;
    }
}
