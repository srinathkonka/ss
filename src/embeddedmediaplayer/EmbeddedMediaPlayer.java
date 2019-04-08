/*
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

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import  embeddedmediaplayer.MediaControl;



public class EmbeddedMediaPlayer extends Application {
    
    Media media;
    MediaPlayer mediaPlayer;
    MediaControl mediaControl ;
    VBox root;
    

    private static final String MEDIA_URL =
            "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";

    
    @Override
    public void start(Stage primaryStage) {
        
        
        
        primaryStage.setTitle("Embedded Media Player");
        root = new VBox();
        Scene scene = new Scene(root, 800, 800);
       
        
        
        MenuBar menuBar = new MenuBar();
        
        Menu menu = new Menu("File");
        
        MenuItem mItemOpenVid = new MenuItem("Open Video");
        menu.getItems().add(mItemOpenVid);
        
        mItemOpenVid.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File f = fileChooser.showOpenDialog(null);
                media = new Media(f.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                root.getChildren().remove(mediaControl);
                mediaControl = null;
                mediaControl = new MediaControl(mediaPlayer,f.toURI().toString());
                root.getChildren().add(mediaControl);
                
                
            }
        });    
        
        
        menu.getItems().add(new MenuItem("Open Time Listing"));
        menu.getItems().add(new MenuItem("Exit"));
        
        
        
        menuBar.getMenus().add(menu);
        
        root.getChildren().add(menuBar); 
        

        // create media player
        media = new Media (MEDIA_URL);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(false);
        
        mediaControl = new MediaControl(mediaPlayer,MEDIA_URL);
        root.getChildren().add(mediaControl);
        
        scene.setRoot(root);

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        
        
        
        primaryStage.show();
        
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
