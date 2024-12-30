package com.alpsbte.alpslib.libpsterra.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.PasteBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for WorldEdit operations and region handling using reflection to support all WorldEdit versions from 1.12 to 1.21 <br>
 * <br>
 * The following WorldEdit methods are supported: <br>
 * • {@link Region#getMinimumPoint()} ➔ {@link #getRegionMinimumPoint(Region)} <br>
 * • {@link Region#getMaximumPoint()} ➔ {@link #getRegionMaximumPoint(Region)} <br>
 * • {@link Region#contains(BlockVector3)} ➔ {@link #regionContains(Region, Vector)} <br>
 * • {@link Region#getCenter()} ➔ {@link #getRegionCenter(Region)} <br>
 * • {@link Clipboard#getOrigin()} ➔ {@link #getClipboardOrigin(Clipboard)} <br>
 * • {@link Polygonal2DRegion#getPoints()} ➔ {@link #getRegionPoints(Polygonal2DRegion)} <br>
 * • {@link Region#polygonize(int)} ➔ {@link #polygonizeRegion(Region, int)} <br>
 * • {@link CylinderRegion#CylinderRegion()} ➔ {@link #createCylinderRegion(Region, Vector, int, int, int)} <br>
 * • {@link #pasteSchematic(File, File, World, Vector, double, boolean)} <br>
 * • {@link #saveSchematic(Region, File)} <br>
 * <br>
 * @author MineFact
 */
public class WorldEditUtil {

    /**
     * Get the minimum point of a region <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@link Region#getMinimumPoint()} -> {@code com.sk89q.worldedit.Vector} <br>
     * > 1.13: {@link Region#getMinimumPoint()} -> {@link com.sk89q.worldedit.math.BlockVector3} <br>
     * <br>
     * @param region Region to get the minimum point from
     * @return Minimum point of the region
     */
    public static Vector getRegionMinimumPoint(Region region){
        try {
            Class<?> regionClass = region.getClass();
            Method getMinimumPointMethod = regionClass.getMethod("getMinimumPoint");

            Class<?> vectorClass = getMinimumPointMethod.getReturnType();

            Method getXMethod = vectorClass.getMethod("getX");
            Method getYMethod = vectorClass.getMethod("getY");
            Method getZMethod = vectorClass.getMethod("getZ");

            Object minimumPoint = getMinimumPointMethod.invoke(region);

            // Vector class returns double values in 1.12 and BlockVector class returns integer values in 1.13+
            if(VersionUtil.is_1_12()) {
                double x = (Double) getXMethod.invoke(minimumPoint);
                double y = (Double) getYMethod.invoke(minimumPoint);
                double z = (Double) getZMethod.invoke(minimumPoint);

                return new Vector(x, y, z);

            }else{
                int x = (Integer) getXMethod.invoke(minimumPoint);
                int y = (Integer) getYMethod.invoke(minimumPoint);
                int z = (Integer) getZMethod.invoke(minimumPoint);

                return new Vector(x, y, z);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get the maximum point of a region <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@link Region#getMinimumPoint()} -> {@code com.sk89q.worldedit.Vector} <br>
     * > 1.13: {@link Region#getMinimumPoint()} -> {@link com.sk89q.worldedit.math.BlockVector3} <br>
     * <br>
     * @param region Region to get the maximum point from
     * @return Maximum point of the region
     */
    public static Vector getRegionMaximumPoint(Region region){
        try {
            Class<?> regionClass = region.getClass();
            Method getMaximumPointMethod = regionClass.getMethod("getMaximumPoint");

            Class<?> vectorClass = getMaximumPointMethod.getReturnType();

            Method getXMethod = vectorClass.getMethod("getX");
            Method getYMethod = vectorClass.getMethod("getY");
            Method getZMethod = vectorClass.getMethod("getZ");

            Object maximumPoint = getMaximumPointMethod.invoke(region);

            // Vector class returns double values in 1.12 and BlockVector class returns integer values in 1.13+
            if(VersionUtil.is_1_12()) {
                double x = (Double) getXMethod.invoke(maximumPoint);
                double y = (Double) getYMethod.invoke(maximumPoint);
                double z = (Double) getZMethod.invoke(maximumPoint);

                return new Vector(x, y, z);

            }else{
                int x = (Integer) getXMethod.invoke(maximumPoint);
                int y = (Integer) getYMethod.invoke(maximumPoint);
                int z = (Integer) getZMethod.invoke(maximumPoint);

                return new Vector(x, y, z);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Check if a region contains a point <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@code Region.contains(com.sk89q.worldedit.Vector)} <br>
     * > 1.13: {@link Region#contains(BlockVector3)} <br>
     * <br>
     * @param region Region to check
     * @param point Point to check
     * @return True if the region contains the point, otherwise false
     */
    public static boolean regionContains(Region region, Vector point){
        try {
            Class<?> regionClass = region.getClass();

            Class<?> vectorClass;
            Object vectorInstance;

            if(VersionUtil.is_1_12()){
                vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                vectorInstance = createLegacyVector(point.getX(), point.getY(), point.getZ());
            }else{
                vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                vectorInstance = createBlockVector3(point.getX(), point.getY(), point.getZ());
            }

            Method containsMethod = regionClass.getMethod("contains", vectorClass);

            return (boolean) containsMethod.invoke(region, vectorInstance);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get the center of a region <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@link Region#getCenter()} -> {@code com.sk89q.worldedit.Vector} <br>
     * > 1.13: {@link Region#getCenter()} -> {@link com.sk89q.worldedit.math.Vector3} <br>
     * <br>
     * @param region Region to get the center from
     * @return Center of the region
     */
    @Nullable
    public static Vector getRegionCenter(Region region){
        try {
            Class<?> regionClass = region.getClass();
            Method getCenterMethod = regionClass.getMethod("getCenter");

            Class<?> vectorClass = getCenterMethod.getReturnType();

            Method getXMethod = vectorClass.getMethod("getX");
            Method getYMethod = vectorClass.getMethod("getY");
            Method getZMethod = vectorClass.getMethod("getZ");

            Object center = getCenterMethod.invoke(region);

            double x = (Double) getXMethod.invoke(center);
            double y = (Double) getYMethod.invoke(center);
            double z = (Double) getZMethod.invoke(center);

            return new Vector(x, y, z);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get the origin of a clipboard <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@code Clipboard.getOrigin() -> {@code com.sk89q.worldedit.Vector} <br>
     * > 1.13: {@code Clipboard.getOrigin() -> {@link com.sk89q.worldedit.math.BlockVector3} <br>
     * <br>
     * @param clipboard Clipboard to get the origin from
     * @return Origin of the clipboard
     */
    @Nullable
    public static Vector getClipboardOrigin(Clipboard clipboard){
        try {
            Class<?> clipboardClass = clipboard.getClass();
            Method getOriginMethod = clipboardClass.getMethod("getOrigin");

            Class<?> vectorClass = getOriginMethod.getReturnType();

            Method getXMethod = vectorClass.getMethod("getX");
            Method getYMethod = vectorClass.getMethod("getY");
            Method getZMethod = vectorClass.getMethod("getZ");

            Object origin = getOriginMethod.invoke(clipboard);

            // Vector class returns double values in 1.12 and BlockVector class returns integer values in 1.13+
            if(VersionUtil.is_1_12()) {
                double x = (Double) getXMethod.invoke(origin);
                double y = (Double) getYMethod.invoke(origin);
                double z = (Double) getZMethod.invoke(origin);

                return new Vector(x, y, z);

            }else{
                int x = (Integer) getXMethod.invoke(origin);
                int y = (Integer) getYMethod.invoke(origin);
                int z = (Integer) getZMethod.invoke(origin);

                return new Vector(x, y, z);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get the points of a region <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@code Polygonal2DRegion.getPoints()} -> {@code List<com.sk89q.worldedit.Vector>} <br>
     * > 1.13: {@code Polygonal2DRegion.getPoints()} -> {@link List<com.sk89q.worldedit.math.BlockVector2>} <br>
     * <br>
     * @param region Region to get the points from
     * @return List of points of the region
     */
    public static List<Vector> getRegionPoints(Polygonal2DRegion region){
        List<Vector> points = new ArrayList<>();

        try {
            for(Object blockVector2Obj : region.getPoints()){
                Class<?> blockVectorClass = blockVector2Obj.getClass();
                Method getXMethod = blockVectorClass.getMethod("getBlockX");
                Method getZMethod = blockVectorClass.getMethod("getBlockZ");

                int x = (Integer) getXMethod.invoke(blockVector2Obj);
                int z = (Integer) getZMethod.invoke(blockVector2Obj);

                points.add(new Vector(x, 0, z));
            }

            return points;
        } catch (Exception ex) {
            ex.printStackTrace();
            return points;
        }
    }

    /**
     * Polygonize a region <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@code Region.polygonize(int)} -> {@code List<com.sk89q.worldedit.Vector>} <br>
     * > 1.13: {@code Region.polygonize(int)} -> {@link List<com.sk89q.worldedit.math.BlockVector2>} <br>
     * <br>
     * @param region Region to polygonize
     * @param maxPoints Maximum amount of points
     * @return List of points of the polygonized region
     */
    public static List<Vector> polygonizeRegion(Region region, int maxPoints){
        List<Vector> points = new ArrayList<>();

        try {
            for(Object blockVector2Obj : region.polygonize(maxPoints)){
                Class<?> blockVectorClass = blockVector2Obj.getClass();
                Method getXMethod = blockVectorClass.getMethod("getBlockX");
                Method getZMethod = blockVectorClass.getMethod("getBlockZ");

                int x = (Integer) getXMethod.invoke(blockVector2Obj);
                int z = (Integer) getZMethod.invoke(blockVector2Obj);

                points.add(new Vector(x, 0, z));
            }

            return points;
        } catch (Exception ex) {
            ex.printStackTrace();
            return points;
        }
    }

    /**
     * Create a Polygonal2DRegion <br>
     * <br>
     * WorldEdit History: <br>
     * < 1.12: {@code CylinderRegion.CylinderRegion(world, com.sk89q.worldedit.Vector, com.sk89q.worldedit.Vector2D, int, int)} <br>
     * > 1.13: {@link CylinderRegion#CylinderRegion(com.sk89q.worldedit.world.World, com.sk89q.worldedit.math.BlockVector3, com.sk89q.worldedit.math.Vector2, int, int)} <br>
     * <br>
     * @param region Region to create the Polygonal2DRegion from
     * @param center Center of the region
     * @param radius Radius of the region
     * @param minY Minimum Y value of the region
     * @param maxY Maximum Y value of the region
     * @return Polygonal2DRegion object
     */
    @Nullable
    public static CylinderRegion createCylinderRegion(Region region, Vector center, int radius, int minY, int maxY){
        try {
            Class<?> cylinderRegionClass = Class.forName("com.sk89q.worldedit.regions.CylinderRegion");

            Class<?> vector3Class, vector2Class;
            Object vector3Instance, vector2Instance;

            if(VersionUtil.is_1_12()){
                vector3Class = Class.forName("com.sk89q.worldedit.Vector");
                vector2Class = Class.forName("com.sk89q.worldedit.Vector2D");

                vector3Instance = createLegacyVector(center.getX(), center.getY(), center.getZ());
                vector2Instance = createLegacyVector2D(radius, radius);
            }else{
                vector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                vector2Class = Class.forName("com.sk89q.worldedit.math.Vector2");

                vector3Instance = createBlockVector3(center.getX(), center.getY(), center.getZ());
                vector2Instance = createVector2(radius, radius);
            }

           Class<?> worldClass = Class.forName("com.sk89q.worldedit.world.World");
            Constructor<?> cylinderRegionConstructor = cylinderRegionClass.getConstructor(
                    worldClass, vector3Class, vector2Class, int.class, int.class
            );

            // Construct the CylinderRegion object
            Object cylinderRegionInstance = cylinderRegionConstructor.newInstance(
                    region.getWorld(), vector3Instance, vector2Instance, minY, maxY
            );

            return (CylinderRegion) cylinderRegionInstance;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Save a region as schematic
     *
     * @param region Region to save as schematic
     * @param schematic File to save the schematic to
     */
    public static void saveSchematic(Region region, File schematic) {
        BlockArrayClipboard cb = new BlockArrayClipboard(region);
        Vector center = getRegionCenter(region);
        Vector min = getRegionMinimumPoint(region);

        if(center == null || min == null)
            return;

        Object vector3CenterInstance;
        Object vector3MinInstance;
        Method setOriginMethod;
        try {
            // Set the origin of the clipboard:
            // cb.setOrigin(center);
            if(VersionUtil.is_1_12()) {
                setOriginMethod = cb.getClass().getMethod("setOrigin", Class.forName("com.sk89q.worldedit.Vector"));
                vector3CenterInstance = createLegacyVector(center.getX(), min.getY(), center.getZ());
                vector3MinInstance = createLegacyVector(min.getX(), min.getY(), min.getZ());
            }else {
                setOriginMethod = cb.getClass().getMethod("setOrigin", Class.forName("com.sk89q.worldedit.math.BlockVector3"));
                vector3CenterInstance = createBlockVector3(center.getX(), min.getY(), center.getZ());
                vector3MinInstance = createBlockVector3(min.getX(), min.getY(), min.getZ());
            }
            if(vector3CenterInstance == null) return;
            setOriginMethod.invoke(cb, vector3CenterInstance);


            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);

            // Create a ForwardExtentCopy operation:
            // new ForwardExtentCopy(editSession, region, cb, min);

            Class<?> forwardExtentCopyClass = Class.forName("com.sk89q.worldedit.function.operation.ForwardExtentCopy");
            Constructor<?> forwardExtentCopyConstructor;
            if(VersionUtil.is_1_12())
                forwardExtentCopyConstructor = forwardExtentCopyClass.getConstructor(Extent.class, Region.class, Extent.class, Class.forName("com.sk89q.worldedit.Vector"));
            else
                forwardExtentCopyConstructor = forwardExtentCopyClass.getConstructor(Extent.class, Region.class, Extent.class, Class.forName("com.sk89q.worldedit.math.BlockVector3"));

            Object forwardExtentCopy = forwardExtentCopyConstructor.newInstance(editSession, region, cb, vector3MinInstance);
            Operations.complete((Operation) forwardExtentCopy);

            if(VersionUtil.is_1_12()) {
                // Obtain world data for 1.12 version
                Method getWorldMethod = Objects.requireNonNull(region.getWorld()).getClass().getMethod("getWorldData");
                Object worldData = getWorldMethod.invoke(region.getWorld());

                // Write the clipboard to a schematic file (SCHEMATIC format)
                Class<?> clipboardFormatClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat");
                Object schematicFormatInstance = clipboardFormatClass.getField("SCHEMATIC").get(null);
                Method getWriterMethod = schematicFormatInstance.getClass().getMethod("getWriter", OutputStream.class);
                getWriterMethod.setAccessible(true);

                try (ClipboardWriter writer = (ClipboardWriter) getWriterMethod.invoke(schematicFormatInstance, new FileOutputStream(schematic, false))) {
                    Method writeMethod = writer.getClass().getMethod("write", Class.forName("com.sk89q.worldedit.extent.clipboard.Clipboard"), Class.forName("com.sk89q.worldedit.world.registry.WorldData"));
                    writeMethod.setAccessible(true);
                    writeMethod.invoke(writer, cb, worldData);
                }

            } else {
                ClipboardFormat format = getClipboardFormat();

                if(format != null)
                    try (ClipboardWriter writer = format.getWriter(new FileOutputStream(schematic, false))) {
                        Method writeMethod = writer.getClass().getMethod("write", Class.forName("com.sk89q.worldedit.extent.clipboard.Clipboard"));
                        writeMethod.invoke(writer, cb);
                    }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Paste a schematic
     *
     * @param outlineSchematic File of the outline schematic
     * @param completedSchematic File of the completed schematic
     * @param world World to paste the schematic in
     * @param mcCoordinates Minecraft coordinates to paste the schematic at
     * @param plotVersion Version of the plot
     * @param fastMode Fast mode for the paste operation
     */
    public static void pasteSchematic(File outlineSchematic, File completedSchematic, World world, Vector mcCoordinates, double plotVersion, boolean fastMode) {

        try {
            com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
            if (fastMode) editSession.setFastMode(true);
            editSession.enableQueue();

            Clipboard outlineClipboard, completedClipboard;

            if(VersionUtil.is_1_12()){
                Class<?> clipboardFormatClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat");
                Object schematicFormatInstance = clipboardFormatClass.getField("SCHEMATIC").get(null);
                Method getReaderMethod = schematicFormatInstance.getClass().getMethod("getReader", InputStream.class);
                getReaderMethod.setAccessible(true);

                Object outlineClipboardReader = getReaderMethod.invoke(schematicFormatInstance, Files.newInputStream(outlineSchematic.toPath()));
                Object completedClipboardReader = getReaderMethod.invoke(schematicFormatInstance, Files.newInputStream(completedSchematic.toPath()));

                // Use reflection to get the getWorldData() method from weWorld
                Class<?> weWorldClass = weWorld.getClass();
                Method getWorldDataMethod = weWorldClass.getMethod("getWorldData");
                Object worldData = getWorldDataMethod.invoke(weWorld);

                // Read the clipboard
                Class<?> clipboardReaderClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardReader");
                Method readMethod = clipboardReaderClass.getMethod("read", Class.forName("com.sk89q.worldedit.world.registry.WorldData"));

                outlineClipboard = (Clipboard) readMethod.invoke(outlineClipboardReader, worldData);
                completedClipboard = (Clipboard) readMethod.invoke(completedClipboardReader, worldData);

            }else{
                Class<?> clipboardFormatsClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats");
                Method findByFileMethod = clipboardFormatsClass.getMethod("findByFile", File.class);

                // Get the ClipboardFormat
                ClipboardFormat format1 = (ClipboardFormat) findByFileMethod.invoke(null, outlineSchematic);
                ClipboardFormat format2 = (ClipboardFormat) findByFileMethod.invoke(null, completedSchematic);

                outlineClipboard = format1.getReader(Files.newInputStream(outlineSchematic.toPath())).read();
                completedClipboard = format2.getReader(Files.newInputStream(completedSchematic.toPath())).read();
            }

            Vector toPaste;
            if (plotVersion >= 3) {
                Vector plotOriginOutline = getClipboardOrigin(outlineClipboard);
                toPaste = new Vector(plotOriginOutline.getX(), plotOriginOutline.getY(), plotOriginOutline.getZ());
            } else toPaste = mcCoordinates;


            Operation operation;

            // 1.12 version
            // operation = new ClipboardHolder(completedClipboard, weWorld.getWorldData()).createPaste(editSession, weWorld.getWorldData()).to(toPaste).ignoreAirBlocks(true).build();
            if(VersionUtil.is_1_12()) {
                // Get necessary classes
                Class<?> clipboardHolderClass = Class.forName("com.sk89q.worldedit.session.ClipboardHolder");
                Class<?> clipboardClass = Class.forName("com.sk89q.worldedit.extent.clipboard.Clipboard");
                Class<?> worldDataClass = Class.forName("com.sk89q.worldedit.world.registry.WorldData");
                Class<?> extentClass = Class.forName("com.sk89q.worldedit.extent.Extent");
                Class<?> weWorldClass = weWorld.getClass();
                Class<?> editSessionClass = editSession.getClass();

                // Use reflection to get the getWorldData() method from weWorld
                Method getWorldDataMethod = weWorldClass.getMethod("getWorldData");
                Object worldData = getWorldDataMethod.invoke(weWorld);

                // Get the constructor for ClipboardHolder that takes (Clipboard, WorldData)
                Constructor<?> clipboardHolderConstructor = clipboardHolderClass.getConstructor(clipboardClass, worldDataClass);

                // Create the ClipboardHolder instance
                Object clipboardHolderInstance = clipboardHolderConstructor.newInstance(completedClipboard, worldData);

                // Get the createPaste method
                Method createPasteMethod = clipboardHolderClass.getMethod("createPaste", extentClass, worldDataClass);

                // Invoke createPaste on the clipboardHolder instance
                Object pasteOperation = createPasteMethod.invoke(clipboardHolderInstance, editSession, worldData);

                // Get the to method
                Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
                Method toMethod = pasteOperation.getClass().getMethod("to", vectorClass);

                // Invoke the to method
                Object vectorInstance = createLegacyVector(toPaste.getX(), toPaste.getY(), toPaste.getZ());
                PasteBuilder toPasteOperation = (PasteBuilder) toMethod.invoke(pasteOperation, vectorInstance);

                operation = toPasteOperation.ignoreAirBlocks(true).build();


            // 1.13+ version with WorldEdit
            // operation = new ClipboardHolder(completedClipboard).createPaste(editSession).to(toPaste).ignoreAirBlocks(true).build();
            }else{
                Class<?> clipboardHolderClass = Class.forName("com.sk89q.worldedit.session.ClipboardHolder");
                Class<?> clipboardClass = Class.forName("com.sk89q.worldedit.extent.clipboard.Clipboard");
                Class<?> extentClass = Class.forName("com.sk89q.worldedit.extent.Extent");

                // Get the constructor for ClipboardHolder that takes (Clipboard, WorldData)
                Constructor<?> clipboardHolderConstructor = clipboardHolderClass.getConstructor(clipboardClass);

                // Create the ClipboardHolder instance
                Object clipboardHolderInstance = clipboardHolderConstructor.newInstance(completedClipboard);

                // Get the createPaste method
                Method createPasteMethod = clipboardHolderClass.getMethod("createPaste", extentClass);

                // Invoke createPaste on the clipboardHolder instance
                Object pasteOperation = createPasteMethod.invoke(clipboardHolderInstance, editSession);

                // Get the to method
                Class<?> vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                Method toMethod = pasteOperation.getClass().getMethod("to", vectorClass);

                // Invoke the to method
                Object vectorInstance = createBlockVector3(toPaste.getX(), toPaste.getY(), toPaste.getZ());
                PasteBuilder toPasteOperation = (PasteBuilder) toMethod.invoke(pasteOperation, vectorInstance);

                operation = toPasteOperation.ignoreAirBlocks(true).build();
            }

            Operations.complete(operation);
            editSession.commit();

            // 1.12 version
            // editSession.flushQueue();
            if(VersionUtil.is_1_12()){
                Method flushQueueMethod = editSession.getClass().getMethod("flushQueue");
                flushQueueMethod.invoke(editSession);

            // 1.13+ version
            // editSession.close();
            }else{
                Method closeMethod = editSession.getClass().getMethod("close");
                closeMethod.invoke(editSession);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Create a com.sk89q.worldedit.math.BlockVector3 object
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return BlockVector3 object
     */
    @Nullable
    private static Object createBlockVector3(double x, double y, double z) {
        try {
            Class<?> blockVector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            Method atMethod = blockVector3Class.getMethod("at", double.class, double.class, double.class);
            return atMethod.invoke(null, x, y, z);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Create a com.sk89q.worldedit.math.Vector2 object
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Vector2 object
     */
    @Nullable
    private static Object createVector2(double x, double z) {
        try {
            Class<?> vector2Class = Class.forName("com.sk89q.worldedit.math.Vector2");
            Method atMethod = vector2Class.getMethod("at", double.class, double.class);
            return atMethod.invoke(null, x, z);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Create a legacy com.sk89q.worldedit.Vector object from WorldEdit 1.12
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Vector object
     */
    @Nullable
    private static Object createLegacyVector(double x, double y, double z) {
        try {
            Class<?> vectorClass = Class.forName("com.sk89q.worldedit.Vector");
            Constructor<?> vectorConstructor = vectorClass.getConstructor(double.class, double.class, double.class);
            return vectorConstructor.newInstance(x, y, z);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Create a legacy com.sk89q.worldedit.Vector2D object from WorldEdit 1.12
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Vector2D object
     */
    @Nullable
    private static Object createLegacyVector2D(int x, int z) {
        try {
            Class<?> vector2DClass = Class.forName("com.sk89q.worldedit.Vector2D");
            Constructor<?> vector2DConstructor = vector2DClass.getConstructor(int.class, int.class);
            return vector2DConstructor.newInstance(x, z);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * Get the ClipboardFormat for the WorldEdit version 1.13+
     *
     * @return ClipboardFormat object
     */
    @Nullable
    private static ClipboardFormat getClipboardFormat(){
        try {
            Class<?> clipboardFormatsClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats");
            Class<?> clipboardFormatClass = Class.forName("com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat");
            Method findByAliasMethod = clipboardFormatsClass.getMethod("findByAlias", String.class);

            // Get the ClipboardFormat for SPONGE_SCHEMATIC
            ClipboardFormat format;

            if(VersionUtil.is_1_12()){
                format = (ClipboardFormat) clipboardFormatClass.getField("SCHEMATIC").get(null);

            }else{
                    format = (ClipboardFormat) findByAliasMethod.invoke(null, "schem");
                if(format == null)
                    format = (ClipboardFormat) findByAliasMethod.invoke(null, "sponge");
                if(format == null)
                    format = (ClipboardFormat) findByAliasMethod.invoke(null, "sponge.3");
            }

            return format;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
