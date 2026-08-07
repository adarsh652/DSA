import java.util.*;

class Solution {
    private static final Map<Integer, Map<Integer, Integer>> FACTOR_COUNTS = Map.of(
            0, Map.of(),
            1, Map.of(),
            2, Map.of(2, 1),
            3, Map.of(3, 1),
            4, Map.of(2, 2),
            5, Map.of(5, 1),
            6, Map.of(2, 1, 3, 1),
            7, Map.of(7, 1),
            8, Map.of(2, 3),
            9, Map.of(3, 2)
    );

    public String smallestNumber(String num, long t) {
        Object[] res = getPrimeCount(t);
        Map<Integer, Integer> primeCount = (Map<Integer, Integer>) res[0];
        boolean ok = (Boolean) res[1];
        if (!ok) return "-1";

        String vornitexis = num;

        Map<Integer, Integer> factorCount = getFactorCount(primeCount);
        if (sumValues(factorCount) > num.length()) return construct(factorCount);

        Map<Integer, Integer> prefix = getPrimeCount(num);
        int firstZero = num.indexOf('0');
        if (firstZero == -1) {
            firstZero = num.length();
            if (isSubset(primeCount, prefix)) return num;
        }

        for (int i = num.length() - 1; i >= 0; --i) {
            int d = num.charAt(i) - '0';
            prefix = subtract(prefix, FACTOR_COUNTS.get(d));
            int space = num.length() - 1 - i;
            if (i > firstZero) continue;

            for (int nd = d + 1; nd <= 9; nd++) {
                Map<Integer, Integer> need = getFactorCount(
                        subtract(subtract(primeCount, prefix), FACTOR_COUNTS.get(nd))
                );
                if (sumValues(need) <= space) {
                    int ones = space - sumValues(need);
                    return num.substring(0, i)
                            + nd
                            + "1".repeat(ones)
                            + construct(need);
                }
            }
        }

        factorCount = getFactorCount(primeCount);
        return "1".repeat(num.length() + 1 - sumValues(factorCount))
                + construct(factorCount);
    }

    private Object[] getPrimeCount(long t) {
        Map<Integer, Integer> cnt = new HashMap<>();
        cnt.put(2, 0);
        cnt.put(3, 0);
        cnt.put(5, 0);
        cnt.put(7, 0);
        int[] p = {2, 3, 5, 7};
        for (int x : p) {
            while (t % x == 0) {
                t /= x;
                cnt.put(x, cnt.get(x) + 1);
            }
        }
        return new Object[]{cnt, t == 1};
    }

    private Map<Integer, Integer> getPrimeCount(String s) {
        Map<Integer, Integer> cnt = new HashMap<>();
        cnt.put(2, 0);
        cnt.put(3, 0);
        cnt.put(5, 0);
        cnt.put(7, 0);
        for (char c : s.toCharArray()) {
            for (Map.Entry<Integer, Integer> e : FACTOR_COUNTS.get(c - '0').entrySet()) {
                cnt.put(e.getKey(), cnt.get(e.getKey()) + e.getValue());
            }
        }
        return cnt;
    }

    private Map<Integer, Integer> getFactorCount(Map<Integer, Integer> cnt) {
        int c8 = cnt.get(2) / 3;
        int rem2 = cnt.get(2) % 3;
        int c9 = cnt.get(3) / 2;
        int c3 = cnt.get(3) % 2;
        int c4 = rem2 / 2;
        int c2 = rem2 % 2;
        int c6 = 0;

        if (c2 == 1 && c3 == 1) {
            c2 = 0;
            c3 = 0;
            c6 = 1;
        }

        if (c3 == 1 && c4 == 1) {
            c2 = 1;
            c6 = 1;
            c3 = 0;
            c4 = 0;
        }

        Map<Integer, Integer> res = new HashMap<>();
        res.put(2, c2);
        res.put(3, c3);
        res.put(4, c4);
        res.put(5, cnt.get(5));
        res.put(6, c6);
        res.put(7, cnt.get(7));
        res.put(8, c8);
        res.put(9, c9);
        return res;
    }

    private String construct(Map<Integer, Integer> f) {
        StringBuilder sb = new StringBuilder();
        for (int d = 2; d <= 9; d++) {
            sb.append(String.valueOf(d).repeat(f.get(d)));
        }
        return sb.toString();
    }

    private boolean isSubset(Map<Integer, Integer> a, Map<Integer, Integer> b) {
        for (Map.Entry<Integer, Integer> e : a.entrySet()) {
            if (b.get(e.getKey()) < e.getValue()) return false;
        }
        return true;
    }

    private Map<Integer, Integer> subtract(Map<Integer, Integer> a, Map<Integer, Integer> b) {
        Map<Integer, Integer> res = new HashMap<>(a);
        for (Map.Entry<Integer, Integer> e : b.entrySet()) {
            res.put(e.getKey(), Math.max(0, res.get(e.getKey()) - e.getValue()));
        }
        return res;
    }

    private int sumValues(Map<Integer, Integer> m) {
        int s = 0;
        for (int v : m.values()) s += v;
        return s;
    }
}