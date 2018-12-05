package cn.finull.framework.db.orm;

public final class PageHelper {

    private static final ThreadLocal<PageConfig> TL = new ThreadLocal<>();

    public static void startPage(int pageNum,int pageSize) {
        PageConfig config = new PageConfig();
        config.pageNum = pageNum;
        config.pageSize = pageSize;
        TL.set(config);
    }

    public static boolean isPage() {
        return TL.get() != null;
    }

    public static int getPageNum() {
        PageConfig config = TL.get();
        return config.pageNum;
    }

    public static int getPageSize() {
        PageConfig config = TL.get();
        return config.pageSize;
    }

    public static void remove() {
        TL.remove();
    }

    static class PageConfig {
        int pageNum = 1;
        int pageSize = 10;
    }
}
