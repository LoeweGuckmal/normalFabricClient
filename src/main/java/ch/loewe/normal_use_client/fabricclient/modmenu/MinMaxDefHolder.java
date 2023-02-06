package ch.loewe.normal_use_client.fabricclient.modmenu;

public record MinMaxDefHolder(int min, int max, int def) {

    public int max() {
        return this.max;
    }

    public int min() {
        return this.min;
    }

    public int def(){
        return this.def;
    }
}
