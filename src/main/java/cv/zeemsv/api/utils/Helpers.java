package cv.zeemsv.api.utils;

public final class Helpers {
    private Helpers() {
    }

    public static <T> T nvl(T novoValor, T atual) {
        return novoValor != null ? novoValor : atual;
    }
}
