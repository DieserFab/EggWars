package com.grizz.generators;

import com.grizz.EggWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Gbtank.
 */
public class GeneratorManager {

    private EggWars ew;
    private Set<Generator> generators = new HashSet<>();

    // Singleton Structure

    private static GeneratorManager gm = null;

    public GeneratorManager(EggWars ew) {
        this.ew = ew;
        if(gm == null) {
            gm = this;
        }
    }

    public static GeneratorManager get() {
        return gm;
    }

    public Generator createFromFile(File file) {
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);

        Location location = new Location(
                Bukkit.getWorld(conf.getString("generator.world")),
                conf.getInt("generator.x"),
                conf.getInt("generator.y"),
                conf.getInt("generator.z"));
        int level = conf.getInt("generator.start_level");
        String basePath = conf.getString("generator.base_file");
        File base = new File(basePath.endsWith(".yml") ? basePath : basePath + ".yml");

        YamlConfiguration baseConf = YamlConfiguration.loadConfiguration(base);
        Generator gen =  new Generator(ew, location, new GeneratorSettings(base), new GeneratorLevel(level,
                baseConf.getInt("upgrades." + level + ".max_drops"),
                baseConf.getLong("upgrades." + level + ".ticks")));
        generators.add(gen);
        return gen;
    }

    public Generator getGeneratorByLocation(Location location) {
        for(Generator gen : generators) {
            if(gen.getLocation().equals(location)) {
                return gen;
            }
        }
        return null;
    }

}
