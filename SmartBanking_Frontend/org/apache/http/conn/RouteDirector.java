package org.apache.http.conn;

public class RouteDirector {
    public static final int COMPLETE = 0;
    public static final int CONNECT_PROXY = 2;
    public static final int CONNECT_TARGET = 1;
    public static final int LAYER_PROTOCOL = 5;
    public static final int TUNNEL_PROXY = 4;
    public static final int TUNNEL_TARGET = 3;
    public static final int UNREACHABLE = -1;

    public int nextStep(HttpRoute plan, HttpRoute fact) {
        if (plan == null) {
            throw new IllegalArgumentException("Planned route may not be null.");
        } else if (fact == null) {
            return firstStep(plan);
        } else {
            if (plan.getHopCount() > CONNECT_TARGET) {
                return proxiedStep(plan, fact);
            }
            return directStep(plan, fact);
        }
    }

    protected int firstStep(HttpRoute plan) {
        return plan.getHopCount() > CONNECT_TARGET ? CONNECT_PROXY : CONNECT_TARGET;
    }

    protected int directStep(HttpRoute plan, HttpRoute fact) {
        if (fact.getHopCount() > CONNECT_TARGET || !plan.getTargetHost().equals(fact.getTargetHost()) || plan.isSecure() != fact.isSecure()) {
            return UNREACHABLE;
        }
        if (plan.getLocalAddress() == null || plan.getLocalAddress().equals(fact.getLocalAddress())) {
            return COMPLETE;
        }
        return UNREACHABLE;
    }

    protected int proxiedStep(HttpRoute plan, HttpRoute fact) {
        if (fact.getHopCount() <= CONNECT_TARGET || !plan.getTargetHost().equals(fact.getTargetHost())) {
            return UNREACHABLE;
        }
        int phc = plan.getHopCount();
        int fhc = fact.getHopCount();
        if (phc < fhc) {
            return UNREACHABLE;
        }
        for (int i = COMPLETE; i < fhc + UNREACHABLE; i += CONNECT_TARGET) {
            if (!plan.getHopTarget(i).equals(fact.getHopTarget(i))) {
                return UNREACHABLE;
            }
        }
        if (phc > fhc) {
            return TUNNEL_PROXY;
        }
        if (fact.isTunnelled() && !plan.isTunnelled()) {
            return UNREACHABLE;
        }
        if (fact.isLayered() && !plan.isLayered()) {
            return UNREACHABLE;
        }
        if (plan.isTunnelled() && !fact.isTunnelled()) {
            return TUNNEL_TARGET;
        }
        if (plan.isLayered() && !fact.isLayered()) {
            return LAYER_PROTOCOL;
        }
        if (plan.isSecure() == fact.isSecure()) {
            return COMPLETE;
        }
        return UNREACHABLE;
    }
}
