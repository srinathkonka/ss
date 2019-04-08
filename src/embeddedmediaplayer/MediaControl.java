/*
My Dev 2-2
 * Copyright (c) 2012, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package embeddedmediaplayer;


import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import embeddedmediaplayer.Clip;
import javafx.scene.control.Tooltip;

public class MediaControl extends VBox {

    private MediaPlayer mp;
    private MediaView mediaView;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Label atPlayTime;
    
    private Slider volumeSlider;
    private HBox mediaBar;
    private HBox segmentBar;
    private GridPane gridBox;
    private TableView<Clip> table;
    private int movedToPlayTime = 0;
    private ClipBag cb;
    
    
    
    public void newMediaControl(final MediaPlayer mp,String s)
    {
        this.mp = mp;
       
    }


    public MediaControl(final MediaPlayer mp, String fileSrc) {
        this.mp = mp;
         cb = new ClipBag();
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView(mp);
        mediaView.setFitWidth(320); mediaView.setFitHeight(240); mediaView.setPreserveRatio(false);

        //mediaView.setTranslateX(mediaView.getFitWidth()  / 2 + 200); 
        //mediaView.setTranslateY(mediaView.getFitHeight() / 2 + 200);
        //mediaView.setScaleX(.5); mediaView.setScaleY(.5);
        Pane mvPane = new Pane() {
        };
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;");
        getChildren().add(mvPane);

        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);
        cb.setVideoFileLocation(fileSrc);

        final Button playButton = new Button(">");

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            //System.out.println("Trying");
            public void handle(ActionEvent e) {
                System.out.println("trying");
                Status status = mp.getStatus();

                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    System.out.println("status == Status.UNKNOWN || status == Status.HALTED");
                    return;
                }

                if (status == Status.PAUSED
                        || status == Status.READY
                        || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                    }
                    System.out.println("Should be playing");
                    mp.play();
                } else {
                    System.out.println("Should be pausing");
                    mp.pause();
                }
            }
            
            
        });
        
        

        
        
        
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run() {
                System.out.println("inside run");
                if (stopRequested) {
                    System.out.println("inside stop requested");
                    mp.pause();
                    stopRequested = false;
                } else {
                    System.out.println("inside else");
                    playButton.setText("||");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });

        mediaBar.getChildren().add(playButton);
        
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
                    int videoAt = (int) mp.getCurrentTime().toSeconds();
                    int videoLength = (int) mp.getTotalDuration().toSeconds();
                    atPlayTime.setText("" + videoAt + "s/" + videoLength + "s");
                }
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        playTime.setStyle("-fx-background-color: white");
        mediaBar.getChildren().add(playTime);
        mediaBar.setStyle("-fx-background-color: white");

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    mp.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        mediaBar.getChildren().add(volumeSlider);
        
        final Button btnNew = new Button("Create New Sub Clip");
        //creates "new" item from the current start and end
        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clip p = new Clip();
                System.out.println(mp.totalDurationProperty());
                p.setTitle("Title");
                p.setMax((int) duration.toSeconds());
                
                p.setEnd((int) mp.getCurrentTime().toSeconds());
                p.setStart((int) mp.getCurrentTime().toSeconds());
                cb.addClip(p);
            }
            });
        
        

        final Button btnStart = new Button("Set Start To Current Position");
        btnStart.setStyle("-fx-max-width:infinity");
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clip p = table.getSelectionModel().getSelectedItem();
                if (p==null) return;
                p.setStart((int)mp.getCurrentTime().toSeconds());
                doTableRefresh(table);
                
                
            }
        });
        
        
        final Button btnEnd = new Button("Set End To Current Position");
        btnEnd.setStyle("-fx-max-width:infinity");
        btnEnd.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clip p = table.getSelectionModel().getSelectedItem();
                if (p==null) return;
                p.setEnd((int)mp.getCurrentTime().toSeconds());
                doTableRefresh(table);
                
                
            }
        });
        
        //ADDITIONAL FUNCTIONALITY
        //changes the position of the master
        final Button btnNudgeBack = new Button("<<");
        btnNudgeBack.setTooltip(new Tooltip("Nudge Master Back"));
        btnNudgeBack.setStyle("-fx-max-width:infinity");
        btnNudgeBack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            }
        });
        //changes the position of the master
        final Button btnNudgeForward = new Button(">>");
        btnNudgeForward.setTooltip(new Tooltip("Nudge Master Forward"));
        btnNudgeForward.setStyle("-fx-max-width:infinity");
        btnNudgeForward.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {  
            }
        });
        
        //changes the case of the selected
        final Button btnAllCapsSelected= new Button("UPPER");
        btnAllCapsSelected.setTooltip(new Tooltip("Make Selected Item Upper Case"));
        btnAllCapsSelected.setStyle("-fx-max-width:infinity");
        btnAllCapsSelected.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            }
        });
        
        //moves the selected item bac
        final Button btnNudgeSelectedStartBack = new Button("<<");
        btnNudgeSelectedStartBack.setTooltip(new Tooltip("Nudge Selected Back"));
        btnNudgeSelectedStartBack.setStyle("-fx-max-width:infinity");
        btnNudgeSelectedStartBack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            }
        });
        final Button btnNudgeSelectedStartForward = new Button(">>");
        btnNudgeSelectedStartForward.setTooltip(new Tooltip("Nudge Selected Forward"));
        btnNudgeSelectedStartForward.setStyle("-fx-max-width:infinity");
        btnNudgeSelectedStartForward.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {  
            }
        });
        
        final Button btnAllCaps= new Button("UPPER");
        btnNudgeBack.setStyle("-fx-max-width:infinity");
        btnNudgeBack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
            }
        });
        
        
        final TextArea outputTa = new TextArea("Make Clips");
        outputTa.setStyle("-fx-max-width:infinity");
        
        final Button btnMakeClips = new Button("Make Clips");
        btnMakeClips.setStyle("-fx-max-width:infinity");
        btnMakeClips.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("Got here");
                String oput = cb.createBatchFileTxt();
                System.out.println(oput);
                outputTa.setText(oput);
                
                
            }
        });
        
        final Button btnDeleteClip = new Button("Delete");
        btnDeleteClip.setStyle("-fx-max-width:infinity");
        btnDeleteClip.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Clip p = table.getSelectionModel().getSelectedItem();
                table.getItems().remove(p);
                doTableRefresh(table);
                
                
            }
        });
        
        

        
        
        final TextField tfTitle = new TextField("Title");
   
        tfTitle.textProperty().addListener(new ChangeListener<String>() {

            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

                Clip p = table.getSelectionModel().getSelectedItem();
                p.setTitle(newValue);
                doTableRefresh(table);
            }
    });
        
        
        
        
        
        
        
        
        
        table = new TableView<Clip>();
        table.setPrefWidth(800);
        table.setEditable(true);
        TableColumn nameCol = new TableColumn("title");
        
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        nameCol.setCellValueFactory(
            new PropertyValueFactory<Clip, String>("title"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        nameCol.setOnEditCommit(
        new EventHandler<CellEditEvent<Clip, String>>() {
        @Override
        public void handle(CellEditEvent<Clip, String> t) {
            ((Clip) t.getTableView().getItems().get(
                t.getTablePosition().getRow())
                ).setTitle(t.getNewValue());
            }
        }
    );
        
        

        
        
        
        
        
        atPlayTime = new Label();
        
        atPlayTime.setText("@  ");
        
        
        TableColumn startCol = new TableColumn("start");
        startCol.setCellValueFactory(
                new PropertyValueFactory<>("start"));
        
        //startCol.setCellFactory(SpinnerCell<Clip,Integer>());
        
        
        
        
        TableColumn endCol = new TableColumn("end");  
        endCol.setCellValueFactory(
                new PropertyValueFactory<>("end"));
        
        TableColumn actionCol = new TableColumn("action");
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        
        actionCol.setCellFactory(ActionButtonTableCell.<Clip>forTableColumn("Remove", (Clip p) -> {
            table.getItems().remove(p);
            return p;
        })); 
        
        table.getColumns().addAll(nameCol,startCol,endCol,actionCol);
        table.setPrefHeight(200);
        table.setItems(cb.clipList);
        
        
        
          segmentBar = new HBox();
          gridBox = new GridPane();
          gridBox.setPadding(new Insets(10,10,10,10));
          gridBox.setStyle("-fx-background-color:pink");
          gridBox.maxWidth(Double.MAX_VALUE);
          //gridBox.setHgrow(childElementOfGridPane, Priority.ALWAYS);
          
          
          
          //gridBox.add(btnGoToStart,0,0,1,1);
          //gridBox.add(btnGoToEnd,1,0,1,1);
          
          gridBox.add(btnNew,0,1,1,1);
          
          
          //gridBox.add(tfTitle,3,1,3,1);
          gridBox.add(btnStart,4,1,1,1);
          gridBox.add(btnEnd,5,1,1,1);
          
          
          
          
          
          
          
          gridBox.add(atPlayTime,6,1,1,1);
          
          
          
          //ADDITIONAL FUNCTIONALITY
          
          //gridBox.add(btnNudgeBack,6,1,1,1);
          //gridBox.add(btnNudgeForward,6,1,1,1);
          //gridBox.add(btnAllCaps,8,1,1,1);
          //gridBox.add(btnNudgeSelectedStartBack,8,1,1,1);
          //gridBox.add(btnNudgeSelectedStartForward,8,1,1,1);
          
          
         
          
          
          
          
          
          gridBox.add(table,0,2,10,5);
          
          //gridBox.add(btnDeleteClip,0,4,1,1);
          gridBox.add(btnMakeClips,0,8,2,1);
          //gridBox.add(new Label("  "),5,0,1,1);
          
          gridBox.add(outputTa,0,9,10,3);
        
        
        
//        segmentBar = new HBox();
//        segmentBar.getStyleClass().add("white");
//        segmentBar.setSpacing(10.0);
//        segmentBar.setAlignment(Pos.TOP_RIGHT);
//        
//        segmentBar.setPadding(new Insets(5, 10, 5, 10));
//        segmentBar.getChildren().add(atPlayTime);
//        segmentBar.getChildren().add(tfTitle);
//        segmentBar.getChildren().add(btnStart);
//        segmentBar.getChildren().add(btnEnd);
//        segmentBar.getChildren().add(btnNew);
//        segmentBar.getChildren().add(table);
//        segmentBar.getChildren().add(btnGoToStart);
//        segmentBar.getChildren().add(btnGoToEnd);
//        
//        
//        segmentBar.setMinHeight(USE_PREF_SIZE);

        getChildren().add(mediaBar);
        //getChildren().add(segmentBar);
        getChildren().add(gridBox);
        
        
    }
    
    public void doTableRefresh( TableView<Clip> t)
    {
       //t.refresh();
        t.getProperties().put(TableViewSkinBase.RECREATE, Boolean.TRUE);   
    }
    
    public Duration getCurrentTime()
    {
      return mp.getCurrentTime();
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis()
                                * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume()
                                * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    
}