package org.openjfx.api2semestre.view.controllers.views.popups;

import org.openjfx.api2semestre.view_utils.AppointmentWrapper;

public interface PopupController {
    AppointmentWrapper getSelected();
    void setSelected (AppointmentWrapper aptWrapper);
    void buildTable ();
}
