package org.ivangeevo.bwt_hct.loot;

public class PistonBreakTracker {
    private static final ThreadLocal<Boolean> isPistonBreak = ThreadLocal.withInitial(() -> false);

    public static void setPistonBreak(boolean value) {
        isPistonBreak.set(value);
    }

    public static boolean isPistonBreak() {
        return isPistonBreak.get();
    }

    public static void clear() {
        isPistonBreak.remove();
    }
}
