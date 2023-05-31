package org.openjfx.api2semestre.view.controllers.views;

import java.util.List;

import org.openjfx.api2semestre.appointment.Appointment;
import org.openjfx.api2semestre.appointment.AppointmentType;
import org.openjfx.api2semestre.authentication.Authentication;
import org.openjfx.api2semestre.authentication.Profile;
import org.openjfx.api2semestre.database.QueryLibs;
import org.openjfx.api2semestre.report.ReportInterval;
import org.openjfx.api2semestre.utils.AppointmentCalculator;
import org.openjfx.api2semestre.utils.DateConverter;
import org.openjfx.api2semestre.view.utils.ChartGenerator;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.chart.LineChart;

public class Dashboard {

    @FXML private HBox hb_filters;
    

    @FXML private FlowPane fp_charts;


    @FXML private LineChart<?, ?> chartDiaSemana;
    

    public void initialize() {

        Appointment[] appointments = new Appointment[] {
            // new Appointment(0, AppointmentType.Overtime, 
            //     DateConverter.stringToTimestamp("2023-05-05 22:00:00"), 
            //     DateConverter.stringToTimestamp("2023-05-07 19:00:00"), 
            //     0, 0, "ProjetoA", "pq sim"
            // ),
            // new Appointment(0, AppointmentType.Overtime, 
            //     DateConverter.stringToTimestamp("2023-05-05 22:00:00"), 
            //     DateConverter.stringToTimestamp("2023-05-06 01:00:00"), 
            //     0, 0, "ProjetoA", "pq sim"
            // ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 12:00:00"), 
                DateConverter.stringToTimestamp("2023-05-05 13:00:00"), 
                0, 0, "ProjetoA", "pq sim"
            ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 12:30:00"), 
                DateConverter.stringToTimestamp("2023-05-05 13:30:00"), 
                0, 0, "ProjetoA", "pq sim"
            ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 13:00:00"), 
                DateConverter.stringToTimestamp("2023-05-05 14:00:00"), 
                0, 0, "ProjetoA", "pq sim"
            ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 13:30:00"), 
                DateConverter.stringToTimestamp("2023-05-05 14:30:00"), 
                0, 0, "ProjetoA", "pq sim"
            ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 14:00:00"), 
                DateConverter.stringToTimestamp("2023-05-05 15:00:00"), 
                0, 0, "ProjetoA", "pq sim"
            ),
            new Appointment(0, AppointmentType.Overtime, 
                DateConverter.stringToTimestamp("2023-05-05 14:15:00"), 
                DateConverter.stringToTimestamp("2023-05-05 14:45:00"), 
                0, 0, "ProjetoA", "pq sim"
            )
        };

        fp_charts.getChildren().add(ChartGenerator.hourIntersectionCountGraph(appointments));
        fp_charts.getChildren().add(ChartGenerator.weekIntersectionCountGraph(appointments));
        fp_charts.getChildren().add(ChartGenerator.monthIntersectionCountGraph(appointments));


        if (Authentication.getCurrentUser().getProfile() == Profile.Administrator){
            Appointment[] listAppointments = QueryLibs.selectAllAppointments();
            List<ReportInterval> listResporIntervals = AppointmentCalculator.calculateReports(listAppointments);
        }
    }

}
