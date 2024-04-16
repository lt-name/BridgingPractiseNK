package cn.ricoco.bridgingpractise.plugin;

public class Exp {

    private final int exp;
    private final int level;

    public Exp(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }
}
