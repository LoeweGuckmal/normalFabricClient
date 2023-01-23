package ch.loewe.normal_use_client.fabricclient.modmenu;

public record MinMaxHolder(int max, int min) {
    public MinMaxHolder(int max, int min) {
        this.max = max;
        this.min = min;
    }

    public int max() {
        return this.max;
    }

    public int min() {
        return this.min;
    }
}
