package org.firstinspires.ftc.teamcode.webdashboard;

public class EmptyLayout extends DashboardLayout {
    public EmptyLayout() {
        super(null);
    }

    @Override
    public void setMyNodeValue(String id, String value) {

    }

    @Override
    public void setMyNodeValue(String id, Object value) {

    }

    public double getDoubleValue(String id, double defaultValue) {
        return defaultValue;
    }

    public boolean getBooleanValue(String id) {
        return false;
    }

    public String getInputValue(String id) {
        return "";
    }

    public String getSelectedValue(String id) {
        return "";
    }
}
