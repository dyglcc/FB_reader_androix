package dyg.activity;

public class ReyunConfig {
    private static volatile ReyunConfig instance = null;
    private static final int DEFAULT_COUNT = 3;

    public static ReyunConfig getInstance() {
        if (instance == null) {
            synchronized (ReyunConfig.class) {
                if (instance == null) {
                    instance = new ReyunConfig();
                }
            }
        }
        return instance;
    }

    private ReyunConfig() {

    }

    public void setCounts2Ad(int count) {
        this.counts2Ad = count;
    }

    private int counts2Ad = DEFAULT_COUNT;

    public int getCounts2Ad() {
        return counts2Ad;
    }


}
