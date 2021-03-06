package com.grizz;

import com.grizz.generators.Generator;
import com.grizz.generators.GeneratorManager;
import com.grizz.nms.api.Handler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Gbtank.
 */
public class EggWars extends JavaPlugin implements PluginMessageListener {

    @Getter private String prefix;

    @Getter private FileConfiguration messenger;

    @Getter private String arenaDir;
    @Getter private String baseDir;
    @Getter private String genDir;

    // Singleton Structure (Needs to have public constructor for Bukkit enabling)

    private static EggWars ew = null;

    public static EggWars get() {
        return ew;
    }

    // TODO: Move commands into neater packages

    // TODO: Remove test code once done!
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("test") && player.hasPermission("eggwars.test")) {
            System.out.println(getDataFolder().getAbsolutePath());
            Generator gen = GeneratorManager.get().createFromFile("/generators/example_gen.yml");
            gen.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.SANDSTONE);
            gen.tryStart();
        }
        try {
            Handler handler = (Handler) Class.forName("com.grizz.nms.v1_8_R2.NMSHandler").newInstance();
            handler.displayActionBar(player, "Hello!");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    // TODO: Generate arena example!
    public void onEnable() {
        ew = this;

        saveDefaultConfig();

        this.arenaDir = getConfig().getString("arena_dir");
        this.baseDir = getConfig().getString("base_dir");
        this.genDir = getConfig().getString("gen_dir");

        File arenas = new File(getDataFolder().getAbsolutePath() + "/arenas/");
        File base = new File(getDataFolder().getAbsolutePath() + "/base/");
        File gens = new File(getDataFolder().getAbsolutePath() + "/generators/");

        if(!arenas.exists() && !base.exists() && !gens.exists()) {
            try {
                copyResource("examples/example_arena.yml", this.getDataFolder().getAbsolutePath() + arenaDir + "examples/example_arena.yml");
                copyResource("examples/example_base.yml", this.getDataFolder().getAbsolutePath() + baseDir + "examples/example_base.yml");
                copyResource("examples/example_gen.yml", this.getDataFolder().getAbsolutePath() + genDir + "examples/example_gen.yml");
                copyResource("generators/gen_sign.yml", this.getDataFolder().getAbsolutePath() + "/gen_sign.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!arenas.exists()) arenas.mkdir();
        if(!base.exists()) base.mkdir();
        if(!gens.exists()) gens.mkdir();

        messenger = YamlConfiguration.loadConfiguration(new File("messages.yml"));
        prefix = messenger.getString("prefix");
    }

    public void onDisable() {
        for(Generator gen : GeneratorManager.get().getGenerators()) {
            Bukkit.getScheduler().cancelTask(gen.getRunId());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // TODO: Configure BungeeCord
    }

    public void copyResource(String originPath, String finalPath) throws IOException {
        File finalFile = new File(getDataFolder() + finalPath);
        if(!finalFile.exists()) {
            InputStream finalIn = null;
            FileOutputStream finalOut = null;

            try {
                finalFile.createNewFile();
                finalIn = getResource(originPath);
                finalOut = new FileOutputStream(finalFile);

                int c;
                while ((c = finalIn.read()) != -1) finalOut.write(c);

            } finally {
                if (finalIn != null) finalIn.close();
                if(finalOut != null) finalOut.close();
            }
        }
    }

}
