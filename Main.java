package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private int zaehler = -1;
    static int sleep = 0;
    TextArea area = new TextArea();

    static ImageView screen = new ImageView();
    ImageView thumb = new ImageView();
    ImageView backgroundpic = new ImageView();
    // Hintergrundbild
    Image back = new Image(getClass().getResourceAsStream("csm_Headerbild1-TH-Mittelhessen_229c11d06d.jpg"));
        //Filename werden in einer Listview eingetragen
    ListView<String> eintrag = new ListView<>();
    //VBox ist da sLayout des Programms!
    VBox steuerung = new VBox();

    //Buttons zur Steuerung die benötigt werden!
    Button load = new Button(" Upload ");
    Button next = new Button("  Next  ");
    Button prev = new Button("Previous");
    Button dia = new Button ("Diashow ");
    static Button stop = new Button("  Stop  ");
    static Button pause = new Button(" Pause ");
    static Button weiter = new Button("weiter");
    TextField zeit = new TextField();
    TextField kommentar = new TextField();
    TextArea kommSpeich = new TextArea();
    Label label = new Label();
    static ArrayList<String> pics = new ArrayList<>();
    Thread counterthread;

    @Override
    public void start(Stage primaryStage) throws Exception{

        CounterTask counterstask = new CounterTask("MA");
        kommentar.setVisible(false);
        stop.setVisible(false);
        pause.setVisible(false);
        weiter.setVisible(false);

        eintrag.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        eintrag.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            thumb.setImage(new Image(newValue));
            label.setText("");
            for(String f: pics){
                if(f == newValue){
                    screen.setImage(new Image(newValue));
                    System.out.println("Texten");
                }
            }
        });



        backgroundpic.setImage(back);
        backgroundpic.setFitWidth(1300);
        backgroundpic.setFitHeight(700);


        next.setOnAction(event -> {
            try{
                if(pics.get(zaehler+1) != null) {
                    zaehler++;
                    screen.setImage(new Image(pics.get(zaehler)));

                }
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Kein Bild mehr ");
                alert.setContentText("Keine Bilder mehr vorhanden!");
                alert.show();
            }
        });


        prev.setOnAction(e1 -> {
            try{
                if(pics.get(zaehler-1) != null){
                    zaehler--;
                    screen.setImage(new Image(pics.get(zaehler)));
                }

            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Kein Bild mehr ");
                alert.setContentText("Keine Bilder mehr vorhanden!");
                alert.show();
            }
        });

        // Imageviewer-Einstellungen
        screen.setFitHeight(500);
        screen.setFitWidth(750);

        thumb.setFitHeight(150);
        thumb.setFitWidth(150);


        //Upload Image
        load.setOnAction(e2 -> {
            FileChooser fc = new FileChooser();
            List<File> selectedFiles = fc.showOpenMultipleDialog(primaryStage);
            if(selectedFiles != null){

                for (File i : selectedFiles){
                    pics.add("file:///"+ i.getAbsolutePath());}
                for(String k: pics) {
                    eintrag.getItems().add(k);
                }
            }
            else{
                System.out.println("Selected File is not a valid Image!");
            }
            if(zaehler == -1) {
                zaehler++;
                thumb.setImage(new Image(pics.get(zaehler)));
            }
        });


        dia.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               if(zaehler == -1) {
                    Alert a1 = new Alert(Alert.AlertType.ERROR);
                    a1.setTitle("Keine Bilder");
                    a1.setContentText("Keine Bilder sind vorhanden!");
                    a1.show();
               }try{
                       sleep = Integer.parseInt(zeit.getText());
                       if(zeit.getText()!=null && sleep>=0 && sleep<=10 ){

                           counterthread = new Thread(counterstask);
                           counterthread.start();
                           stop.setVisible(true);
                           pause.setVisible(true);
                       }else if (sleep>10 || sleep<0){
                           Alert a1 = new Alert(Alert.AlertType.ERROR);
                           a1.setTitle("Sekundenfehler");
                           a1.setContentText("Bitte eine Zahl zwischen 0 - 10 eingeben");
                           a1.show();
                       }

                   }catch (Exception e){
                       Alert alert = new Alert(Alert.AlertType.ERROR);
                       alert.setTitle("Zeitfehler");
                       alert.setContentText("Bitte eine Zahl eingeben! (Sekunden)");
                       alert.show();
                       e.printStackTrace();
                   }
               }

        });

        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try{
                    counterstask.stop();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                  counterstask.setSuspend();
                  weiter.setVisible(true);
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });

        weiter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    counterstask.setResume();
                    weiter.setVisible(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        // VBOX-Eingeschaften
        steuerung.setSpacing(20);
        steuerung.setAlignment(Pos.CENTER);
        steuerung.getChildren().addAll(load,next, prev, dia, zeit, pause, stop, weiter);
        // Root-Einstellungen
        GridPane root = new GridPane();
        root.setHalignment(thumb, HPos.CENTER);
        root.setGridLinesVisible(false);
        root.setVgap(5);

        root.setHgap(30);
        root.add(steuerung,1,0);
        root.add(screen,3,0);
        root.add(kommSpeich,3,1);
        root.add(label,3,2);
        root.add(thumb,5,2);
        root.add(eintrag,5,0);

        StackPane root2 = new StackPane();
        root2.getChildren().addAll(backgroundpic, root);
        // Stage-Eigenschaften
        primaryStage.setTitle("EdelViewer");
        primaryStage.setScene(new Scene(root2, 1300, 800));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    void regisButton(Button button){
       
        button.setOnAction(new MyActionHandler());
    }

    class MyActionHandler implements  EventHandler{
        @Override
        public void handle(Event event) {
            System.out.println("AAAAAAA");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
