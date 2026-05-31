package br.com.conectapet.config;

public class TenantContext {

    private static final ThreadLocal<Long>    TENANT    = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> ONG_ADMIN = new ThreadLocal<>();

    public static void set(Long ongId, boolean isOngAdmin) {
        TENANT.set(ongId);
        ONG_ADMIN.set(isOngAdmin);
    }

    public static Long    get()        { return TENANT.get(); }
    public static boolean hasTenant()  { return TENANT.get() != null; }
    public static boolean isOngAdmin() { return Boolean.TRUE.equals(ONG_ADMIN.get()); }

    public static void clear() {
        TENANT.remove();
        ONG_ADMIN.remove();
    }
}
