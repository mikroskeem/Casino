package eu.mikroskeem.mod.casino;

import eu.mikroskeem.mod.casino.config.CasinoConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Mark Vainomaa
 */
class CasinoConfigurationLoader {
    private final ConfigurationLoader<ConfigurationNode> loader;
    private final ObjectMapper<CasinoConfiguration>.BoundInstance configurationMapper;
    private ConfigurationNode baseNode = null;
    @Getter private CasinoConfiguration configuration = null;

    @SneakyThrows({IOException.class, ObjectMappingException.class})
    CasinoConfigurationLoader(Casino plugin) {
        Path configurationPath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "config.yml");
        Files.createDirectories(configurationPath.getParent());
        loader = YAMLConfigurationLoader
                .builder()
                .setDefaultOptions(ConfigurationOptions.defaults().setShouldCopyDefaults(true))
                .setHeaderMode(HeaderMode.PRESERVE)
                .setPath(configurationPath)
                .build();
        configurationMapper = ObjectMapper.forClass(CasinoConfiguration.class).bindToNew();
        configuration = configurationMapper.getInstance();
    }

    @SneakyThrows({IOException.class, ObjectMappingException.class})
    void load() {
        baseNode = loader.load();
        configuration = configurationMapper.populate(baseNode.getNode("casino"));
    }

    @SneakyThrows({IOException.class, ObjectMappingException.class})
    void save() {
        configurationMapper.serialize(baseNode.getNode("casino"));
        loader.save(baseNode);
    }
}
