package net.pl3x.map.task;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.RenderManager;
import net.pl3x.map.data.Image;
import net.pl3x.map.data.Region;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.SpiralIterator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class FullRender extends BukkitRunnable {
    private final World world;
    private final File directory;
    private final DecimalFormat df = new DecimalFormat("00.00%");
    private int maxRadius = 0;

    private int total, current;

    public FullRender(World world) {
        this.world = world;
        this.directory = new File(new File(Pl3xMap.getInstance().getDataFolder(), "tiles"), world.getName());
        if (!directory.exists() && directory.mkdirs()) {
            Pl3xMap.log().severe("Could not create tiles directory!");
        }
    }

    @Override
    public void cancel() {
        RenderManager.finish(world);
        super.cancel();
    }

    @Override
    public void run() {
        Pl3xMap.log().info("§3Started map render for §e" + world.getName());

        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            Pl3xMap.log().severe("Unable to write to " + directory.getAbsolutePath());
            e.printStackTrace();
            return;
        }

        Pl3xMap.log().info("§eScanning region files...");
        List<Region> regions = getRegions();
        total = regions.size();
        Pl3xMap.log().info("§aFound §7" + total + " §aregion files");

        SpiralIterator spiral = new SpiralIterator(0, 0, maxRadius + 1);
        while (spiral.hasNext()) {
            Region region = spiral.next();
            if (regions.contains(region)) {
                mapRegion(region);
            }
        }

        Pl3xMap.log().info("§3Finished rendering map for §e" + world.getName());
        cancel();
    }

    private List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        File[] files = FileUtil.getRegionFiles(world);
        for (File file : files) {
            if (file.length() == 0) continue;
            try {
                String[] split = file.getName().split("\\.");
                int x = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                Region region = new Region(x, z);
                maxRadius = Math.max(Math.max(maxRadius, Math.abs(x)), Math.abs(z));
                regions.add(region);
            } catch (NumberFormatException ignore) {
            }
        }
        return regions;
    }

    private void mapRegion(Region region) {
        Pl3xMap.log().info("§3" + progress() + " §eScanning region " + region.getX() + "," + region.getZ());
        Image image = new Image();
        int rendered = 0;
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                Chunk chunk = world.getChunkAtAsync(region.getChunkX() + x, region.getChunkZ() + z, false, true).join();
                if (chunk != null) {
                    rendered++;
                    mapChunk(chunk, image);
                }
            }
        }
        current++;
        if (rendered > 0) {
            Pl3xMap.log().info("       §aSaving " + rendered + " chunks for region " + region.getX() + "," + region.getZ());
            image.save(region, directory);
        } else {
            Pl3xMap.log().info("       §cRegion is empty. Skipping. " + region.getX() + "," + region.getZ());
        }
    }

    private void mapChunk(Chunk chunk, Image image) {
        int cX = chunk.getX() << 4;
        int cZ = chunk.getZ() << 4;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int bX = cX + x;
                int bZ = cZ + z;
                image.setPixel(bX, bZ, Colors.getColor(chunk, bX, bZ));
            }
        }
    }

    private String progress() {
        return df.format((double) current / total);
    }
}