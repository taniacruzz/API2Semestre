package org.openjfx.api2semestre.view.controllers.templates;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openjfx.api2semestre.appointment.Appointment;
import org.openjfx.api2semestre.authentication.Authentication;
import org.openjfx.api2semestre.database.QueryLibs;
import org.openjfx.api2semestre.report.ReportInterval;
import org.openjfx.api2semestre.utils.AppointmentCalculator;
import org.openjfx.api2semestre.view.utils.ChartGenerator;
import org.openjfx.api2semestre.view.utils.dashboard.DashboardContext;
import org.openjfx.api2semestre.view.utils.dashboard.FilterControl;
import org.openjfx.api2semestre.view.utils.dashboard.FilterField;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

public class DashboardTab {
    private DashboardContext context;

    @FXML private HBox hb_filters;
    @FXML private FlowPane fp_charts;

    private ObservableList<Appointment> filteredAppointments;
    private Appointment[] loadedAppointments;

    private ObservableList<ReportInterval> filteredIntervals;
    private ReportInterval[] loadedIntervals;

    FilterControl[] filterControls;

    private void createFilters () {

        // Inicia a conexão com o banco de dados
        Optional<java.sql.Connection> connectionOptional = QueryLibs.connect();

        List<FilterControl> filterControlsList = Arrays.asList(context.getFields()).stream()
            .map((FilterField filterField) -> {
                try {
                    FilterControl filterControl = filterField.create(
                        loadedAppointments,
                        loadedIntervals,
                        this::applyFilter,
                        connectionOptional
                    ).orElseThrow(
                        () -> new Exception(
                            "Khali | DashboardTab.createFilters -- Error: unhadled FilterField.create() implementation"
                        )
                    );
                    return filterControl;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(filterControl -> filterControl != null)
            .collect(Collectors.toList()
        );

        // Fecha a conexão com o banco de dados
        QueryLibs.close(connectionOptional);

        hb_filters.getChildren().addAll(
            filterControlsList.stream().map(
                (FilterControl filterControl) -> filterControl.getControl()
            ).collect(Collectors.toList())
        );

        filterControls = filterControlsList.toArray(FilterControl[]::new);
    }
    
    public void setContext(DashboardContext context, Optional<java.sql.Connection> connectionOptional) {
        this.context = context;
        
        loadedAppointments = context.loadData(Authentication.getCurrentUser(), connectionOptional);
        loadedIntervals = AppointmentCalculator.calculateReports(loadedAppointments);

        // System.out.println("DashboardTab | loadedAppointments.length: " + loadedAppointments.length);
        // System.out.println("DashboardTab | loadedIntervals.length: " + loadedIntervals.length);

        // for (Appointment apt : loadedAppointments) {
        //     System.out.println(
        //         "setContext | auth.curUsr -- apt: " + Authentication.getCurrentUser().getName() 
        //         + " | apt.usr: " + apt.getRequesterName()
        //         + " | " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(apt.getStart().getTime()))
        //         + " | " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(apt.getEnd().getTime()))
        //     );
        // }
        // for (ReportInterval ri : loadedIntervals) {
        //     Optional<Appointment> apt = QueryLibs.selectAppointmentById(ri.getAppointmmentId());
        //     System.out.println(
        //         "setContext | auth.curUsr -- ri: " + Authentication.getCurrentUser().getName() 
        //         + " | apt.usr: " + (apt.isEmpty() ? "NULL APT" : apt.get().getRequesterName()) 
        //         + " | " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(ri.getStart().getTime()))
        //         + " | " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(ri.getEnd().getTime()))
        //     );
        // }

        createFilters();

        applyFilter();

        // System.out.println("DashboardTab | filteredAppointments.size(): " + filteredAppointments.size());
        // System.out.println("DashboardTab | filteredIntervals.size(): " + filteredIntervals.size());

        // for (Appointment apt : loadedAppointments) {
        //     System.out.println(Authentication.getCurrentUser().getName() + " | " + apt.getRequesterName());
        // }

    }

    private void applyFilter() {        
        filteredAppointments = FXCollections.observableList(
            Arrays.asList(loadedAppointments).stream()
            .filter((Appointment appointment) -> {
                if (filterControls != null) for (FilterControl filterControl : filterControls) {
                    if (!filterControl.filter(appointment)) return false;
                }
                return true;
            })
            .collect(Collectors.toList())
        );

        filteredIntervals = FXCollections.observableList(
            Arrays.asList(loadedIntervals).stream().filter((ReportInterval interval) -> {
                for (Appointment apt : filteredAppointments) {
                    if (interval.getAppointmmentId() == apt.getId()) return true;
                }
                return false;
            }).collect(Collectors.toList())
        );

        updateCharts();

    }

    private void updateCharts () {
        fp_charts.getChildren().clear();

        Appointment[] appointmentsArray = filteredAppointments.toArray(Appointment[]::new);
        ReportInterval[] intervalsArray = filteredIntervals.toArray(ReportInterval[]::new);

        addChart(TotalHours.totalHoursParent(appointmentsArray, intervalsArray));
        addChart(ChartGenerator.statusAppointmentChart(appointmentsArray));

        switch (context.getProfile()) {
            case Administrator:
                addChart(ChartGenerator.intervalFeeChart(intervalsArray));
            break;
            case Gestor: case Colaborador:
                addChart(ChartGenerator.hourIntersectionCountGraph(appointmentsArray));
            default: break;
        }

        addChart(ChartGenerator.weekIntersectionCountGraph(appointmentsArray));
        addChart(ChartGenerator.monthIntersectionCountGraph(appointmentsArray));
    }

    private void addChart (Node control) {
        // final double[] SIZES = new double[] {180, 180, 256, 256, 256};
        // control.minWidth(-1);
        // control.prefWidth(-1);
        // control.maxWidth(160);
        fp_charts.getChildren().add(control);
    }

    public Appointment[] getLoadedAppointments() { return loadedAppointments; }
    public ReportInterval[] getLoadedIntervals() { return loadedIntervals; }
}
