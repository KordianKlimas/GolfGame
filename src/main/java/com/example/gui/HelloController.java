package com.example.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.example.gui.ODESolver.euler;

public class HelloController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField size;
    @FXML
    private TextField time;
    @FXML
    private TextField formula;
    @FXML
    private TextField formula1;
    @FXML
    private TextField conditions;
    @FXML
    private RadioButton Euler, Runge;
    @FXML
    private HBox ubeite;

    @FXML
    private boolean applyButton = false;
    @FXML
    private LineChart<String, Number> chart, chart2;
    @FXML
    private VBox vbox2, vbox1, vbox3, vbox4;
    @FXML
    private Label numericalsol;
    XYChart.Series<String, Number> series1 = new XYChart.Series<>();
    XYChart.Series<String, Number> series2 = new XYChart.Series<>();


    ArrayList<CheckBox> checked = new ArrayList<>();
    double in_conditions;

    List<String> equations = new ArrayList<>();
    List<Double> condit = new ArrayList<>();
    List<String> variables = new ArrayList<>();
    List<CheckBox> checkBoxes = new ArrayList<>();
    List<Boolean> bChackBoxes = new ArrayList<>();

    public Double[][] result;
    public Double[][]result1;

    ArrayList<String> boxes = new ArrayList<>();
    /**
     * Adds a formula to the list of equations and variables.
     * Called when the user clicks the "Add Formula" button.
     *
     * @param event The action event
     * @author Lera
     */
    public void addFormula(ActionEvent event){
        if(variables.isEmpty()){
            variables.add("t");
        }
        vbox1.setStyle("-fx-font-size: 27px;");
        vbox2.setStyle("-fx-font-size: 27px;");

        String inFormula = formula.getText();
        equations.add(inFormula);
        Label label = new Label(inFormula);
        vbox2.getChildren().add(label);
        formula.clear();

        String Value = formula1.getText();
        variables.add(Value);
        System.out.println(Value);

        CheckBox checkBox = new CheckBox(Value);
        checkBoxes.add(checkBox);

        boxes.add(Value);
        ubeite.getChildren().add(checkBox);


        Label label1 = new Label(Value);
        vbox1.getChildren().add(label1);
        formula1.clear();
        //System.out.print(equations);
    }
    /**
     * Adds initial conditions to the list of conditions.
     * Called when the user clicks the "Add Conditions" button.
     *
     * @param event The action event
     * @author Lera
     */
    public void addConditions(ActionEvent event){
        vbox4.setStyle("-fx-font-size: 27px;");

        String Conditions = conditions.getText();
        in_conditions = Double.parseDouble(Conditions);
        condit.add(in_conditions);
        Label label  = new Label(Conditions);
        vbox4.getChildren().add(label);
        conditions.clear();

    }
    /**
     * Applies the selected method (Euler or Runge-Kutta) to solve the ODEs.
     * Called when the user clicks the "Apply" button.
     *
     * @param event The action event
     * @author Lera
     */
    public void apply(ActionEvent event) {

        double step_size;
        int in_time;
        for (String variables:variables){
            System.out.println(variables + " ");
        }
        for (String equation:equations){
            System.out.println(equation + " ");
        }
        //System.out.print("Conditions ");
        for (double con: condit){
            //System.out.println(con + " ");
        }
        applyButton = false;
        String inputSize = size.getText();
        String inputTime = time.getText();

        try {
                step_size = Double.parseDouble(inputSize);
                System.out.println("Step size: " + step_size);
                in_time = Integer.parseInt(inputTime);
                System.out.println("Time: " + in_time);

                String[] equationsArray = equations.toArray(new String[0]);
                Double[] conditArray = condit.toArray(new Double[0]);
                double[] initialCond = new double[conditArray.length + 1];
                initialCond[0]=step_size;
                for (int i = 0 ; i < conditArray.length;i++){
                    initialCond[i+1] = conditArray[i];
                    //System.out.print(conditArray[i]);
                    //System.out.print(Arrays.toString(conditArray));
                }
                for (double value: initialCond){
                    System.out.print(value + " ");
                }

                    //Double[] initialConditions = {step_size, conditPrimitiveArray};
                    if (Euler.isSelected() ){
                        ODESolver solver = new ODESolver();
                        result = solver.euler(equationsArray, step_size, in_time, initialCond, variables);
                        System.out.print(Arrays.deepToString(result));
                        System.out.println();
                    }
            else if(Runge.isSelected()) {
               ODESolver solver2 = new ODESolver();
               result1 = solver2.rungeKutta(equationsArray, step_size, in_time, initialCond, variables);
               System.out.print(Arrays.deepToString(result1));
            }
        }
        catch (NumberFormatException ex) {
            System.out.println("Invalid input");
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}
    /**
     * Draws the selected method's solution on the chart.
     * Called when the user clicks the "Draw" button.
     *
     * @param event The action event
     * @author Lera
     */
    @FXML
    public void draw(ActionEvent event){

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        if (Euler.isSelected()) {
            series1.getData().clear();
            for (int l =0; l < checkBoxes.size();l++){
                if (checkBoxes.get(l).isSelected()){
                    bChackBoxes.add(true);
                }
                else bChackBoxes.add(false);
                }
                            System.out.println("checked ");
            /////
                            series1.setName("Euler's solver");
                            for (int j = 0; j < bChackBoxes.size(); j++) {
                                int index1 = variables.indexOf(boxes.get(j));
                                if(bChackBoxes.get(j)){
                                    for (int i = 0; i < result[0].length; i++) {
                                        series1.getData().add(new XYChart.Data<>(String.valueOf(i), result[index1][i]));
                                    }
                                }
                            }
//                            chart.getXAxis().setLabel("Time");
//                            chart.getYAxis().setLabel("Value");
                            chart.getData().clear();

                            chart.getData().add(series1);
                            bChackBoxes.clear();
                            chart.setVisible(true);
                    }

        else if (Runge.isSelected()) {
            series2.getData().clear();
            for (int l =0; l < checkBoxes.size();l++){
                if (checkBoxes.get(l).isSelected()){
                    bChackBoxes.add(true);
                }
                else bChackBoxes.add(false);
            }
            System.out.println("checked ");
            series2.setName("Runge-Kutta 4th solver");
            for (int j = 0; j < bChackBoxes.size(); j++) {
                int index1 = variables.indexOf(boxes.get(j));
                if(bChackBoxes.get(j)){
                    for (int i = 0; i < result1[0].length; i++) {
                        series2.getData().add(new XYChart.Data<>(String.valueOf(i), result1[index1][i]));
                    }
                }
            }
//            chart2.getXAxis().setLabel("Time");
//            chart2.getYAxis().setLabel("Value");
            chart2.getData().clear();
            chart2.getData().add(series2);

            bChackBoxes.clear();

            chart2.setVisible(true);
        }
    }

}
