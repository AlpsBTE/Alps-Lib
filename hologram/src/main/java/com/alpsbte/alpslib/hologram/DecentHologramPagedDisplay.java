package com.alpsbte.alpslib.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.Collections;

/**
 * Extended class to create paged hologram display
 */
public abstract class DecentHologramPagedDisplay extends DecentHologramDisplay {
    public static final String PROGRESS_BAR_NEXT_PAGE = "---------------------";

    protected String currentPage;
    public BukkitTask changePageTask = null;
    private final Plugin plugin;
    private int changeState = 0;
    private long changeDelay = 0;
    protected boolean automaticallySkipPage;

    /**
     * @param id        Hologram identifier for creating DecentHologram name, this will later be concatenated as "${player.uuid}-${id}"
     * @param location  The location in a world to create hologram.
     * @param isEnabled Force enable or disable this hologram on create, this will not register new hologram in the hashmap.
     * @param plugin    Assign a plugin reference and this hologram pages will be automatically turns by a fixed interval.
     */
    public DecentHologramPagedDisplay(@NotNull String id, Location location, boolean isEnabled, @NotNull Plugin plugin) {
        super(id, location, isEnabled);
        this.plugin = plugin;
        this.automaticallySkipPage = true;
    }

    /**
     * @param id        Hologram identifier for creating DecentHologram name, this will later be concatenated as "${player.uuid}-${id}"
     * @param location  The location in a world to create hologram.
     * @param isEnabled Force enable or disable this hologram on create, this will not register new hologram in the hashmap.
     */
    public DecentHologramPagedDisplay(@NotNull String id, Location location, boolean isEnabled) {
        super(id, location, isEnabled);
        this.plugin = null;
        this.automaticallySkipPage = false;
    }

    public abstract List<String> getPages();

    @Override
    public void create(Player player) {
        if (!super.isEnabled() | !this.hasViewPermission(player.getUniqueId())) return;
        if (getPages() != null && !getPages().isEmpty()) currentPage = getPages().get(0);
        super.create(player);
        if (automaticallySkipPage) startChangePageTask();
    }

    /**
     * Starts a repeating task to update and change hologram pages at a set interval.
     * Cancels any previous task and skips creation if the interval is zero.
     */
    private void startChangePageTask() {
        final long interval = getInterval();
        changeState = 0;
        changeDelay = interval / PROGRESS_BAR_NEXT_PAGE.length();

        if (changePageTask != null) changePageTask.cancel();
        changePageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (changeState == 0) getHolograms().keySet().forEach(uuid -> reload(uuid));
                if (interval == 0) return;
                if (changeState >= changeDelay) {
                    if (automaticallySkipPage) goToNextPage();
                    else changePageTask.cancel();
                } else {
                    changeState++;
                    getHolograms().forEach((uuid, holo) -> updateDataLines(holo.getPage(0),
                            holo.getPage(0).getLines().size() - 1, getFooter(uuid)));
                }
            }
        }.runTaskTimer(plugin, 0, changeDelay);
    }

    @Override
    public List<DecentHologramDisplay.DataLine<?>> getFooter(UUID playerUUID) {
        int footerLength = PROGRESS_BAR_NEXT_PAGE.length();
        int highlightCount = (int) (((float) changeState / changeDelay) * footerLength);

        StringBuilder highlighted = new StringBuilder();
        for (int i = 0; i < highlightCount; i++) {
            highlighted.append("-");
        }
        StringBuilder notH = new StringBuilder();
        for (int i = 0; i < footerLength - highlightCount; i++) {
            notH.append("-");
        }

        return Collections.singletonList(new DecentHologramDisplay.TextLine("§e" + highlighted + "§7" + notH));
    }

    /**
     * If provided plugin reference, override this method to set interval value in milliseconds.
     *
     * @return Plugin's check interval for when turning to next page automatically.
     */
    public long getInterval() {
        return 15 * 20L;
    }

    /**
     * Advances the display to the next page in the list of pages.<br/>
     * If the last page is reached, it loops back to the first page.<br/>
     * <b>Note:</b> There is no function to navigate to the previous page.
     */
    public void goToNextPage() {
        String nextPage = getNextPageItem(getPages(), currentPage);
        currentPage = nextPage == null ? getPages().get(0) : nextPage;
        startChangePageTask();
    }

    /**
     * Retrieves the item immediately following the specified item in a list, or null if the end of the list is reached.
     * @param list The list of items.
     * @param currentItem The current item.
     * @param <T> The type of items in the list.
     * @return The next item in the list, or null if at the end.
     */
    private static <T> T getNextPageItem(List<T> list, T currentItem) {
        int currentIndex = list.indexOf(currentItem);
        if (currentIndex == -1 || currentIndex + 1 >= list.size()) {
            return null;
        }
        return list.get(currentIndex + 1);
    }
}