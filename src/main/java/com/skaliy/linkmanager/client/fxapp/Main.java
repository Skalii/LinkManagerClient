package com.skaliy.linkmanager.client.fxapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main extends Application {
    protected static Stage[] parent = new Stage[100];
    protected static int quant = 0;

    private double xOffset;
    private double yOffset;

    protected static SystemTray[] systemTray = new SystemTray[100];
    protected static TrayIcon[] trayIcon = new TrayIcon[100];
    private MenuItem menuOpen, menuExit;
    private PopupMenu popupMenu;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage previewStage = new Stage(StageStyle.UNDECORATED);
        previewStage.show();
        previewStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/preview.fxml"))));
        previewStage.centerOnScreen();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root, 750, 600));
        primaryStage.setResizable(false);
        primaryStage.show();

        int indexThisMain = quant++;
        parent[indexThisMain] = primaryStage;

        if (primaryStage.isShowing())
            previewStage.close();

        primaryStage.getScene().setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
        primaryStage.getScene().setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Platform.setImplicitExit(false);
                try {
                    systemTray[indexThisMain] = SystemTray.getSystemTray();

                    trayIcon[indexThisMain] = new TrayIcon(ImageIO.read(new File("src/main/resources/icons/system/tray.png")));
                    trayIcon[indexThisMain].addActionListener(e -> {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                primaryStage.show();
                                primaryStage.setIconified(false);
                                primaryStage.toFront();
                            }
                        });
                    });

                    menuOpen = new MenuItem("Открыть");
                    menuOpen.addActionListener(e -> {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                primaryStage.show();
                                primaryStage.setIconified(false);
                                primaryStage.toFront();
                            }
                        });
                    });

                    menuExit = new MenuItem("Выход");
                    menuExit.addActionListener(e -> System.exit(0));

                    popupMenu = new PopupMenu();
                    popupMenu.add(menuOpen);
                    popupMenu.add(menuExit);

                    trayIcon[indexThisMain].setPopupMenu(popupMenu);
                    systemTray[indexThisMain].add(trayIcon[indexThisMain]);
                } catch (IOException | AWTException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
