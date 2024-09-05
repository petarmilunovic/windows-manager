package backend;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts JNH (JNativeHook) key codes to JNA (Java Native Access) key codes.
 * This class provides methods to map key codes captured from JNH to those used by JNA for window movement operations.
 */
public class KeyCodeConverter {

    static final Map<Integer, Integer> jnativehookToJNA = new HashMap<>();

    // JNH (JNativeHook) codes (A-Z)
    private static final int JNA_A = 65;
    private static final int JNA_B = 66;
    private static final int JNA_C = 67;
    private static final int JNA_D = 68;
    private static final int JNA_E = 69;
    private static final int JNA_F = 70;
    private static final int JNA_G = 71;
    private static final int JNA_H = 72;
    private static final int JNA_I = 73;
    private static final int JNA_J = 74;
    private static final int JNA_K = 75;
    private static final int JNA_L = 76;
    private static final int JNA_M = 77;
    private static final int JNA_N = 78;
    private static final int JNA_O = 79;
    private static final int JNA_P = 80;
    private static final int JNA_Q = 81;
    private static final int JNA_R = 82;
    private static final int JNA_S = 83;
    private static final int JNA_T = 84;
    private static final int JNA_U = 85;
    private static final int JNA_V = 86;
    private static final int JNA_W = 87;
    private static final int JNA_X = 88;
    private static final int JNA_Y = 89;
    private static final int JNA_Z = 90;
    private static final int JNA_ALT = 18;

    // JNA (Java Native Access) codes (A-Z)
    public static final int JNH_A = 30;
    public static final int JNH_B = 48;
    public static final int JNH_C = 46;
    public static final int JNH_D = 32;
    public static final int JNH_E = 18;
    public static final int JNH_F = 33;
    public static final int JNH_G = 34;
    public static final int JNH_H = 35;
    public static final int JNH_I = 23;
    public static final int JNH_J = 36;
    public static final int JNH_K = 37;
    public static final int JNH_L = 38;
    public static final int JNH_M = 50;
    public static final int JNH_N = 49;
    public static final int JNH_O = 24;
    public static final int JNH_P = 25;
    public static final int JNH_Q = 16;
    public static final int JNH_R = 19;
    public static final int JNH_S = 31;
    public static final int JNH_T = 20;
    public static final int JNH_U = 22;
    public static final int JNH_V = 47;
    public static final int JNH_W = 17;
    public static final int JNH_X = 45;
    public static final int JNH_Y = 21;
    public static final int JNH_Z = 44;
    public static final int JNH_ALT = 56;

    // map JNH key codes to JNA key codes
    static {

        jnativehookToJNA.put(JNH_Q, JNA_Q);
        jnativehookToJNA.put(JNH_W, JNA_W);
        jnativehookToJNA.put(JNH_E, JNA_E);
        jnativehookToJNA.put(JNH_R, JNA_R);
        jnativehookToJNA.put(JNH_T, JNA_T);
        jnativehookToJNA.put(JNH_Y, JNA_Y);
        jnativehookToJNA.put(JNH_U, JNA_U);
        jnativehookToJNA.put(JNH_I, JNA_I);
        jnativehookToJNA.put(JNH_O, JNA_O);
        jnativehookToJNA.put(JNH_P, JNA_P);
        jnativehookToJNA.put(JNH_A, JNA_A);
        jnativehookToJNA.put(JNH_S, JNA_S);
        jnativehookToJNA.put(JNH_D, JNA_D);
        jnativehookToJNA.put(JNH_F, JNA_F);
        jnativehookToJNA.put(JNH_G, JNA_G);
        jnativehookToJNA.put(JNH_H, JNA_H);
        jnativehookToJNA.put(JNH_J, JNA_J);
        jnativehookToJNA.put(JNH_K, JNA_K);
        jnativehookToJNA.put(JNH_L, JNA_L);
        jnativehookToJNA.put(JNH_Z, JNA_Z);
        jnativehookToJNA.put(JNH_X, JNA_X);
        jnativehookToJNA.put(JNH_C, JNA_C);
        jnativehookToJNA.put(JNH_V, JNA_V);
        jnativehookToJNA.put(JNH_B, JNA_B);
        jnativehookToJNA.put(JNH_N, JNA_N);
        jnativehookToJNA.put(JNH_M, JNA_M);
        jnativehookToJNA.put(JNH_ALT, JNA_ALT);
    }

    /**
     * Convert JNativeHook key codes to JNA
     */
    public static int jnativehookToJNA(int jnativehookKeyCode) {
        return jnativehookToJNA.getOrDefault(jnativehookKeyCode, -1); // returns -1 if not found
    }

}
