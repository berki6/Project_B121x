package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class FacilityRow {

    private final StringProperty facilityName;
    private final StringProperty availableBed;
    private final Button actionButton;

    public FacilityRow(String facilityName, String availableBeds) {
        this.facilityName = new SimpleStringProperty(facilityName);
        this.availableBed = new SimpleStringProperty(availableBeds);
        this.actionButton = new Button("Action");
    }

    public String getFacilityName() {
        return facilityName.get();
    }

    public String getAvailableBed() {
        return availableBed.get();
    }

    public Button getActionButton() {
        return actionButton;
    }

    public StringProperty facilityNameProperty() {
        return facilityName;
    }

    public StringProperty availableBedProperty() {
        return availableBed;
    }
}
